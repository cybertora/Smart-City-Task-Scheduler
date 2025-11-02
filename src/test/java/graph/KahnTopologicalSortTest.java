package graph;

import graph.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class KahnTopologicalSortTest {

    @Test
    void testValidDAG() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_02.json");
        KahnTopologicalSort topo = new KahnTopologicalSort(g);
        List<Integer> order = topo.sort();
        assertNotNull(order);
        assertEquals(g.n, order.size());
        assertTrue(isValidTopoOrder(g, order));
    }

    @Test
    void testCycleReturnsNull() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_01.json");
        KahnTopologicalSort topo = new KahnTopologicalSort(g);
        assertNull(topo.sort(), "Граф с циклом → null");
    }

    private boolean isValidTopoOrder(Graph g, List<Integer> order) {
        int[] pos = new int[g.n];
        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }
        for (int u = 0; u < g.n; u++) {
            for (Graph.Edge e : g.adj.get(u)) {
                if (pos[u] >= pos[e.to]) return false;
            }
        }
        return true;
    }
}