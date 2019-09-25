package com.granado.java.cap;

import java.util.LinkedList;

public class Implementation1 {

    private static byte[] LOCK = new byte[0];

    private static final LinkedList<Integer> storehouse = new LinkedList<>();

    private static final int STOREHOUSE_CAPACITY = 1;

    static class ConsumerImpl extends Operator {

        @Override
        public void run() {
            while (true) {
                synchronized (LOCK) {
                    if (!storehouse.isEmpty()) {
                        System.out.println("consumer-" + id + " consume " + storehouse.pop());
                        signal();
                    } else {
                        await();
                    }
                }
            }
        }
    }

    static class ProviderImpl extends Operator {

        private int i = 0;

        @Override
        public void run() {
            while (true) {
                synchronized (LOCK) {
                    if (storehouse.size() != STOREHOUSE_CAPACITY) {
                        int a = i++;
                        storehouse.push(a);
                        System.out.println("provider-" + id + " provide " + a);
                        signal();
                    } else {
                        await();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            new Thread(new ConsumerImpl()).start();
            new Thread(new ProviderImpl()).start();
        }
    }

    private static void await() {
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {

            }
        }
    }

    private static void signal() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }
}
