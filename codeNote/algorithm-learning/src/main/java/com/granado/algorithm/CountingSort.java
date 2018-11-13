package com.granado.algorithm;

public class CountingSort {

  /*
  *  基本思想在于找出每个元素应当在数组的哪个位置
  *  1、要知道待排序数据的范围或者知道待排序数据有哪些
  *  2、遍历所有数据，计算每个数出现的次数
  *  3、计算出每个数，小于或等于它的数的个数
  *  4、从后到前的遍历，根据比不大于当前元素个数插入到数组中
  *
  *  缺点：
  *   1、如果最大值比数组长度大很多，那么 桶数组 就会很长，S(n) = O(max)
  *   2、为了保证排序的稳定性，还加了额外的辅助数组空间，S(n) = O(n)
  * */
  public static void sort(int[] array) {

    // find the max value
    int max = array[array.length - 1];
    for (int i = 0; i < array.length; i++) {
      max = array[i] > max ? array[i] : max;
    }

    // calculate times of occurrence of each element in the array
    int[] counts = new int[max + 1];
    for (int i = 0; i < array.length; i++) {
      counts[array[i]] += 1;
    }

    // calculate number of elements less than or equal each element
    for (int i = 1; i < counts.length; i++) {
      counts[i] += counts[i - 1];
    }

    int[] mirror = new int[array.length];
    for (int i = array.length - 1; i >= 0; i--) {

      int index = counts[array[i]] - 1;
      mirror[index] = array[i];
      counts[array[i]] -= 1;
    }

    System.arraycopy(mirror, 0, array, 0, mirror.length);
  }

  public static void main(String[] args) {

    int[] array = {2, 5, 3, 0, 2, 3, 0, 3};
    sort(array);
    System.out.println(ArrayUtils.printArray(array));
  }
}
