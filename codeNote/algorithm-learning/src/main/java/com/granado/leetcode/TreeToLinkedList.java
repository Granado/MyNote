package com.granado.leetcode;

public class TreeToLinkedList {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
    }

    public static TreeNode flattenRecursive(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode left = flattenRecursive(root.left);
        TreeNode right = flattenRecursive(root.right);

        if (left != null) {
            root.right = left;
            root.left = null;
        }

        while (left != null && left.right != null) {
            left = left.right;
        }

        if (left != null) {
            left.right = right;
        }

        return root;
    }

    public static TreeNode flatten(TreeNode root) {
        if (root == null) {
            return null;
        }

        TreeNode r = root;
        while (r != null) {
            TreeNode p = r.left;
            if (p != null) {
                while (p.right != null) {
                    p = p.right;
                }
                p.right = r.right;
                r.right = r.left;
                r.left = null;
            }

            r = r.right;
        }

        return root;
    }
}
