package com.granado.leetcode;

/**
 * @Author: Granado
 * @Date: 2019-05-27 15:26
 */
public class StringToIntege {

    static long[] radix = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000L};

    public static int myAtoi(String str) {

        if (str == null || str.length() == 0) {
            return 0;
        }

        str = str.trim();
        if (str.length() == 0) {
            return 0;
        }

        boolean flag = (str.charAt(0) == '-') || (str.charAt(0) == '+');
        if (flag && str.length() == 1) {
            return 0;
        }

        int i = flag ? 1 : 0;
        while(i < str.length()) {
            char ch = str.charAt(i);
            if (ch > '9' || ch < '0') {
                break;
            }
            i++;
        }

        if ((flag && i == 1) || (!flag && i == 0)) {
            return 0;
        }

        int index = flag ? i - 1 : i;
        if (index >= radix.length && ((flag && str.charAt(1) != '0') || (!flag && str.charAt(0) != '0'))) {
            return (str.charAt(0) == '-') ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }

        long r = 0;
        for (int p = flag ? 1 : 0; p < i; p++, index--) {
            char ch = str.charAt(p);
            if (ch != '0') {
                r += (ch - '0') * radix[index - 1];
            }
        }

        if (str.charAt(0) == '-') {
            r = r * -1;
        }

        if (r >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (r <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        return (int)r;
    }

    public static void main(String[] args) {
        System.out.println(myAtoi("2147483648"));
    }
}
