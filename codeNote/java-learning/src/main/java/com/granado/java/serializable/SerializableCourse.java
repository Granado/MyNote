package com.granado.java.serializable;

import java.io.*;

public class SerializableCourse {

    public static class Protocol implements Serializable {

        private String name;

        private int version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }

    public static void main(String[] args) {

        final String OBJECT_FILE = "./object.dat";

        Protocol protocol = new Protocol();
        protocol.setName("test");
        protocol.setVersion(1);

        if (!isObjectFileExists(OBJECT_FILE)) {
            writeObjectToFile(protocol, OBJECT_FILE);
        }

        Protocol load = (Protocol) readObjectFromFile("E:\\program\\workspace\\MyNote\\codeNote\\object.dat");
        System.out.println(load.name + "  " + load.version);
    }

    public static boolean isObjectFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static boolean writeObjectToFile(Object object, String fileName) {

        File file = new File(fileName);
        try(FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.defaultWriteObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Object readObjectFromFile(String fileName) {

        File file = new File(fileName);
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)){
            ois.defaultReadObject();
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
