package com.granado.java;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ApiLimiter {

    public static class ApiAccessCount {
        private String api;

        private volatile AtomicLong count;

        public ApiAccessCount(String api) {
            this.api = api;
            count = new AtomicLong(0);
        }
    }

    public static class CountManager extends Thread {

        private ConcurrentHashMap<String, ApiAccessCount> times = new ConcurrentHashMap<>();

        private long time;

        private TimeUnit timeUnit;

        public CountManager(long time, TimeUnit timeUnit) {

            this.time = time;
            this.timeUnit = timeUnit;
            this.start();
        }

        public void register(String api, ApiAccessCount count) {

            if (!times.contains(api)) {
                times.put(api, count);
            }
        }

        @Override
        public void run() {
            while (!this.isInterrupted()) {

                for (ApiAccessCount each : times.values()) {
                    each.count.set(0);
                }
                try {
                    Thread.sleep(timeUnit.toMillis(time));
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    private volatile ConcurrentHashMap<String, ApiAccessCount> apiCountMap = new ConcurrentHashMap<>();

    private volatile CountManager countManager;

    private long countLimit;

    private long time;

    private TimeUnit timeUnit;

    public ApiLimiter(long countLimit, long time, TimeUnit timeUnit) {

        this.countLimit = countLimit;
        this.time = time;
        this.timeUnit = timeUnit;

        countManager = new CountManager(time, timeUnit);
    }

    public boolean apiRateLimit(String apiName) {

        ApiAccessCount apiAccessCount = apiCountMap.get(apiName);
        if (apiAccessCount == null) {
            synchronized (ApiAccessCount.class) {
                if (apiAccessCount == null) {
                    apiAccessCount = new ApiAccessCount(apiName);
                    countManager.register(apiName, apiAccessCount);
                    apiCountMap.put(apiName, apiAccessCount);
                }
            }
        }

        long count = apiAccessCount.count.addAndGet(1);

        if (count > countLimit) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws InterruptedException {

        ApiLimiter apiLimiter = new ApiLimiter(100, 1, TimeUnit.MINUTES);
        AtomicLong count = new AtomicLong(0);
        Runnable task = () -> {
            if (apiLimiter.apiRateLimit("test")) {
                System.out.println("count: " + count.incrementAndGet());
            } else {
                System.out.println("error");
            }
        };

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }

        Thread.sleep(TimeUnit.MINUTES.toMillis(1));

        for (int i = 0; i < 1000; i++) {
            executorService.execute(task);
        }
    }
}
