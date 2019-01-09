package com.granado.java.thread;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程中断
 * 自1.2以后，Thread.stop() 方法不推荐使用了。官方推荐使用的 interrupt()。这里顺带多说些 interrupt 方法。
 * public void interrupt()  // 中断该线程对象
 * public boolean isInterrupted() // 该线程对象的中断状态，不会清除中断标志位
 * public static boolean interrupted() // 静态方法，用来判断当前线程是否中断了，但是会清除中断标志位，下次调用就无效了。
 * <br/>
 * 中断并不意味着线程立即停止，对于运行中的线程，只会设置线程的中断标志，需要用户自己检测该标志，实现线程的中断处理。
 * 对于 WAITING 中的线程（join, sleep, wait 调用导致的）， 当它被中断时，会收到 InterruptedException 异常，并且清除中断标志
 * 可以在该异常中处理中断操作。
 * <br/>
 * 并非所有的阻塞方法都抛出 InterruptedException。
 * 输入和输出流类会阻塞等待 I/O 完成，但是它们不抛出 InterruptedException，而且在被中断的情况下也不会提前返回。
 * 然而，对于套接字 I/O，如果一个线程关闭套接字，则那个套接字上的阻塞 I/O 操作将提前结束，并抛出一个 SocketException。
 * java.nio 中的非阻塞 I/O 类也不支持可中断 I/O，但是同样可以通过关闭通道或者请求Selector上的唤醒来取消阻塞操作。
 * 类似地，尝试获取一个内部锁的操作（进入一个 synchronized 块）是不能被中断的，但是 ReentrantLock 支持可中断的获取模式。
 * <br/>
 * 对于 synchronized 导致的线程 BLOCK 状态，线程会的中断标志会为 true ，但并不影响线程执行，也不会抛出什么异常。
 * 对于 ReentrantLock，线程在竞争锁的时候是进入 WAITING 状态，如果是通过 Lock() 使线程陷入 WAITING 状态，那么对该线程进行中断
 * 并不会发生任何事情，只会设置该线程的中断标志位。待到其拿到锁后，才可以根据中断标志位处理。如果是通过lockInterruptibly()方法
 * 使线程陷入等待状态，那么当其他线程对其中断时，会抛出InterruptedException异常。
 * <p>
 * <br/>
 * LockSupport 底层是使用 Unsafe 的 park 方法，会使线程进入 WAITING 或者 WAITING_TIMED 状态。但是对于用该方法陷入等待状态的线程，
 * 只会设置其中断标志位，不会抛出异常。
 */
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
                    System.out.println("Thread interrupt state: " + Thread.currentThread().isInterrupted() + ", "
                            + Thread.currentThread().getState());
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

    public static void interruptReentrantLockThread() {
        ReentrantLock LOCK = new ReentrantLock();
        Condition condition = LOCK.newCondition();
        Thread running = new Thread(() -> {
            int interruptTimes = 0;
            while (true) {
                try {
                    System.out.println("new Thread acquire not Lock");
                    LOCK.lock(); // LOCK.lockInterruptibly();
                    System.out.println("new Thread acquire Lock");

                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("new Thread is interrupted: " + Thread.currentThread().isInterrupted());
                        System.out.println("new Thread is interrupted: " + Thread.interrupted());
                        System.out.println("new Thread is interrupted: " + Thread.interrupted());
                        condition.signalAll();
                        LOCK.unlock();
                        return;
                    }
                    Thread.sleep(1000);
                    LOCK.unlock();
                } catch (InterruptedException e) {
                    if (interruptTimes++ == 5) {
                        return;
                    }
                    e.printStackTrace();
                }
            }
        }, "target-thread");


        LOCK.lock();
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
                System.out.println("new Thread interrupt is: " + running.isInterrupted());
                condition.await();
                Thread.sleep(100);
            }
            running.interrupt();
            //thread.join();
            System.out.println("blocking的线程中断");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    public static void interruptBlockedThread() {
        Object LOCK = new Object();
        Thread running = new Thread(() -> {
            try {
                System.out.println("new Thread acquire not Lock");

                synchronized (LOCK) {
                    System.out.println("new Thread acquire Lock");
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("new Thread is interrupted: " + Thread.currentThread().isInterrupted());
                        System.out.println("new Thread is interrupted: " + Thread.interrupted());
                        System.out.println("new Thread is interrupted: " + Thread.interrupted());
                        LOCK.notify();
                        return;
                    }
                    Thread.sleep(100);
                    LOCK.notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }, "target-thread");


        try {
            synchronized (LOCK) {
                running.start();
                System.out.println("main Thread acquire Lock");
                // 等待开启的 running 线程开始执行，不加延迟，可能下面的代码跑得比较快
                Thread.sleep(100);
                for (int i = 0; i < 10 && running.isAlive(); i++) {
                    running.interrupt();
                    System.out.println("new Thread state is: " + running.getState());
                    System.out.println("new Thread interrupt is: " + running.isInterrupted());
                    //LOCK.wait();
                    Thread.sleep(1);
                }
            }
            running.join();
            System.out.println("blocking的线程中断");
        } catch (Exception e) {
            e.printStackTrace();
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

        //interruptRunningThread();

        //interruptSleepThread();

        //interruptReentrantLockThread();

        interruptBlockedThread();
    }
}
