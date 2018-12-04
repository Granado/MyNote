package com.granado.algorithm;

public class AfterClass {

  public static void chapter13(char[] chars) {

    int j = 0;
    for (int i = 0; i < chars.length; i++) {

      if (chars[i] >= 'a') {
        ArrayUtils.swap(chars, i, j);
        j++;
      }
    }
  }

  public static void main(String[] args) {

    int[] array = {1, 2, 2, 2, 3, 3, 4, 7, 7, 8, 9, 9};

    int i = 0, j = 0;
    for (; i < array.length; i++) {

      if (array[i] != array[j]) {
        array[++j] = array[i];
      }
    }

    for (int p = 0; p <= j; p++) {
      System.out.printf(array[p] + " ");
    }

    ArrayUtils.printArray(array);
  }
}
