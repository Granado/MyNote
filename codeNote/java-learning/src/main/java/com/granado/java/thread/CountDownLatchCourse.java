package com.granado.java.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static com.granado.java.utils.Utils.printInfo;

public class CountDownLatchCourse {

    public static void main(String[] args) {

        // 设置了 30 个线程，只等待 2 个线程完成
        // 抢占数量多于 CountDownLatch 初始设定的数量时，并不会有任何异常
        // 但是抢到的2个线程一旦完成，无论其他线程如何都算完成了
        final int THREAD_COUNT = 30;
        final int WAIT_THREAD_COUNT = 2;
        final CountDownLatch latch = new CountDownLatch(WAIT_THREAD_COUNT);

        final AtomicLong WAIT_TIME = new AtomicLong(1000); // initialize 1000 ms
        final long WAIT_DELTA_TIME = 500; // 500 ms

        final Runnable runnable = () -> {
            try {
                System.out.println("子线程" + Thread.currentThread().getName() + "正在执行");
                Thread.sleep(WAIT_TIME.getAndAdd(WAIT_DELTA_TIME));
                System.out.println("子线程" + Thread.currentThread().getName() + "执行完毕");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        printInfo("等待" + THREAD_COUNT + "个子线程执行完毕...", 1);

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(runnable).start();
        }

        try {
            latch.await();
            printInfo(THREAD_COUNT + "个子线程已经执行完毕", -1);
            printInfo("继续执行主线程", 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
