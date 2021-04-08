package com.granado.algorithm;

public class HeapSort {

    public static int[] heapSort(int[] array) {
        if (array == null) {
            return null;
        }

        for (int i = array.length / 2 - 1; i >= 0; i--) {
            adjust(array, i, array.length);
        }

        for (int i = 1; i <= array.length; i++) {
            ArrayUtils.swap(array, 0, array.length - i);
            adjust(array, 0, array.length - i);
        }

        return array;
    }

    public static void adjust(int[] array, int root, int len) {

        for (int left = root * 2 + 1; left < len;) {

            int max = array[root] > array[left] ? root : left;

            int right = left + 1;
            if (right < len && array[max] < array[right]) {
                max = right;
            }

            if (root == max) {
                break;
            }

            ArrayUtils.swap(array, root, max);
            root = max;
            left = root * 2 + 1;
        }
    }

    public static void main(String[] args) {
        int[] array = ArrayUtils.generateRandomArray(10);
        System.out.println(ArrayUtils.printArray(array));
        heapSort(array);
        System.out.println(ArrayUtils.printArray(array));
    }
}
