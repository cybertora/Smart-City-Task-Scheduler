package graph.topo;

import graph.Graph;
import java.util.*;

public class KahnTopologicalSort {
    private final Graph dag;
    private final int[] indegree;
    private final List<Integer> order;
    private int relaxCount = 0;

    public KahnTopologicalSort(Graph dag) {
        this.dag = dag;
        this.indegree = new int[dag.n];
        this.order = new ArrayList<>();
        computeIndegree();
    }

    private void computeIndegree() {
        for (int u = 0; u < dag.n; u++) {
            for (Graph.Edge e : dag.adj.get(u)) {
                indegree[e.to]++;
            }
        }
    }

    public List<Integer> sort() {
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < dag.n; i++) {
            if (indegree[i] == 0) q.offer(i);
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (Graph.Edge e : dag.adj.get(u)) {
                relaxCount++;
                if (--indegree[e.to] == 0) {
                    q.offer(e.to);
                }
            }
        }
        return order.size() == dag.n ? order : null;
    }

    public int getRelaxCount() { return relaxCount; }
}