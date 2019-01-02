package com.granado.algorithm;

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

  // 根据字符串hash比较的一个算法
  // 计算指数值得是否还能优化
  public static int pk(char[] text, char[] pattern) {

    if (isAnyEmpty(text, pattern) || text.length < pattern.length) {
      return -1;
    }

    int count = text.length - pattern.length + 1;
    int patternHashCode = hashCode(pattern), textHashCode = 0, pLen = pattern.length;
    for (int i = 0; i < count; i++) {

      if (textHashCode == 0) {
        textHashCode = hashCode(text, i, pattern.length);
      } else {
        textHashCode = textHashCode * 31 - (int)Math.pow(31, pLen) * text[i - 1] + text[i + pLen - 1];
      }

      if (textHashCode == patternHashCode) {
        int j = 0;
        while (j < pLen && text[j + i] == pattern[j]) j++;
        if (j == pLen) return i;
      }
    }

    return -1;
  }

  private static int hashCode(char[] text) {
    return hashCode(text, 0, text.length);
  }

  private static int hashCode(char[] text, int start, int len) {

    int h = 0;
    if (isAnyEmpty(text) || start < 0 || start > len || len < 0) {
      return h;
    }

    for (int i = start; i < text.length && i < len; i++) {
      h = h * 31 + text[i];
    }

    return h;
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

  // text, pattern 分别是主串和模式串；n, m 分别是主串和模式串的长度。
  public static int kmp(char[] text, char[] pattern) {
    int n = text.length, m = pattern.length;
    int[] next = getNext(pattern);
    int j = 0;
    for (int i = 0; i < n; ++i) {
      while (j > 0 && text[i] != pattern[j]) { // 一直找到 text[i] 和 pattern[j]
        j = next[j - 1] + 1;
      }
      if (text[i] == pattern[j]) {
        ++j;
      }
      if (j == m) { // 找到匹配模式串的了
        return i - m + 1;
      }
    }
    return -1;
  }

  // b 表示模式串，m 表示模式串的长度
  private static int[] getNext(char[] b) {
    int m = b.length, k = -1;
    int[] next = new int[m];
    next[0] = -1;
    for (int i = 1; i < m; ++i) {
      while (k != -1 && b[k + 1] != b[i]) {
        k = next[k];
      }
      if (b[k + 1] == b[i]) {
        ++k;
      }
      next[i] = k;
    }
    return next;
  }


  public static void main(String[] args) {

    String a = "ababaeabacababacd";
    String b = "ababacd";

    System.out.println(kmp(a.toCharArray(), b.toCharArray()));
  }
}
