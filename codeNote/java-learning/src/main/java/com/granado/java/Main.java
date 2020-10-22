package com.granado.java;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Yang Songlin
 * @Date: 2020/7/22 3:48 下午
 */
public class Main {

    private static final Pattern JSON_REG = Pattern.compile("(\\{[\\w\\W]*\\})");

    private static final Pattern IMPORT_REG = Pattern.compile("import\\([\\w\\W]*?'(.*)'[\\w\\W]*?\\)");

    public static class Line {

        private String line;

        private Integer row;

        public Line(String line, Integer row) {
            this.line = line;
            this.row = row;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public Integer getRow() {
            return row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }
    }

    public static class Text {

        public String getFile() {
            return file;
        }

        private final String file;

        private final List<Line> lines;

        public Text(String file, List<Line> lines) {
            this.file = file;
            this.lines = lines;
        }

        public Text(String file) {
            this.file = file;
            lines = new ArrayList<>();
        }

        public List<Line> getLines() {
            return lines;
        }

        public void addLine(Line line) {
            lines.add(line);
        }
    }

    public static void main(String[] args) throws Exception {
        parse();
    }

    private static Text parseByLine(File file) throws Exception {
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line = null;
            int row = 0;
            Text text = new Text(file.getAbsolutePath());
            while ((line = bufferedReader.readLine()) != null) {
                row++;
                Line textLine = new Line(line, row);
                text.addLine(textLine);
            }
            return text;
        }
    }

    private static void parse() throws Exception {
        File file = new File("/Users/user/Documents/program/workspace/admin-ui/src/router");
        List<File> js = new ArrayList<>();
        LinkedList<File> queue = new LinkedList<>();
        queue.add(file);
        while (!queue.isEmpty()) {
            File eachFile = queue.poll();
            if (eachFile.isDirectory()) {
                queue.addAll(Lists.newArrayList(eachFile.listFiles()));
            } else if (eachFile.getName().contains(".js") && !eachFile.getName().equals("index.js")) {
                js.add(eachFile);
            }
        }

        List<Text> texts = new ArrayList<>(js.size());
        for (File eachFile : js) {
            Text text = parseByLine(eachFile);
            texts.add(text);
            parseByJson(eachFile);
        }

        // System.out.println(texts);
    }

    private static void parseByJson(File eachFile) throws Exception {
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(eachFile));
        String text = new String(bytes, Charset.forName("utf-8"));
        text = text.replaceAll("//.*", "");

        Matcher matcher = JSON_REG.matcher(text);
        if (matcher.find()) {
            text = matcher.group(1);
        }

        Map<String, Object> json = JsonPath.parse(text).read("$");
        List<Map<String, Object>> children = getChildren(json);
        LinkedList<Map<String, Object>> routerQueue = new LinkedList<>();

        if (children != null && !children.isEmpty()) {
            routerQueue.addAll(children);
        }

        Map<String, Map<String, Object>> set = new HashMap<>();

        while (!routerQueue.isEmpty()) {
            Map<String, Object> each = routerQueue.poll();
            children = getChildren(each);
            if (!children.isEmpty()) {
                routerQueue.addAll(children);
            } else {
                String name = (String) each.get("name");
                if (set.containsKey(name)) {
                    System.out.println(eachFile.getName());
                    System.out.println(each.get("component") + ", " + each.get("name") + ", exists: " + set.get(name).get("component") + ", " + set.get(name).get("name"));
                } else {
                    set.put(name, each);
                }
            }
        }
    }

    private static List<Map<String, Object>> getChildren(Map<String, Object> node) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (node == null || node.isEmpty()) {
            return result;
        }

        if (node.containsKey("subTitle")) {
            List<Map<String, Object>> subTitle = (List<Map<String, Object>>) node.get("subTitle");
            for (Map<String, Object> eachTitle : subTitle) {
                result.addAll((List<Map<String, Object>>) eachTitle.get("subMenu"));
            }
        }

        if (node.containsKey("subMenu")) {
            List<Map<String, Object>> subMenus = (List<Map<String, Object>>) node.get("subMenu");
            if (subMenus != null && !subMenus.isEmpty()) {
                result.addAll(subMenus);
            }
        }

        if (node.containsKey("children")) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.get("children");
            if (children != null && !children.isEmpty()) {
                result.addAll(children);
            }
        }

        return result;
    }
}
