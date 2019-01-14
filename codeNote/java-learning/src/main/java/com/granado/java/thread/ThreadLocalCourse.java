package com.granado.java.thread;

import com.granado.java.utils.Utils;

/***
 *
 *  关于 ThreadLocal 内存泄露的一个测试
 *  触发条件(建议 JVM Heap 参数：-Xms10m -Xmx10m)：
 *  1、在某一个线程处理流程内，且线程必须要存活，因为线程对象死亡，或者被回收，ThreadLocalMap会被释放。
 *  （因此，此问题常见在线程池环境）
 *  2、当其他地方没有引用 value 对象时，由于 ThreadLocalMap 还关联有该对象，因而不会释放，如果有大量的空间占用，
 *  就会导致内存泄漏。
 * */
public class ThreadLocalCourse {

    private static final class MemoryLeak {

        private int id;
        private int[] arr;
        private ThreadLocal<MemoryLeak> threadLocal;

        public MemoryLeak(int id) {
            this.id = id;
            arr = new int[1000000];
            threadLocal = new ThreadLocal<>();
            threadLocal.set(this);
        }

        public int getId() {
            return id;
        }

        public int[] getArr() {
            return arr;
        }

        public void printId() {
            System.out.println(id);
        }
    }

    private static final class MemoryLeakThread {

        public void run() {
            for (int i = 0; i < 1000; i++) {
                MemoryLeak t = new MemoryLeak(i);
                t.printId();
                // help GC
                t = null;
            }
        }
    }

    private static final class MemoryNotLeakThread {

        public void run() {
            for (int i = 0; i < 1000; i++) {
                MemoryLeak t = new MemoryLeak(i);
                t.printId();
                t.threadLocal.remove();
            }
        }
    }

    public static void main(String[] args) {

        new MemoryNotLeakThread().run();
        Utils.printInfo("", 10);
        new MemoryLeakThread().run();
    }
}