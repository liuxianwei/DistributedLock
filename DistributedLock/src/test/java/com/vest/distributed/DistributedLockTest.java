package com.vest.distributed;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.vest.distributed.impl.RedisDistributedLockImpl;

public class DistributedLockTest {

	private static final String key = "epay:lockTest";
	
	private int count = 0;
	
	@Test
	public void testLock() throws IOException{
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(19);  
		for(int i = 0; i < 10000; i++){
			fixedThreadPool.execute(new Task("woker_" + i));
		}
		System.in.read();
		System.out.println("count:" + count);
	}
	
	class Task implements Runnable{

		private String name;
		private DistributedLock lock;
		
		public Task(String name){
			this.name = name;
			lock = new RedisDistributedLockImpl(key);
		}
		
		public void run() {
			try{
				lock.lock();
				System.out.println(this.name + "开始工作了");
				int time = (int) (Math.random() * 5);
				count++;
				if(time == 0){
					Thread.sleep(time);
				}
				System.out.println(this.name + "结束工作了;" + System.currentTimeMillis());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				lock.unLock();
			}
		}
		
	}
}
