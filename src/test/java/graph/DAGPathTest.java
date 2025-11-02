package graph;

import graph.dagsp.DAGShortestLongestPath;
import graph.topo.KahnTopologicalSort;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DAGPathTest {

    @Test
    void testShortestAndLongestPath() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_02.json");
        KahnTopologicalSort topo = new KahnTopologicalSort(g);
        List<Integer> order = topo.sort();
        assertNotNull(order, "Граф должен быть DAG");

        DAGShortestLongestPath path = new DAGShortestLongestPath(g, order);
        path.computeFromSource(g.source);

        long[] dist = path.getShortestDistances();

        assertArrayEquals(new long[]{
                0,
                5,
                3,
                5,
                9,
                10
        }, dist);

        assertEquals(16, path.getCriticalPathLength());
        List<Integer> critPath = path.getCriticalPath();
        assertEquals(List.of(0, 1, 3, 4, 5), critPath);
    }

    @Test
    void testLargeDAG() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_09.json");
        KahnTopologicalSort topo = new KahnTopologicalSort(g);
        List<Integer> order = topo.sort();
        assertNotNull(order);
        assertEquals(g.n, order.size());
    }
}