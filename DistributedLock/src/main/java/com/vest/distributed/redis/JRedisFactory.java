package com.vest.distributed.redis;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class JRedisFactory {

	private static JedisCluster jedis = null;
	
	private static String redisNodes = "192.168.1.51:7001,192.168.1.51:7002,192.168.1.51:7003,192.168.1.51:7004,192.168.1.51:7005,192.168.1.51:7006";
	
	public static JedisCluster builder(){
		if(jedis == null){
			jedis = new JedisCluster(parseHostAndPort());
		}
		return jedis;
	}
	
	private static Set<HostAndPort> parseHostAndPort(){  
		String[] nodes = redisNodes.split(",");
        
        Set<HostAndPort> haps = new HashSet<HostAndPort>();  
        for (String node : nodes) {  
            String[] ipAndPort = node.split(":");  
            HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));  
            haps.add(hap);  
        }  
        return haps;   
    } 
}
