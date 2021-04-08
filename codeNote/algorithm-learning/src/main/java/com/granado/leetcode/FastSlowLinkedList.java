package com.granado.leetcode;

/**
 * @Author: Yang Songlin
 * @Date: 2021/3/4 0004 19:50
 */
public class FastSlowLinkedList {

    public static class Node<T> {
        T data;
        Node<T> next;

        public Node(T t, Node<T> next) {
            this.data = t;
            this.next = next;
        }
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

    public static <T> Node<T> cycleEntry(Node<T> root) {
        if (root == null) {
            return null;
        }

        Node<T> slow = root;
        Node<T> fast = root;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                break;
            }
        }

        if (fast == null || fast.next == null) {
            return null;
        }

        slow = root;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        return fast;
    }

    public static <T> Node<T> intersection(Node<T> a, Node<T> b) {
        if (a == null || b == null) {
            return null;
        }

        Node<T> aEnd = a;
        while (aEnd.next != null) {
            aEnd = aEnd.next;
        }

        aEnd.next = a;

        return cycleEntry(b);
    }

    public static void main(String[] args) {
        Node<Integer> r1 = new Node<>(1, new Node<>(2, new Node<>(3, null)));
        Node<Integer> r2 = r1.next;
        Node<Integer> r4 = new Node<>(4, r2);

        System.out.println(intersection(r1, r4).data);
    }
}
