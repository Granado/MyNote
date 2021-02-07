package com.granado.java.thread;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.thread.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Yang Songlin
 * @Date: 2020/5/22 8:57 下午
 */
public class CompletableFutureCourse {

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ExecutorService executorService = Executors.newFixedThreadPool(10, new NamedThreadFactory("sync-", false));
        CompletableFuture<Long> r = CompletableFuture.completedFuture(0L);
        for (int i = 0; i < 100000; i++) {
            r = r.thenCombineAsync(CompletableFuture.supplyAsync(() -> {
                        sleep(1);
                        return 1L;
                    }, executorService), Long::sum, executorService);
        }
        Long result = r.get();
        System.out.println(result);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
