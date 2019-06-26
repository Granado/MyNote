package com.granado.leetcode;

import java.util.ArrayList;
import java.util.List;

public class RegularExpressionMatching {

    public static final class Digraph {

        private int vertex;

        private List<Integer>[] adj;

        private int edge;

        public Digraph(int v) {
            this.vertex = v;
            adj = new ArrayList[v];
            for (int i = 0; i < v; i++) {
                adj[i] = new ArrayList<>();
            }
        }

        public int getVertex() {
            return vertex;
        }

        public int getEdge() {
            return edge;
        }

        public void addEdge(int startVertex, int endVertex) {
            if (checkVertex(startVertex) || checkVertex(endVertex)) {
                return;
            }

            adj[startVertex].add(endVertex);
            edge++;
        }

        public List<Integer> adj(int v) {
            if (!checkVertex(v)) {
                return null;
            }

            return adj[v];
        }

        private boolean checkVertex(int v) {
            if (v < 0 || v >= vertex) {
                return false;
            }

            return true;
        }
    }

    private Digraph constructNFA(String pattern) {

        if (pattern == null) {
            return null;
        }

        int v = pattern.length();
        Digraph digraph = new Digraph(v + 1);  // 多一个结束状态

        for (int i = 0; i < v; i++) {

            char ch = pattern.charAt(i);

            if (ch == '*') {
                digraph.addEdge(i, i + 1);

                if (i < v - 1) {
                    digraph.addEdge(i + 1, i);
                }
            } else if (ch == '.' || (ch >= 'a' && ch <= 'z')) {
                digraph.addEdge(i, i+1);
            } else {
                throw new IllegalArgumentException("this pattern has illegal character");
            }
        }

        return digraph;
    }

    public boolean isMatch(String s, String p) {

        Digraph digraph = constructNFA(p);
        return false;
    }
}