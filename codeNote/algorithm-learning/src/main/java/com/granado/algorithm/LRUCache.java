package com.granado.algorithm;

/**
 * @Author: Yang Songlin
 * @Date: 2020/12/18 5:15 下午
 */
public class LRUCache<K, V> {

    private Node<K, V> first = null;

    private Node<K, V> tail = null;

    static class Node<K, V> {
        Node<K, V> prev;
        Node<K, V> next;
        V value;
        K key;

        public Node() {}

        public Node(K key, V data) {
            this.value = data;
            this.key = key;
        }
    }

    public Node<K, V> get(K key) {
        Node<K, V> f = findNode(key);
        if (f == null) return null;

        if (f == first) {
            return f;
        }

        if (f == tail) {
            tail = tail.prev;
            f.prev = null;
            f.next = first;
            first.prev = f;
            return f;
        }

        f.prev.next = f.next;
        f.prev.next.prev = f.prev;
        f.prev = null;
        f.next = first;
        first.prev = f;
        first = f;
        return first;
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key or value must not be null");
        }
        Node<K, V> f = findNode(key);
        if (f == null) {
            f = tail;
            Node<K, V> newNode = new Node<>(key, value);
            tail = newNode;
            if (f == null) {
                first = tail;
            } else {
                f.next = newNode;
                newNode.prev = f;
            }
        } else {
            f.value = value;
        }
    }

    private Node<K, V> findNode(K key) {
        Node<K, V> f = first;
        while (f != null && !(f.key == key || key.equals(f.key))) f = f.next;
        return f;
    }
}
