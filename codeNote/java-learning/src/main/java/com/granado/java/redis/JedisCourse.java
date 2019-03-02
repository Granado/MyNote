package com.granado.java.redis;

import redis.clients.jedis.JedisCluster;

import java.util.BitSet;

public class JedisCourse {

    private JedisCluster jedis;

    public <T> boolean cacheObject(T object, String key, long expire) {

        return true;
    }

    public static void main(String[] args) {
        BitSet bits = new BitSet();
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[i >>> 3] & (1 << (7 - (i % 8)))) != 0) {
                bits.set(i);
            }
        }
    }
}
