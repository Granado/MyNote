package com.granado.java.classloader;

import java.lang.reflect.InvocationTargetException;

public class ClassLoaderCourse {

    public static class CustomizeClassLoader extends ClassLoader {

    }

    public static void main(String[] args) {

        try {
            Class<?> stringClass = Class.forName("java.lang.String");
            char[] test = new char[]{'t', 'e', 's', 't'};
            Object str = stringClass.getDeclaredConstructor(char[].class).newInstance(test);
            System.out.println(str.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("not find class");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
