package com.granado.leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Yang Songlin
 * @Date: 2020/9/27 10:07 上午
 */
public class PathSum2 {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public List<List<Integer>> pathSum(TreeNode root, int sum) {
        if (root == null) {
            return new ArrayList<>();
        }

        // node is leaf
        if (root.left == null && root.right == null) {

            if (sum == root.val) {
                List<Integer> result = new ArrayList<>(1);
                result.add(root.val);
                List<List<Integer>> r = new ArrayList<>(1);
                r.add(result);
                return r;
            }

            return new ArrayList<>();
        }

        int rootVal = root.val;
        int subVal = sum - rootVal;
        List<List<Integer>> left = pathSum(root.left, subVal);
        List<List<Integer>> right = pathSum(root.right, subVal);

        int size = (left == null ? 0 : left.size()) + (right == null ? 0 : right.size());
        List<List<Integer>> r = new ArrayList<>(size);

        if (left != null && !left.isEmpty()) {
            left.forEach(eachList -> eachList.add(0, rootVal));
            r.addAll(left);
        }

        if (right != null && !right.isEmpty()) {
            right.forEach(eachList -> eachList.add(0, rootVal));
            r.addAll(right);
        }

        return r;
    }
}
