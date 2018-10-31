
public class QuickSort {

  public static void main(String[] args) {

    int arrayLength = 1000;

    int topK = 50;
    int[] array = ArrayUtils.generateRandomArray(arrayLength);
    //int[] array = new int[] {1, 0, 0, 7, 8, 4, 9, 2, 2, 2};
    System.out.println("original:  " + ArrayUtils.printArray(array));

    int[] topKs = topK(array, topK);

    System.out.println("topKs: " + ArrayUtils.printArray(topKs));
  }



  public static int[] topK(int[] array, int k) {

    int[] result = new int[k];
    int s = 0, e = array.length - 1, p;

    while (s < e) {
      p = partition(array, s, e);
      if (p < k - 1) {
        s = p + 1;
      } else if (p > k - 1) {
        e = p - 1;
      } else if (p == k - 1) {
        break;
      }
      System.out.println("partition: " + ArrayUtils.printArray(array));
    }

    System.arraycopy(array, 0, result, 0, k);
    return result;
  }

  public static void quickSort(int[] array, int i, int j) {

    if (i < j) {
      int p = partition(array, i, j);
      quickSort(array, i, p - 1);
      quickSort(array, p + 1, j);
    }
  }

  public static int partition(int[] array, int p, int r) {
    int pivot = array[r];
    int i = p, j = p;
    for (; j < r; j++) {

      if (array[j] < pivot) {
        swap(array, i, j);
        i = i + 1;
      }
    }
    swap(array, i, r);
    return i;
  }

  public static void swap(int[] array, int a, int b) {

    if (a < array.length && b < array.length) {

      int t = array[a];
      array[a] = array[b];
      array[b] = t;
    }
  }
}
