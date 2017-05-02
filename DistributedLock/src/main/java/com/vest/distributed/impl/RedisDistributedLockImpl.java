package com.vest.distributed.impl;

import com.vest.distributed.DistributedLock;
import com.vest.distributed.redis.JRedisFactory;

import redis.clients.jedis.JedisCluster;

/**
 * Redis分布式锁实现
 * @author Liuxianwei
 *
 */
public class RedisDistributedLockImpl implements DistributedLock {

	private JedisCluster redis = JRedisFactory.builder();
	
	private String key;
	private String value;
	private int seconds;
	
	public static final int DEFAULT_TIMEOUT = 3; //默认超时时间，秒为单位
	
	public RedisDistributedLockImpl(String key){
		this(key, DEFAULT_TIMEOUT);
	}
	
	public RedisDistributedLockImpl(String key, int seconds){
		this.key = key;
		this.seconds = seconds;
	}
	
	public void lock() {
		for(;;){
			value = System.currentTimeMillis() + "";
			Long result = redis.setnx(key, value);
			if(result == 1){
				break;
			}
			if(isTimeOut()){
				redis.del(key);
			}
			try {
				Thread.sleep(3L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean tryLock() {
		Long result = redis.setnx(key, value);
		if(result == 1){
			return true;
		}
		if(isTimeOut()){
			redis.del(key);
		}
		return false;
	}

	public boolean tryLock(int timeOut) {
		boolean flag = false;
		Long time = System.currentTimeMillis();
		for(;;){
			value = System.currentTimeMillis() + "";
			Long result = redis.setnx(key, value);
			if(result == 1){
				flag = true;
				break;
			}
			if(isTimeOut()){
				redis.del(key);
			}
			try {
				Thread.sleep(3L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Long now = System.currentTimeMillis();
			if((now - time) >= timeOut){
				break;
			}
		}
		return flag;
	}

	public void unLock() {
		String oldValue = redis.get(key);
		if(value.equals(oldValue)){
			redis.del(key);
		}
	}
	
	private boolean isTimeOut(){
		String oldValue = redis.get(key);
		if(oldValue == null){
			return false;
		}
		long now = System.currentTimeMillis();
		int useTime = (int) ((now - Long.parseLong(oldValue))/1000);
		if(useTime >= seconds){
			return true;
		}
		return false;
	}

}
