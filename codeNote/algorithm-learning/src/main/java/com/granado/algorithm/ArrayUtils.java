package com.granado.algorithm;

import java.lang.reflect.Array;
import java.util.Random;

public class ArrayUtils {

  // 静态绑定能区分，动态绑定会找重载方法或多态方法的参数的共同父类传入
  public static String printArray(Object obj) {

    if (obj.getClass().isArray()) {
      StringBuilder msg = new StringBuilder();
      int length = Array.getLength(obj);
      for (int i = 0; i < length; i++) {

        msg.append(Array.get(obj, i));
        if (i != length - 1) {
          msg.append(", ");
        }
      }

      return msg.toString();
    } else {
      return obj.toString();
    }
  }

  public static int[] generateRandomArray(int n) {

    if (n < 0) {
      return null;
    }

    int[] array = new int[n];
    Random random = new Random();
    for (int i = 0; i < array.length; i++) {

      array[i] = random.nextInt() % n;
      if (array[i] < 0) {
        array[i] = array[i] + n;
      }
    }

    return array;
  }

  public static void swap(Object obj, int a, int b) {

    if (obj.getClass().isArray()) {
        Object t = Array.get(obj, a);
        Array.set(obj, a, Array.get(obj, b));
        Array.set(obj, b, t);
      } else {
      throw new RuntimeException("Parameter isn't a array, not support type " + obj.getClass());
    }
  }
}
