package com.granado.algorithm.spa;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

// 有向有权图的邻接表表示
public class Graph {

    private LinkedList<Edge> adj[]; // 邻接表

    private int v; // 顶点个数

    public Graph(int v) {
        this.v = v;
        this.adj = new LinkedList[v];
        for (int i = 0; i < v; ++i) {
            this.adj[i] = new LinkedList<>();
        }
    }

    public void addEdge(int s, int t, int w) { // 添加一条边
        this.adj[s].add(new Edge(s, t, w));
    }

    static class Edge {
        public int sid; // 边的起始顶点编号
        public int tid; // 边的终止顶点编号
        public int w; // 权重

        public Edge(int sid, int tid, int w) {
            this.sid = sid;
            this.tid = tid;
            this.w = w;
        }
    }

    // 下面这个类是为了 dijkstra 实现用的
    static class Vertex implements Comparable<Vertex> {
        public int id; // 顶点编号 ID
        public int dist; // 从起始顶点到这个顶点的距离

        public Vertex(int id, int dist) {
            this.id = id;
            this.dist = dist;
        }

        @Override
        public int compareTo(Vertex o) {
            if (o == null) {
                return 1;
            }

            return dist - o.dist;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj == null) {
                return false;
            }

            if (obj instanceof Vertex) {
                return id == ((Vertex) obj).id;
            }

            return false;
        }
    }

    // Vertex小顶堆
    static class VertexHeap {

        public static final int DEFAULT_SIZE = 16;

        Vertex[] vertices;

        int count;

        public VertexHeap() {

            vertices = new Vertex[DEFAULT_SIZE];
            count = 0;
        }

        public VertexHeap(int size) {

            vertices = new Vertex[size < DEFAULT_SIZE ? DEFAULT_SIZE : size];
            count = 0;
        }

        public boolean update(Vertex vertex) {

            heapify();
            return true;
        }

        public Vertex poll() {

            final Vertex[] es;
            final Vertex result;

            if ((result = (es = vertices)[0]) != null) {
                final int n;
                final Vertex x = es[(n = --count)];
                es[n] = null;
                if (n > 0) {
                    siftDown(0, x, es, n);
                }
            }
            return result;
        }

        public boolean add(Vertex vertex) {
            int n = count;
            if (n >= vertices.length) {
                grow();
            }
            vertices[n] = vertex;
            siftUp(n, vertex, vertices);
            count++;
            return true;
        }

        private void heapify() {
            final Vertex[] es = vertices;
            int n = count, i = (n >>> 1) - 1;
            for (; i >= 0; i--)
                siftDown(i, es[i], es, n);
        }

        private void siftUp(int k, Vertex x, Vertex[] es) {
            Vertex key = x;
            while (k > 0) {
                int parent = (k - 1) >>> 1;
                Vertex e = es[parent];
                if (x.compareTo(e) >= 0)
                    break;
                es[k] = e;
                k = parent;
            }
            es[k] = key;
        }

        private static void siftDown(int k, Vertex x, Vertex[] es, int n) {
            // assert n > 0;
            int half = n >>> 1;
            while (k < half) {
                int child = (k << 1) + 1;
                Vertex c = es[child];
                int right = child + 1;
                if (right < n && c.compareTo(es[right]) > 0)
                    c = es[child = right];
                if (x.compareTo(c) <= 0)
                    break;
                es[k] = c;
                k = child;
            }
            es[k] = x;
        }

        public boolean isEmpty() {
            if (vertices == null || vertices.length == 0 || count == 0) {
                return true;
            }

            return false;
        }

        private void grow() {
            int oldCapacity = vertices.length;
            // Double size if small; else grow by 50%
            int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                    (oldCapacity + 2) :
                    (oldCapacity >> 1));
            vertices = Arrays.copyOf(vertices, newCapacity);
        }
    }

    public void dijkstra(int s, int t) { // 从顶点 s 到顶点 t 的最短路径
        int[] predecessor = new int[this.v]; // 用来还原最短路径
        Vertex[] vertexes = new Vertex[this.v];
        for (int i = 0; i < this.v; ++i) {
            vertexes[i] = new Vertex(i, Integer.MAX_VALUE);
        }
        VertexHeap queue = new VertexHeap(this.v);// 小顶堆
        boolean[] inQueue = new boolean[this.v]; // 标记是否进入过队列
        vertexes[s].dist = 0;
        queue.add(vertexes[s]);
        inQueue[s] = true;
        while (!queue.isEmpty()) {
            Vertex minVertex = queue.poll(); // 取堆顶元素并删除
            if (minVertex.id == t) break; // 最短路径产生了
            for (int i = 0; i < adj[minVertex.id].size(); ++i) {
                Edge e = adj[minVertex.id].get(i); // 取出一条 minVetex 相连的边
                Vertex nextVertex = vertexes[e.tid]; // minVertex-->nextVertex
                if (minVertex.dist + e.w < nextVertex.dist) { // 更新 next 的 dist
                    nextVertex.dist = minVertex.dist + e.w;
                    predecessor[nextVertex.id] = minVertex.id;
                    if (inQueue[nextVertex.id] == true) {
                        queue.update(nextVertex); // 更新队列中的 dist 值
                    } else {
                        queue.add(nextVertex);
                        inQueue[nextVertex.id] = true;
                    }
                }
            }
        }
        // 输出最短路径
        System.out.print(s);
        print(s, t, predecessor);
    }

    private void print(int s, int t, int[] predecessor) {
        if (s == t) return;
        print(s, predecessor[t], predecessor);
        System.out.print("->" + t);
    }

}
