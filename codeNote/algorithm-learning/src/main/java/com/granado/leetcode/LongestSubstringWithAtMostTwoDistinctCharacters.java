package com.granado.leetcode;

/**
 * @Author: Granado
 * @Date: 2019-05-21 15:18
 *
 *     解题思路：假设最长序列为 S(n), S(n + 1), ..,  S(m)，那么第 m + 1 个字符，有如下情况：
 *             1、S(m+1) 在 S(n), S(n+1), ... , S(m) 中出现过，那么 len++， maxLength = max(len, maxLength)
 *             2、S(m+1) 在 S(n), S(n+1), ... , S(m) 中没出现过，那么有2种情况：
 *                   1）S(n) == S(n+1) == ... S(m)，即全部都是相同字符，那么第 m+1 个字符在 至多2个不同字符 的约束内，那么 
 *                      len++， maxLength = max(len, maxLength)
 *                   2）S(n) 到 S(m) 之间有2个不一样的字符了，那么第 m+1 个字符是第三种字符，这时候需要找到连续最远与S(m)相等的字符的位置，
 *                      记这个位置的字符为 S(k), len = m+1 - k + 1。 例如已找到的最长连续串是: aabbbababbbb，下个字符是 c，那么新的长度
 *                      应该为 12 - 8 + 1 = 5，
 *             以上，核心问题就在于如何快速比较一个字符是否已经在已找到的子列中出现过没，并且需要知道现有子列中，每个字符最大的位置
 */
public class LongestSubstringWithAtMostTwoDistinctCharacters {

    public static void main(String[] args) {
        String test[] = {"abc", "abb", "aabbababccbbbbcccbbb", "bbbbbb", "bbbbbbbcxxxxxxxxxxx", "eceba"};
        for (String each : test) {
            System.out.printf(lengthOfLongestSubstringTwoDistinct(each) + " ");
            System.out.printf(lengthOfLongestSubstringTwoDistinct(each) + " ");

            System.out.println();
        }
    }

    public static int lengthOfLongestSubstring(String s) {
        if (s == null) return 0;
        char[] chars = s.toCharArray();
        if (chars.length == 0 || chars.length == 1 || chars.length == 2){
            return chars.length;
        }

        char firstChar = 0, secondChar = 0;
        int firstEnd = -1, secondEnd = -1;

        int maxLength = 1, len = 0;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (firstChar != ch && secondChar != ch) {
                int index = min(firstEnd, secondEnd);
                len = i - index;
                if (firstEnd == index) {
                    firstChar = ch;
                    firstEnd = i;
                } else {
                    secondChar = ch;
                    secondEnd = i;
                }
            } else if (firstChar == ch) {
                firstEnd = i;
                len ++;
            } else if (secondChar == ch) {
                secondEnd = i;
                len ++;
            }

            maxLength = max(maxLength, len);
        }

        return maxLength;
    }

    public static int lengthOfLongestSubstringTwoDistinct(String s) {

        int left = 0, right = -1, res = 0;

        for (int i = 1; i < s.length(); ++i) {
            // 判断新的字符是否在已有子串中出现过没，根据子串至多只有2个不同字符反复出现，故而用新字符前一个字符就可以判断是否出现过
            // 相等一定出现了。
            if (s.charAt(i) == s.charAt(i - 1)) continue;

            //不相等，就看子串中是否有另一个字符与之相等，不等那么就是出现的第三个字符
            if (right >= 0 && s.charAt(right) != s.charAt(i)) {
                res = max(res, i - left);
                left = right + 1;
            }
            right = i - 1;
        }
        return max(s.length() - left, res);
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }
}
