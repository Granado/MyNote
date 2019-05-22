package com.granado.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Granado
 * @Date: 2019-05-22 11:35
 */
public class LongestSubstringWithAtMostKDistinctCharacters {

    public static void main(String[] args) {
        String test[] = {"abc", "abb", "aabbababccbbbbcccbbbxxxxxxxxxxxxxxxx", "bbbbbb", "bbbbbbbcxxxxxxxxxxx", "eceba"};
        for (String each : test) {
            System.out.printf(lengthOfLongestSubstringKDistinct(each, 0) + " ");
            System.out.println();
        }
    }

    public static int lengthOfLongestSubstringKDistinct(String s, int k) {

        if (s == null || k < 1) {
            return 0;
        }

        char[] chars = s.toCharArray();
        if (chars.length < k) {
            return chars.length;
        }

        Map<Character, Integer> map = new HashMap<>();
        int len = 0, maxLength = 1;
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (map.containsKey(ch) || map.size() < k) {
                len++;
                map.put(ch, i);
            } else if (map.size() == k){
                Map.Entry<Character, Integer> min = null;
                for (Map.Entry<Character, Integer> each : map.entrySet()) {
                    if (min == null) {
                        min = each;
                    } else if (min.getValue() > each.getValue()) {
                        min = each;
                    }
                }
                len = i - min.getValue();
                map.remove(min.getKey());
                map.put(ch, i);
            }
            maxLength = maxLength > len ? maxLength : len;
        }
        return maxLength;
    }
}
