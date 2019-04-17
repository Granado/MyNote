package com.granado.algorithm;

public class CompressString {

  public static String compress(String originalString) {

    // 2 character string is not necessary for compress
    if (originalString == null || originalString.length() < 2) {
      return originalString;
    }

    char p = 0;
    StringBuffer result = new StringBuffer();
    for (int i = 0, count = 0; i < originalString.length(); i++) {

      if (p == originalString.charAt(i)) {
        count ++;
      } else {
        p = originalString.charAt(i);
        if (count > 1) {
          result.append(count);
        }
        count = 1;
        result.append(p);
      }

      if (i == originalString.length() - 1) {
        if (count > 1) {
          result.append(count);
        }
      }
    }
    return result.toString();
  }

  public static void main(String[] args) {
    System.out.println(compress(null));
    System.out.println(compress( ""));
    System.out.println(compress("AABBCC"));
    System.out.println(compress("AAABCCDDDDE"));
    System.out.println(compress("BAAACCDDDD"));
    System.out.println(compress("AAABAACCDDDD"));
  }
}
