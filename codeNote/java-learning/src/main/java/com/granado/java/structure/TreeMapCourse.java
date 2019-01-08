package com.granado.java.structure;

import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapCourse {

    public static void main(String[] args) {
        SortedMap sortedMap = new TreeMap();
        ((TreeMap) sortedMap).subMap(1, true, 2, true);
    }
}
