package com.granado.algorithm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;

public class StringFind {

  public static boolean isAnyEmpty(char[]... c) {
    assert c != null && c.length > 0 : "need a parameter atleast";
    for (char[] each : c) {
      if (each == null || each.length == 0) {
        return true;
      }
    }
    return false;
  }

  // 暴力搜索 O(m * n)
  public static int bf(char[] text, char[] pattern) {

    if (isAnyEmpty(text, pattern) || text.length < pattern.length) {
      return -1;
    }

    for (int i = 0, j = 0; i < text.length - pattern.length + 1; i++) {
      if (text[i] == pattern[j]) {
        for (j = 0; j < pattern.length; j++) {
          if (text[i + j] != pattern[j]) {
            return -1;
          }
        }

        return i;
      }
    }

    return -1;
  }

  public static int bm(char[] text, char[] pattern) {

    if (isAnyEmpty(text, pattern) || text.length < pattern.length) {
      return -1;
    }

    int[] badCharMap = badCharMap(pattern);

    for (int i = 0; i <= text.length - pattern.length;) {

      int j = pattern.length - 1;

      // 查找是否有不一样的字符
      while (j >= 0 && text[i + j] == pattern[j]) j--;

      // j 小于 0 表示全部匹配
      if (j < 0) return i;

      // 出现不匹配的字符根据坏字符规则跳到下一个可以匹配的位置
      i += j - badCharMap[(int)(text[i + j])];
    }

    return -1;
  }

  private static int[] badCharMap(char[] pattern) {

    int[] map = new int[256];
    for (int i = 0; i < map.length; i++) {
      map[i] = -1;
    }

    for (int i = 0; i < pattern.length; i++) {
      int index = (int) pattern[i];
      map[index] = i;
    }
    return map;
  }

  public static void main(String[] args) throws Exception {
    Field coder = String.class.getDeclaredField("coder");
    coder.setAccessible(true);


    String a = "ssssssssbsssss发货";
    String b = "bsssss";
    System.out.println(coder.get(a));
    System.out.println(coder.get(b));
    System.out.println(a.indexOf(b));
    //System.out.println(bm(a.toCharArray(), b.toCharArray()));
  }
}
