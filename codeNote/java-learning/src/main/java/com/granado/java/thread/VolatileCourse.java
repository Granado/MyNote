package com.granado.java.thread;

// 为了防止编译器和CPU进行重排序，Volatile 使用内存屏障保证可见性和防止重排序
public class VolatileCourse {

    // 重排序可能导致 x, y 为 0, 0
    public static class PossibleReordering {

        //volatile int x = 0, y = 0;
        //volatile int a = 0, b = 0;

        int x = 0, y = 0, a = 0, b = 0;

        public String run() throws Exception {
            Thread one = new Thread(() -> {
                a = 1;
                x = b;
            });

            Thread other = new Thread(() -> {
                b = 1;
                y = a;
            });
            one.start(); other.start();
            one.join(); other.join();
            return x + "," + y;
        }
    }

    public static void main(String[] args) throws Exception {

        String result;
        do {
            result = new PossibleReordering().run();
            System.out.println(result);
        } while (!"0,0".equals(result) &&  !"1,1".equals(result));
        System.out.println("reorder");
    }
}
