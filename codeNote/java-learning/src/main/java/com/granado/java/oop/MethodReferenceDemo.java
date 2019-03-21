package com.granado.java.oop;

public class MethodReferenceDemo {

  static class DemoClass2 {

    /**
     * 这里是一个实例方法, 代码上2个参数 而我们调用的时候只有一个参数
     */
    public int normalMethod(DemoClass2 this, int i) {
      return i * 2;
    }

    /*  public int normalMethod(int i) {
          return i * 2;
    }*/
  }

  public static void main(String[] args) {
    DemoClass2 demo2 = new DemoClass2();

    // 代码定义上有2个参数, 第一个参数为this
    // 但实际上调用的时候只需要一个参数
    demo2.normalMethod(1);
  }
}