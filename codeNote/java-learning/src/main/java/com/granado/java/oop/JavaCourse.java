package com.granado.java.oop;

public class JavaCourse {

    public static class Base {
        {
            System.out.println("this is Base code block");
        }

        static {
            System.out.println("this is Base static code block");
        }

        static String tag = getTag();

        String t = getT();


        public Base() {
            System.out.println("this is Base constructor");
        }

        private static String getTag() {
            System.out.println("this is Base static variable");
            return "tag";
        }

        private String getT() {
            System.out.println("this is Base variable");
            return "t";
        }

    }

    public static class Son extends Base {
        {
            System.out.println("this is Son code block");
        }

        static {
            System.out.println("this is Son static code block");
        }

        static String tag = getTag();

        String t = getT();


        public Son() {
            super();
            System.out.println("this is Son constructor");
        }

        private static String getTag() {
            System.out.println("this is Son static variable");
            return "tag";
        }

        private String getT() {
            System.out.println("this is Son variable");
            return "t";
        }
    }

    public static void main(String[] args) {
        new Son();
    }
}
