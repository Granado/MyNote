package com.granado.java.thread;

public class InterrupterCourse {

    // 中断运行中的线程
    public static void interruptRunningThread() {
        Thread running = new Thread(() -> {
            while (!Thread.interrupted()) {
                //todo
            }
        }, "target-thread");
        running.start();
        try {
            interruptThread(running);
            System.out.println("运行中的线程中断");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void interruptSleepThread() {
        Thread running = new Thread(() -> {
            int interruptTimes = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                    System.out.println("test");
                } catch (InterruptedException e) {
                    if (interruptTimes++ == 5) {
                        return;
                    }
                    e.printStackTrace();
                }
            }
        }, "target-thread");
        running.start();
        try {
            interruptThread(running);
            System.out.println("Sleeping的线程中断");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void interruptBlockingThread() {
        Object LOCK = new Object();
        Thread running = new Thread(() -> {
            int interruptTimes = 0;
            while (true) {
                try {
                    System.out.println("new Thread acquire not Lock");
                    synchronized (LOCK) {
                        System.out.println("new Thread acquire Lock");
                        if (Thread.interrupted()) {
                            return;
                        }
                        Thread.sleep(1000);
                        LOCK.notify();
                    }
                } catch (InterruptedException e) {
                    if (interruptTimes++ == 5) {
                        return;
                    }
                    e.printStackTrace();
                }
            }
        }, "target-thread");


        synchronized (LOCK) {
            running.start();
            try {
                System.out.println("main Thread acquire Lock");
                System.out.println("new Thread state is: " + running.getState());
                // 等待开启的 running 线程开始执行，不加延迟，可能下面的代码跑得比较快
                Thread.sleep(100);
                System.out.println("new Thread state is: " + running.getState());
                for (int i = 0; i < 1000 && running.isAlive(); i++) {
                    running.interrupt();
                    System.out.println("new Thread state is: " + running.getState());
                    LOCK.wait();
                    Thread.sleep(100);
                }
                running.interrupt();
                //thread.join();
                System.out.println("blocking的线程中断");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void interruptThread(Thread thread) throws InterruptedException {
        // 等待开启的 running 线程开始执行，不加延迟，可能下面的代码跑得比较快
        Thread.sleep(100);
        System.out.println("new Thread state is: " + thread.getState());
        for (int i = 0; i < 1000 && thread.isAlive(); i++) {
            thread.interrupt();
            System.out.println("new Thread state is: " + thread.getState());
            Thread.sleep(100);
        }
        thread.interrupt();
        //thread.join();
    }

    public static void main(String[] args) {

        interruptRunningThread();

        interruptSleepThread();

        interruptBlockingThread();
    }
}
