package com.granado.algorithm;

import java.util.HashSet;
import java.util.Set;

public class MaxSubString {

    public static int maxSubStringWithoutRepeatChar(CharSequence str) {
        if (str == null || str.length() == 0) {
            return 0;
        }

        Set<Character> chars = new HashSet<>();
        int subLength = 0;
        int max = 0;
        for (int i = 0; i < str.length(); i++) {
            for (int j = i; j < str.length(); j++) {
                char ch = str.charAt(j);
                if (!chars.contains(ch)) {
                    chars.add(ch);
                    subLength++;
                    if (max < subLength) {
                        max = subLength;
                    }
                } else {
                    subLength = 0;
                    chars.clear();
                    break;
                }
            }
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(maxSubStringWithoutRepeatChar("a"));
        System.out.println(maxSubStringWithoutRepeatChar("ac"));
        System.out.println(maxSubStringWithoutRepeatChar("abcadrghubcdeff"));
    }
}
