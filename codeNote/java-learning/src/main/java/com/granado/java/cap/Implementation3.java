package com.granado.java.cap;

import java.util.concurrent.LinkedBlockingQueue;

public class Implementation3 {

    private static final int STOREHOUSE_CAPACITY = 1000;

    private static final LinkedBlockingQueue<Long> storehouse = new LinkedBlockingQueue<>(STOREHOUSE_CAPACITY);

    static class ConsumerImpl extends Operator {

        @Override
        public void run() {
            while (true) {
                long a = 0;
                try {
                    a = storehouse.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (a % 100000000 == 0) {
                    System.out.println("consumer-" + id + " consume " + a);
                }
            }
        }
    }

    static class ProviderImpl extends Operator {

        private long i = 0;

        @Override
        public void run() {
            while (true) {
                long a = i++;
                try {
                    storehouse.put(a);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (a % 100000000 == 0) {
                    System.out.println("provider-" + id + " provide " + a);
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
