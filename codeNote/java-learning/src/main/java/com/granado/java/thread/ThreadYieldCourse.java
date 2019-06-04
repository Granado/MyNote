package com.granado.java.thread;

import com.granado.java.utils.Utils;

import java.util.concurrent.CountDownLatch;

/**
 *
 *  Thread.yield();
 *  建议调度器让出当前线程持有的 processor。
 *  如果让出会让给其他同等优先级的线程。但是并不能保证一定让给其他线程。
 * */
public class ThreadYieldCourse {

    static CountDownLatch countDownLatch;

    final static class Producer extends Thread {
        public void run() {
            for (int i = 0; i < 50; i++) {
                System.out.println("I am Producer : Produced Item " + i);
                Thread.yield();
            }
            countDownLatch.countDown();
        }
    }

    final static class Consumer extends Thread {
        public void run() {
            for (int i = 0; i < 50; i++) {
                System.out.println("I am Consumer : Consumed Item " + i);
                Thread.yield();
            }
            countDownLatch.countDown();
        }
    }

    public static void main(String[] args) throws Exception {
        same();
        Utils.printInfo("", 1);
        unsame();
    }

    private static void same() throws InterruptedException {
        Thread producer = new Producer();
        Thread consumer = new Consumer();
        countDownLatch = new CountDownLatch(2);

        producer.setPriority(Thread.MAX_PRIORITY);
        consumer.setPriority(Thread.MAX_PRIORITY);

        producer.start();
        consumer.start();
        countDownLatch.await();
    }

    private static void unsame() throws InterruptedException {
        Thread producer = new Producer();
        Thread consumer = new Consumer();
        countDownLatch = new CountDownLatch(2);

        producer.setPriority(Thread.MIN_PRIORITY); //Min Priority
        consumer.setPriority(Thread.MAX_PRIORITY); //Max Priority

        producer.start();
        consumer.start();
        countDownLatch.await();
    }
}