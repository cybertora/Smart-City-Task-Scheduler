package graph;

import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    void testTwoSCCs() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_01.json");
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> components = scc.findSCCs();

        assertEquals(3, components.size(), "Должно быть 3 SCC");
        assertTrue(components.stream().anyMatch(c -> c.contains(1) && c.contains(2) && c.contains(3)));
        assertTrue(components.stream().anyMatch(c -> c.size() == 1 && c.contains(0)));
        assertTrue(components.stream().anyMatch(c -> c.size() == 4 && c.contains(4)));
    }

    @Test
    void testSingleNodeCycle() throws Exception {
        Graph g = new Graph(1, 0, true);
        g.addEdge(0, 0, 1);
        TarjanSCC scc = new TarjanSCC(g);
        assertEquals(1, scc.findSCCs().size());
    }

    @Test
    void testDAGNoCycles() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_02.json");
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> components = scc.findSCCs();
        assertEquals(g.n, components.size(), "В DAG — по 1 вершине в SCC");
    }
}