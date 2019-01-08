package com.granado.java.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import static com.granado.java.reference.GCUtils.isLiving;

public class ReferenceCourse {

    public static void main(String[] args) {
        weakReference();
        System.out.println("---------------");
        softReference();
        System.out.println("---------------");
        phantomReference();
    }

    /**
     * WeakReference，当 weakReference 指向的对象没有其他任何指针引用时，GC时就会回收该对象。
     * 试想一下，如果ThreadLocalMap中不使用WeakReference，
     * 那么就让对象多了一个可达路径，导致想要回收时，还得让ThreadLocal也释放掉。
     * 而WeakReference能在其他引用不可达该对象时，即便他这里能指向对象，也会被GC回收掉。
     *
     * 1、弱引用使用 get() 方法取得对象的强引用从而访问目标对象。
     * 2、一旦系统内存回收，无论内存是否紧张，弱引用指向的对象都会被回收。
     * 3、弱引用也可以避免 Heap 内存不足所导致的异常。
     */
    public static void weakReference() {
        Object obj = new Object();
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        WeakReference<Object> weakObject = new WeakReference<>(obj, queue);
        System.out.println("before gc, this reference is soft: " + weakObject.get());
        if (GCUtils.ensureGC()) {
            System.out.println("after gc, this reference is weak: " + weakObject.get()); // not null
            System.out.println("first gc, weakObject is " + (isLiving(weakObject) ? "living" : "dead"));
        }
        obj = null;
        System.out.println("before gc, this reference is soft: " + weakObject.get());
        if (GCUtils.ensureGC()) {
            System.out.println("after gc, this reference is weak: " + weakObject.get());  // null
            System.out.println("second gc, weakObject is " + (isLiving(weakObject) ? "living" : "dead"));
        }

        System.out.println(weakObject.isEnqueued() + ", " + queue.poll());
    }

    /**
     * SoftReference，当其他地方没有任何强引用关联 softReference 所指向的对象，GC 触发时，并不一定回收该对象。
     * 具体会根据 softReference.get() 的调用情况来判定，如果后续没有地方调用该方法，那么就会被回收掉。
     *
     * 1、软引用使用 get() 方法取得对象的强引用从而访问目标对象。
     * 2、软引用所指向的对象按照 JVM 的使用情况（Heap 内存是否临近阈值）来决定是否回收。
     * 3、软引用可以避免 Heap 内存不足所导致的异常。
     */
    public static void softReference() {
        Object obj = new Object();
        SoftReference<Object> softReference = new SoftReference<>(obj);
        if (GCUtils.ensureGC()) {
            System.out.println("first gc, softReference is " + (isLiving(softReference) ? "living" : "dead"));
        }

        obj = null;
        System.out.println("before gc, this reference is soft: " + softReference.get());
        if (GCUtils.ensureGC()) {
            System.out.println("after gc, this reference is soft: " + softReference.get());
            System.out.println("second gc, softReference is " + (isLiving(softReference) ? "living" : "dead"));
        }

        obj = softReference.get();
        softReference = null;
        if (GCUtils.ensureGC()) {
            System.out.println("last gc, obj " + obj);
        }
    }

    public static void phantomReference() {
        ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
        PhantomReference<Object> referent = new PhantomReference<>(new Object(), refQueue);

        // 无论这个对象有没有被回收，phantomReference 都只返回 null
        System.out.println(referent.get());// null

        // 在 GC 回收前，queue 不会有
        System.out.println(refQueue.poll() == referent); //false

        System.gc();
        System.runFinalization();

        // GC后，并且运行runFinalization后，会进入队列
        System.out.println(refQueue.poll() == referent); //true
    }
}
