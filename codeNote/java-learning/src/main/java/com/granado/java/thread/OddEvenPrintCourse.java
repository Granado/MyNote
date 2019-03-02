package com.granado.java.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OddEvenPrintCourse {

    static final Object LOCK = new Object();

    static volatile boolean ODD_FLAG = false;

    static class Odd extends Thread {
        @Override
        public void run() {
            for (int i = 1; i < 1000; i = i + 2) {
                synchronized (LOCK) {
                    if (!ODD_FLAG) {
                        WAIT();
                    }
                    System.out.println(i);
                    ODD_FLAG = false;
                    LOCK.notify();
                }
            }
        }
    }

    public static void WAIT() {
        try {
            LOCK.wait();
        } catch (Exception e) {

        }
    }

    static class Even extends Thread {

        @Override
        public void run() {
            for (int i = 0; i <= 1000; i = i + 2) {
                synchronized (LOCK) {
                    if (ODD_FLAG) {
                        WAIT();
                    }
                    System.out.println(i);
                    ODD_FLAG = true;
                    LOCK.notify();
                }
            }
        }
    }

    public static void main(String[] args) {

        ExecutorService executorService = null;

        try {
            executorService = Executors.newFixedThreadPool(2);
            executorService.submit(new Odd());
            executorService.submit(new Even());
        } finally {
            try {
                executorService.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
