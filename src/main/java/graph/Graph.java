package graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public final int n;
    public final List<List<Edge>> adj;
    public final int source;
    public final boolean directed;

    public Graph(int n, int source, boolean directed) {
        this.n = n;
        this.source = source;
        this.directed = directed;
        this.adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int w) {
        adj.get(u).add(new Edge(v, w));
        if (!directed) {
            adj.get(v).add(new Edge(u, w));
        }
    }

    public static class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "(" + to + ", w=" + weight + ")";
        }
    }
}