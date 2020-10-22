package com.granado.leetcode;

/**
 * @Author: Yang Songlin
 * @Date: 2020/6/2 10:17 下午
 */
public class LongestIncreasingSubsequence {

    public static int lis(int[] array) {
        int[] dp = new int[array.length];
        dp[0] = 1;

        for (int i = 1; i < array.length; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] + 1 > dp[i] && array[i] >= array[j]) {
                    dp[i] = dp[j] + 1;
                }
            }
        }

        return dp[array.length - 1];
    }

    public static void main(String[] args) {
        System.out.println(9684921 % 19);
    }
}
