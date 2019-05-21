package com.granado.java.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Granado
 * @Date: 2019-05-16 14:23
 * <p>
 * <p>
 * 问题：当一个线程持有锁，另一个线程处于 reentrantLock.condition.await()，或者 synchronized.wait() 时，有锁线程发起 signal 或者
 * notify 时， 等待线程是否会立即持有锁。
 *
 * 结论：不会，必须在 signal 或者 notify 线程执行完了，或者await，wait时，把锁让出去，被唤醒线程才能持有锁。
 */
public class ReentrantLockOnSignal {

    static ReentrantLock lock = new ReentrantLock();

    static Condition condition = lock.newCondition();

    static Runnable r1 = () -> {
        synchronized (ReentrantLockOnSignal.class) {
            System.out.println("i hold the lock first!");
            sleep(1);
            try {
                ReentrantLockOnSignal.class.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("i work again");
            sleep(1);
        }
    };

    static Runnable r2 = () -> {
        synchronized (ReentrantLockOnSignal.class) {
            System.out.println("i hold the lock!");
            ReentrantLockOnSignal.class.notify();
            System.out.println("i'm working again");
            sleep(5);
        }
    };

    private static void sleep(long seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.printf("thread is interrupted");
        }
    }

    public static void main(String[] args) {
        String test1 = new String("test");
        String test2 = "test";
        StringBuilder builder = new StringBuilder();
        builder.append('t').append('e').append('s').append('t');
        System.out.println(test1 == test2);
        System.out.println(test1.intern() == test2.intern());
        System.out.println(test1 == test2);
        String test3 = builder.toString();
        System.out.println(test1 == test3);
        System.out.println(test1.intern());
    }
}
