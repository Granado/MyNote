import java.util.Random;

public class ArrayUtils {

  public static <T> String printArray(T[] array) {
    StringBuilder msg = new StringBuilder();
    for (int i = 0; i < array.length; i++) {

      msg.append(array[i]);
      if (i != array.length - 1) {
        msg.append(", ");
      }
    }

    return msg.toString();
  }

  public static String printArray(int[] array) {
    StringBuilder msg = new StringBuilder();
    for (int i = 0; i < array.length; i++) {

      msg.append(array[i]);
      if (i != array.length - 1) {
        msg.append(", ");
      }
    }

    return msg.toString();
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
}
