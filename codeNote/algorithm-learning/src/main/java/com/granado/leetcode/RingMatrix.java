package com.granado.leetcode;

import com.granado.algorithm.ArrayUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RingMatrix {

    public static <T> List<T> ringMatrixTraversal(T[][] matrix) {

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return Collections.emptyList();
        }

        LinkedList<T> result = new LinkedList<>();

        int depth = (Math.min(matrix.length, matrix[0].length) + 1) / 2; //求层数，向下取整

        for (int traversalCount = 0; traversalCount < depth; traversalCount++) {

            int col = traversalCount, row = traversalCount;

            // traversal top row
            for (; col < matrix[0].length - traversalCount - 1; col++) {
                result.add(matrix[row][col]);
            }

            // traversal right col
            for (; row < matrix.length - traversalCount - 1; row++) {
                result.add(matrix[row][col]);
            }

            // traversal bottom row
            for (; col > traversalCount; col--) {
                result.add(matrix[row][col]);
            }

            // traversal left col
            for (; row > traversalCount; row--) {
                result.add(matrix[row][col]);
            }
        }

        return result;
    }

    public static void main(String[] args) {

        Integer[][] matrix = {{1}, {8}};
        List<Integer> result = ringMatrixTraversal(matrix);
        System.out.println(ArrayUtils.printArray(result));
    }
}
