package com.granado.java.thread;

public class JoinExample {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            System.out.println("First task started");
            System.out.println("Sleeping for 2 seconds");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("First task completed");
        });
        Thread t1 = new Thread(() -> {
            try {
                t.join(); // Line 16
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Second task completed");
        });
        t.start(); // Line 15

        t1.start();
    }
}