package com.granado.algorithm;

public class MergeSort {

    public static int[] mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }

        int[] temp = new int[array.length];
        merge(array, temp, 0, array.length - 1);
        return array;
    }

    public static void merge(int[] array, int[] temp, int s, int e) {
        if (s < e) {
            int mid = (s + e) / 2;
            merge(array, temp, s, mid);
            merge(array, temp, mid + 1, e);

            int n = e - s + 1;
            int i = s, j = mid + 1;
            for (int c = 0; c < n; c++) {
                if ((j > e && i <= mid) || (i <= mid && array[i] <= array[j])) {
                    temp[c] = array[i];
                    i++;
                } else if ((i > mid && j <= e) || (j <= e && array[j] <= array[i])) {
                    temp[c] = array[j];
                    j++;
                }
            }
            System.arraycopy(temp, 0, array, s, n);
        }
    }

    public static void main(String[] args) {
        int[] array = ArrayUtils.generateRandomArray(10);
        System.out.println(ArrayUtils.printArray(array));
        mergeSort(array);
        System.out.println(ArrayUtils.printArray(array));
    }
}
