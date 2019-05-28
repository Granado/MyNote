package com.granado.leetcode;

/**
 * @Author: Granado
 * @Date: 2019-05-27 15:02
 */
public class ReverseInteger {
    static int[] radix = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
    public static int reverse(int x) {
        long r = 0;
        while (x != 0) {
            r = r * 10 + x % 10;
            x = x / 10;
        }
        if (r == (int)r) {
            return (int)r;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(reverse(1534236469));
    }
}
