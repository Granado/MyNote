package com.granado.java.thread;

import cn.hutool.core.date.StopWatch;

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
        CompletableFuture<Integer> r = CompletableFuture.completedFuture(0);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            r = r.thenCombine(CompletableFuture.supplyAsync(() -> {
                sleep(1000 / (finalI + 1));
                return finalI;
            }), (a, b) -> a + b);
        }
        System.out.println(r.get());
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return ;
        }
    }

    public static void main(String[] args) {

    }
}
