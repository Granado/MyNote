package com.granado.java.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockCourse {

    public static void main(String[] args) throws Exception {
        ReentrantLock rl = new ReentrantLock();
        ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        CountDownLatch cdl = new CountDownLatch(2);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        rl.lock();

        Condition condition = rl.newCondition();
        condition.await();
        condition.signalAll();

        rl.unlock();
    }
}
