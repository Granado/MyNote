package com.granado.java.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Utils {

    public static void printInfo(String msg, int splitLineNum) {
        for (; splitLineNum < 0; splitLineNum++) {
            System.out.println("----------------------------");
        }
        System.out.println(msg);
        for (; splitLineNum > 0; splitLineNum--) {
            System.out.println("----------------------------");
        }
    }

    public static long SPIN_TIMES = 1000L;
    public static <T> T getFuture(Future<T> future, long time, TimeUnit timeUnit) throws NullPointerException {

        if (future == null) {
            throw new NullPointerException();
        }

        long spinCount = 0;
        while (!future.isCancelled() && !future.isDone() && spinCount < SPIN_TIMES) {
            spinCount++;
        }

        if (future.isCancelled()) {
            return null;
        }

        try {
            if (future.isDone()) {
                return future.get();
            }

            if (!future.isDone() && spinCount == SPIN_TIMES) {
                return future.get(time, timeUnit);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
        }

        return null;
    }

    public static <T> T getFuture(Future<T> future) {
        return getFuture(future, 0L, TimeUnit.NANOSECONDS);
    }
}
