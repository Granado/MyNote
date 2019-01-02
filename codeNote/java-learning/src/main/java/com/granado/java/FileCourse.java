package com.granado.java;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCourse {

    public static void main(String[] args) {

        File file = new File("D:\\网上下载\\test.7z");
        byte[] buffer = new byte[(int)file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int eof = fis.read(buffer);
            if (eof == -1) {
                System.out.println("file load success: " + buffer.length);
                Thread.sleep(60 * 1000 * 10);
            } else {
                System.out.println("file not load successful: " + buffer.length);
                Thread.sleep(60 * 1000 * 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final Pattern pattern = Pattern.compile("\\[.*\\] ([a-zA-Z]*[.]+?)+");

    public static void parseFileLine(String line) {

        Matcher resultMatcher = pattern.matcher(line);
        while (resultMatcher.find()) {
            System.out.println("match string: " + resultMatcher.group(0) + ", first group: " + resultMatcher.group(1));
        }
    }
}
