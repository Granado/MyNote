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

  public static void main(String[] args) {

    String textString = "";
    String patternStr = "";
    char[] text = textString.toCharArray(), pattern = patternStr.toCharArray();

    System.out.println(bf(text, pattern));
  }
}
