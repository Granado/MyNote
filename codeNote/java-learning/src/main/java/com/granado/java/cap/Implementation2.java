package com.granado.java.cap;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Implementation2 {

    private static ReentrantLock LOCK = new ReentrantLock();

    private static Condition notFull = LOCK.newCondition();

    private static Condition notEmpty = LOCK.newCondition();

    private static final LinkedList<Long> storehouse = new LinkedList<>();

    private static final int STOREHOUSE_CAPACITY = 1000;

    static class ConsumerImpl extends Operator {

        @Override
        public void run() {
            while (true) {
                LOCK.lock();
                try {
                    if (!storehouse.isEmpty()) {
                        long a = storehouse.pop();
                        if (a % 100000000 == 0) {
                            System.out.println("consumer-" + id + " consume " + a);
                        }
                        notFull.signal();
                    } else {
                        notEmpty.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }

    static class ProviderImpl extends Operator {

        private long i = 0;

        @Override
        public void run() {
            while (true) {
                LOCK.lock();
                try {
                    if (storehouse.size() != STOREHOUSE_CAPACITY) {
                        long a = i++;
                        storehouse.push(a);
                        if (a % 100000000 == 0) {
                            System.out.println("provider-" + id + " provide " + a);
                        }
                        notEmpty.signal();
                    } else {
                        notFull.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    LOCK.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < (Runtime.getRuntime().availableProcessors() >>> 1); i++) {
            new Thread(new ConsumerImpl()).start();
            new Thread(new ProviderImpl()).start();
        }
    }
}
