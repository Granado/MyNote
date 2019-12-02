package com.granado.java.structure;

import java.util.HashMap;
/**
 *  1、基本结构：数组 + 链表
 *  2、冲突解决办法：拉链法
 *  3、index，hashcode， key。位置相同，但是hashcode不一定相同，hashcode相同，但是key不一定相同。
 *  4、当 hashMap 中的元素个数 >= 64，每个位置冲突个数 >= 7，那么第8个进来就会转成红黑树。
 *  5、容量为 2^n ，初始化不为 2^n 的话，会取最靠近 2^n 的一个数。 算法 -1 >>> Integer.numberOfLeadingZeros(cap - 1)
 *  6、
 * */
public class HashMapCourse {

    public static void main(String[] args) {
        Object obj = new Object();
        System.out.println(obj);
        System.out.println(obj.hashCode());
    }

    private static void hashMapRBTreeTest() {
        int base = 64;
        Integer[] keys = generateHashIndexConflictKeys(base, 10);

        HashMap<Integer, String> a = new HashMap<>(64);
        for (Integer each : keys) {
            a.put(each, String.valueOf(each));
        }
    }
    // Integer的hashcode就是对应的int值，所以只需要是容量的倍数即可
    private static Integer[] generateHashIndexConflictKeys(int base, int num) {
        Integer[] result = new Integer[num];

        for (int i = 0; i < num; i++) {
            if (i == 0) {
                result[i] = base;
                continue;
            }

            result[i] = base + result[i - 1];
        }

        return result;
    }
}
