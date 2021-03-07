package com.granado.leetcode;

/**
 * @Author: Yang Songlin
 * @Date: 2021/3/4 0004 19:50
 */
public class FastSlowLinkedList {

    public static class Node<T> {
        T data;
        Node<T> next;
    }

    public <T> boolean isCycle(Node<T> root) {
        Node<T> fast = root;
        Node<T> slow = root;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                return true;
            }
        }
        return false;
    }
}
