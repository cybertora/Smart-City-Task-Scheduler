package graph;

import graph.scc.TarjanSCC;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {

    @Test
    void testTwoSCCs() throws Exception {
        Graph g = TestUtils.loadGraph("tasks_01.json");
        TarjanSCC scc = new TarjanSCC(g);
        List<List<Integer>> components = scc.findSCCs();

        List<List<Integer>> normalized = components.stream()
                .map(list -> list.stream().sorted().collect(Collectors.toList()))
                .sorted((a, b) -> Integer.compare(a.get(0), b.get(0)))
                .collect(Collectors.toList());

        List<List<Integer>> expected = List.of(
                List.of(0),
                List.of(1, 2, 3),
                List.of(4, 5, 6, 7)
        );

        assertEquals(expected, normalized, "SCC не совпадают по содержимому");
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
        assertEquals(g.n, components.size());
        components.forEach(c -> assertEquals(1, c.size()));
    }
}