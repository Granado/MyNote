package com.granado.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
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

    private static Digraph constructNFA(String pattern) {

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

    public static boolean isMatch(String s, String p) {

        Digraph digraph = constructNFA(p);
        List<Integer> pc = new LinkedList<>();
        DirectedDFS dfs = new DirectedDFS(digraph, 0);
        for (int v = 0; v < digraph.getVertex(); v++) {
            if (dfs.isMarked(v)) {
                pc.add(v);
            }
        }

        for (int i = 0; i < s.length(); i++) {

            List<Integer> match = new LinkedList<>();
            for (int v : pc) {
                if ( v < p.length()) {
                    if (p.charAt(v) == s.charAt(i) || p.charAt(v) == '.') {
                        match.add(v + 1);
                    }
                }
            }
            pc = new LinkedList<>();
            dfs = new DirectedDFS(digraph, match);
            for (int v = 0; v < digraph.getVertex(); v++) {
                if (dfs.isMarked(v)) {
                    pc.add(v);
                }
            }
        }

        for (int v : pc) {
            if (v == p.length()) {
                return true;
            }
        }

        return false;
    }

    // 记录从给定的起点端点能到达的端点
    public static final class DirectedDFS {

        private boolean marked[];

        public DirectedDFS(Digraph g, int startVertex) {

            marked = new boolean[g.getVertex()];
            dfs(g, startVertex);
        }

        public DirectedDFS(Digraph g, Iterable<Integer> source) {

            marked = new boolean[g.getVertex()];
            for (Integer each : source) {
                if (!marked[each]) {
                    dfs(g, each);
                }
            }
        }

        private void dfs(Digraph g, int s) {
            marked[s] = true;
            for (int each : g.adj(s)) {
                if (!marked[each]) {
                    dfs(g, each);
                }
            }
        }

        public boolean isMarked(int v) {
            return marked[v];
        }
    }

    public static void main(String[] args) {
        String text = "aab", p = "c*a*b";
        System.out.println(isMatch(text, p));
    }
}