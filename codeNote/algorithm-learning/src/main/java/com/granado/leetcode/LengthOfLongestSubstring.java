package com.granado.leetcode;

/**
 * @Author: Granado
 * @Date: 2019-05-21 13:54
 *
 * 解题思路：  考虑设 S(n) 是最长不重复序列，那么当加入第 n + 1 个字符的时候，有如下情况：
 *           1、第 n+1 个字符不在 S(n) 中出现过，那么，新序列为 S(n) + charAt(n+1)，maxLength = max(length(S(n+1)), maxLength)
 *           2、第 n+1 个字符在 S(n) 中出现过，且位置为 k， 那么新序列为 S(k...n+1)，maxLength = max(maxLength, S(k...n+1))
 *           遍历每一个字符，重复如上步骤得到最大值。问题在于在已有序列中查找新字符是否存在，如下第一种方法采用循环查找，最坏的情况是 o(n^2)
 *           第二个方法采用 map 找
 *
 * 其他相关类似问题：
 *     Longest Substring with At Most Two Distinct Characters
 *     Longest Substring with At Most K Distinct Characters
 *     Subarrays with K Different Integers
 * */
public class LengthOfLongestSubstring {

    public static void main(String[] args) {
        final String source = "abcdefg";
        System.out.println(lengthOfLongestSubstring2(source));
    }

    public static int lengthOfLongestSubstring2(String s) {
        if (s == null || s.length() < 1) {
            return 0;
        }
        int[] map = new int[256];
        int max = 1, begin = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            begin = map[c] > begin ? map[c] : begin;
            max = max < (i - begin + 1) ? (i - begin + 1) : max;
            map[c] = i + 1;
        }

        return max;
    }

    public int lengthOfLongestSubstring1(String s) {
        if (s == null || s.length() < 1) {
            return 0;
        }
        int max = 1, len = 0, begin = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            for (int j = begin; j < i; j++) {
                if (c == s.charAt(j)) {
                    len = i - j - 1; // i - ( j + 1 )
                    begin = j + 1;
                    break;
                }
            }
            len++;
            max = max > len ? max : len;
        }

        return max;
    }
}
