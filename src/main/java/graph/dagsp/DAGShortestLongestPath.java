package graph.dagsp;

import graph.Graph;
import java.util.*;

public class DAGShortestLongestPath {
    private final Graph dag;
    private final List<Integer> topoOrder;
    private final long[] distShort, distLong;
    public final int[] prevShort;
    private final int[] prevLong;
    private int relaxCount = 0;

    public DAGShortestLongestPath(Graph dag, List<Integer> topoOrder) {
        this.dag = dag;
        this.topoOrder = topoOrder;
        this.distShort = new long[dag.n];
        this.distLong = new long[dag.n];
        this.prevShort = new int[dag.n];
        this.prevLong = new int[dag.n];
        Arrays.fill(distShort, Long.MAX_VALUE / 2);
        Arrays.fill(distLong, Long.MIN_VALUE / 2);
        Arrays.fill(prevShort, -1);
        Arrays.fill(prevLong, -1);
    }

    public void computeFromSource(int source) {
        distShort[source] = 0;
        distLong[source] = 0;

        for (int u : topoOrder) {
            if (distShort[u] == Long.MAX_VALUE / 2) continue;
            for (Graph.Edge e : dag.adj.get(u)) {
                relaxCount++;
                // Shortest
                if (distShort[e.to] > distShort[u] + e.weight) {
                    distShort[e.to] = distShort[u] + e.weight;
                    prevShort[e.to] = u;
                }
                // Longest
                if (distLong[e.to] < distLong[u] + e.weight) {
                    distLong[e.to] = distLong[u] + e.weight;
                    prevLong[e.to] = u;
                }
            }
        }
    }

    public long getCriticalPathLength() {
        return Arrays.stream(distLong).max().orElse(0);
    }

    public List<Integer> getCriticalPath() {
        int end = -1;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < dag.n; i++) {
            if (distLong[i] > max) {
                max = distLong[i];
                end = i;
            }
        }
        return end == -1 ? List.of() : reconstructPath(prevLong, end);
    }

    private List<Integer> reconstructPath(int[] prev, int end) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public long[] getShortestDistances() { return distShort; }
    public int getRelaxCount() { return relaxCount; }
}