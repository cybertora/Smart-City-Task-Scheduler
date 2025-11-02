package graph.scc;

import graph.Graph;
import java.util.*;

public class TarjanSCC {
    private final Graph g;
    private final int[] disc, low, stackMember, st;
    private final List<List<Integer>> scc;
    private final Deque<Integer> stack;
    private int time = 0;
    private int popCount = 0, pushCount = 0;

    public TarjanSCC(Graph g) {
        this.g = g;
        this.disc = new int[g.n]; this.low = new int[g.n];
        this.stackMember = new int[g.n];
        this.st = new int[g.n];
        this.scc = new ArrayList<>();
        this.stack = new ArrayDeque<>();
        Arrays.fill(disc, -1);
    }

    public List<List<Integer>> findSCCs() {
        for (int i = 0; i < g.n; i++) {
            if (disc[i] == -1) tarjanDFS(i);
        }
        return scc;
    }

    private void tarjanDFS(int u) {
        disc[u] = low[u] = time++;
        stack.push(u); pushCount++;
        stackMember[u] = 1;
        st[u] = 1;

        for (Graph.Edge e : g.adj.get(u)) {
            int v = e.to;
            if (disc[v] == -1) {
                tarjanDFS(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (stackMember[v] == 1) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int v = stack.pop(); popCount++;
                stackMember[v] = 0;
                component.add(v);
                if (v == u) break;
            }
            scc.add(component);
        }
    }

    public int getPopCount() { return popCount; }
    public int getPushCount() { return pushCount; }
}