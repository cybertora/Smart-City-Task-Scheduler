package graph.scc;

import graph.Graph;
import java.util.*;

public class TarjanSCC {
    private final Graph g;
    private int time = 0;
    public int pushCount = 0;
    public int popCount = 0;

    public TarjanSCC(Graph g) {
        this.g = g;
    }

    public List<List<Integer>> findSCCs() {
        List<List<Integer>> sccs = new ArrayList<>();
        int[] disc = new int[g.n];
        int[] low = new int[g.n];
        boolean[] onStack = new boolean[g.n];
        Deque<Integer> stack = new ArrayDeque<>();
        Arrays.fill(disc, -1);

        for (int i = 0; i < g.n; i++) {
            if (disc[i] == -1) {
                dfs(i, disc, low, onStack, stack, sccs);
            }
        }

        // Нормализация: сортировка внутри и по первой вершине
        sccs.forEach(comp -> comp.sort(Integer::compareTo));
        sccs.sort((a, b) -> Integer.compare(a.get(0), b.get(0)));
        return sccs;
    }

    private void dfs(int u, int[] disc, int[] low, boolean[] onStack, Deque<Integer> stack, List<List<Integer>> sccs) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        pushCount++;

        for (Graph.Edge e : g.adj.get(u)) {
            int v = e.to;
            if (disc[v] == -1) {
                dfs(v, disc, low, onStack, stack, sccs);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // Если u — корень SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            while (true) {
                int v = stack.pop();
                onStack[v] = false;
                popCount++;
                component.add(v);
                if (v == u) break;
            }
            sccs.add(component);
        }
    }
}