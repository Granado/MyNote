package com.granado.java.thread;

public class ThreadStateCourse {

    // A thread that has not yet started is in this state.
    Thread.State NEW = Thread.State.NEW; // new Thread(() -> {...})

    // A thread executing in the Java virtual machine is in this state.
    Thread.State RUNNABLE = Thread.State.RUNNABLE; // new Thread(() -> {...}).start();

    // A thread that is blocked waiting for a monitor lock is in this state.
    Thread.State BLOCKED = Thread.State.BLOCKED; // reentrantLock.lock(); synchronized (obj) {...}

    // A thread that is waiting indefinitely for another thread to perform a particular action is in this state.
    Thread.State WAITING = Thread.State.WAITING; // obj.wait(); condition.await();

    // A thread that is waiting for another thread to perform an action for up to a specified waiting time is in this state.
    Thread.State TIMED_WAITING = Thread.State.TIMED_WAITING; // obj.wait(time); condition.await(time)

    // A thread that has exited is in this state.
    Thread.State TERMINATED = Thread.State.TERMINATED;

    // 初始状态为 NEW ，中止状态为 TERMINATED
    // BLOCKED 和 WAITING 的区别在于 BLOCKED 是等待锁，WAITING 是线程间都已经拿到过锁了，
    // 但是在执行的时候通过 wait() 方法放弃时间片，等待其他线程执行。
}
