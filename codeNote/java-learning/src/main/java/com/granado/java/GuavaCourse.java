package com.granado.java;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class GuavaCourse {

    public static void main(String[] args) {
        Table<Long, String, Integer> table = TreeBasedTable.create();
        table.put(1L, "math", 1);
        table.put(1L, "language", 2);
        table.put(2L, "math", 1);
        System.out.println(table.rowMap());
    }
}
