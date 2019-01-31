package com.granado.java.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 无锁版的2个线程交替打印1~999
 * */
public class OddEvenPrintNoLockCourse {

    static volatile boolean ODD_FLAG = true;

    static class Odd extends Thread {
        @Override
        public void run() {
            int i = 1;
            while(i < 1000) {
                if (ODD_FLAG) {
                    System.out.println(i);
                    i += 2;
                    ODD_FLAG = !ODD_FLAG;
                }
            }
        }
    }

    static class Even extends Thread {

        @Override
        public void run() {
            int i = 2;
            while(i <= 1000) {
                if (!ODD_FLAG) {
                    System.out.println(i);
                    i += 2;
                    ODD_FLAG = !ODD_FLAG;
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
