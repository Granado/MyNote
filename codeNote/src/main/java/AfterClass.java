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

    char[] chars = {'D', 'a', 'F', 'B', 'c', 'A', 'z'};
    chapter13(chars);
    System.out.println(ArrayUtils.printArray(chars));
  }
}
