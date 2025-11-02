package graph;

import graph.scc.TarjanSCC;
import graph.topo.KahnTopologicalSort;
import graph.dagsp.DAGShortestLongestPath;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String DATA_DIR = "data";
    private static final String OUTPUT_CSV = "results.csv";

    public static void main(String[] args) throws Exception {
        List<String> jsonFiles = Files.list(Paths.get(DATA_DIR))
                .filter(p -> p.toString().endsWith(".json"))
                .map(p -> p.getFileName().toString())
                .sorted()
                .collect(Collectors.toList());

        List<String> csvLines = new ArrayList<>();
        csvLines.add("File,SCCs,SCC_Sizes,Component_Order,Task_Order,Shortest_Distances,Shortest_Path,Critical_Path,Critical_Length,Time_ms");

        for (String file : jsonFiles) {
            String path = DATA_DIR + "/" + file;
            System.out.println("\n" + "=".repeat(60));
            System.out.println("Processing: " + file);
            System.out.println("=".repeat(60));

            long start = System.nanoTime();
            String result = processGraph(path);
            long timeMs = (System.nanoTime() - start) / 1_000_000;
            csvLines.add(result + "," + timeMs);
            System.out.println("Time: " + timeMs + " ms");
        }

        Files.write(Paths.get(OUTPUT_CSV), csvLines);
        System.out.println("\nAll results saved to: " + OUTPUT_CSV);
    }

    private static String processGraph(String path) throws Exception {
        Graph g = GraphReader.fromJson(path);

        TarjanSCC sccAlgo = new TarjanSCC(g);
        List<List<Integer>> scc = sccAlgo.findSCCs();
        List<String> sccStr = scc.stream()
                .map(c -> c.stream().sorted().map(Object::toString).collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.toList());
        List<Integer> sccSizes = scc.stream().map(List::size).collect(Collectors.toList());

        Graph dag = buildCondensationDAG(g, scc);
        Map<Integer, Integer> nodeToScc = new HashMap<>();
        for (int i = 0; i < scc.size(); i++) {
            for (int node : scc.get(i)) {
                nodeToScc.put(node, i);
            }
        }

        KahnTopologicalSort topo = new KahnTopologicalSort(dag);
        List<Integer> componentOrder = topo.sort();
        if (componentOrder == null) {
            throw new IllegalStateException("Condensation graph has cycle!");
        }

        List<Integer> taskOrder = new ArrayList<>();
        for (int comp : componentOrder) {
            List<Integer> nodes = new ArrayList<>(scc.get(comp));
            nodes.sort(Integer::compareTo);
            taskOrder.addAll(nodes);
        }

        DAGShortestLongestPath pathAlgo = new DAGShortestLongestPath(dag, componentOrder);
        int sourceScc = nodeToScc.get(g.source);
        pathAlgo.computeFromSource(sourceScc);

        long[] dist = pathAlgo.getShortestDistances();
        String distStr = Arrays.stream(dist)
                .mapToObj(d -> d == Long.MAX_VALUE / 2 ? "∞" : String.valueOf(d))
                .collect(Collectors.joining(",", "[", "]"));

        int lastScc = componentOrder.get(componentOrder.size() - 1);
        List<Integer> shortPathScc = reconstructPath(pathAlgo.prevShort, lastScc);
        List<Integer> shortPathNodes = shortPathScc.stream()
                .flatMap(i -> scc.get(i).stream().sorted())
                .collect(Collectors.toList());
        String shortPathStr = shortPathNodes.stream()
                .map(Object::toString)
                .collect(Collectors.joining("→"));

        List<Integer> critPathScc = pathAlgo.getCriticalPath();
        List<Integer> critPathNodes = critPathScc.stream()
                .flatMap(i -> scc.get(i).stream().sorted())
                .collect(Collectors.toList());
        String critPathStr = critPathNodes.stream()
                .map(Object::toString)
                .collect(Collectors.joining("→"));
        long critLength = pathAlgo.getCriticalPathLength();

        System.out.println("SCC: " + sccStr);
        System.out.println("Sizes: " + sccSizes);
        System.out.println("Component Order: " + componentOrder);
        System.out.println("Task Order: " + taskOrder);
        System.out.println("Shortest Dist (from source SCC): " + distStr);
        System.out.println("Shortest Path (to last): " + shortPathStr);
        System.out.println("Critical Path: " + critPathStr);
        System.out.println("Critical Length: " + critLength);

        return String.format("%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d",
                path.substring(path.lastIndexOf('/') + 1),
                String.join(";", sccStr),
                sccSizes,
                componentOrder,
                taskOrder,
                distStr,
                shortPathStr,
                critPathStr,
                critLength
        );
    }

    private static Graph buildCondensationDAG(Graph g, List<List<Integer>> scc) {
        int n = scc.size();
        Graph dag = new Graph(n, 0, true);
        Map<Integer, Integer> nodeToScc = new HashMap<>();
        for (int i = 0; i < scc.size(); i++) {
            for (int node : scc.get(i)) {
                nodeToScc.put(node, i);
            }
        }
        Set<String> edgeSet = new HashSet<>();
        for (int u = 0; u < g.n; u++) {
            int sccU = nodeToScc.get(u);
            for (Graph.Edge e : g.adj.get(u)) {
                int sccV = nodeToScc.get(e.to);
                if (sccU != sccV) {
                    String key = sccU + "->" + sccV;
                    if (edgeSet.add(key)) {
                        dag.addEdge(sccU, sccV, e.weight);
                    }
                }
            }
        }
        return dag;
    }

    private static List<Integer> reconstructPath(int[] prev, int end) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}