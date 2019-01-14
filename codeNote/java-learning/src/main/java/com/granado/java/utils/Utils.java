package com.granado.java.utils;

public class Utils {

    public static void printInfo(String msg, int splitLineNum) {
        for (; splitLineNum < 0; splitLineNum++) {
            System.out.println("----------------------------");
        }
        System.out.println(msg);
        for (; splitLineNum > 0; splitLineNum--) {
            System.out.println("----------------------------");
        }
    }
}
