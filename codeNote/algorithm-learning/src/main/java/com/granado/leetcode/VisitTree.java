package com.granado.leetcode;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @Author: Yang Songlin
 * @Date: 2020/5/17 10:40 上午
 */
public class VisitTree {
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
    }

    public static void preOrderTravel(TreeNode p) {
        if (p != null) {
            print(p);
            preOrderTravel(p.left);
            preOrderTravel(p.right);
        }
    }

    public static void inOrderTravel(TreeNode p) {
        if (p != null) {
            inOrderTravel(p.left);
            print(p);
            inOrderTravel(p.right);
        }
    }

    public static void lastOrderTravel(TreeNode p) {
        if (p != null) {
            lastOrderTravel(p.left);
            lastOrderTravel(p.right);
            print(p);
        }
    }

    public static void preOrderTravelNonRecursive(TreeNode p) {
        if (p == null) {
            return;
        }

        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(p);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            print(node);
            if (node.right != null) {
                stack.push(node.right);
            }

            if (node.left != null) {
                stack.push(node.left);
            }
        }
    }

    public static void inOrderTravelNonRecursive(TreeNode root) {
        if (root == null) {
            return;
        }

        TreeNode p = root;
        Deque<TreeNode> stack = new LinkedList<>();
        while (!stack.isEmpty() || p != null) {
            if (p != null) {
                stack.push(p);
                p = p.left;
            } else {
                p = stack.pop();
                print(p);
                p = p.right;
            }
        }
    }

    public static void lastOrderTravelNonRecursive(TreeNode root) {
        if (root == null) {
            return;
        }

        Deque<TreeNode> stack = new LinkedList<>();
        Deque<TreeNode> help = new LinkedList<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            help.push(node);
            if (node.left != null)
                stack.add(node.left);
            if (node.right != null)
                stack.add(node.right);
        }

        while (!help.isEmpty()) {
            print(help.pop());
        }
    }

    public static void print(TreeNode p) {
        if (p != null) {
            System.out.println(p.val);
        }
    }
}
