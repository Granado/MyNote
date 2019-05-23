package com.granado.leetcode;

/**
 * @Author: Granado
 * @Date: 2019-05-23 15:47
 */
public class LongestPalindromicSubstring {

    public static void main(String[] args) {
        System.out.println(longestPalindrome("abcba"));
    }

    public static String longestPalindrome(String s) {
        if (s == null) {
            return "";
        }

        if (s.length() <= 1) {
            return s;
        }

        int maxLength = 0, left = 0, right = 1;
        // i 为对称轴
        for (int i = 0; i < (s.length() << 1) - 1; i++) {

            int l,r;
            if ((i & 0x00000001) == 1) {
                l = (i - 1) >> 1;
                r = l + 1;//(i + 1) >> 2;
            } else {
                l = (i >> 1) - 1;
                r = l + 2;//(i >> 2) + 1;
            }

            while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {

                if (maxLength < (r - l)) {
                    maxLength = r - l;
                    left = l;
                    right = r + 1;
                }

                l--;
                r++;
            }
        }

        return s.substring(left, right);
    }
}
