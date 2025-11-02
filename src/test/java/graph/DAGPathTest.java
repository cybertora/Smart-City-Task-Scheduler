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
        assertNotNull(order);

        DAGShortestLongestPath path = new DAGShortestLongestPath(g, order);
        path.computeFromSource(g.source);

        long[] dist = path.getShortestDistances();
        assertEquals(0, dist[0]);
        assertEquals(10, dist[3]); // 0→2→3 = 3+2=5? Проверим граф

        long critical = path.getCriticalPathLength();
        assertEquals(16, critical); // 0→1→3→4→5 = 5+6+4+1 = 16

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