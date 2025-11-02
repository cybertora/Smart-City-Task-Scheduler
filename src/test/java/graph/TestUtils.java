package graph;

import java.io.IOException;

public class TestUtils {
    public static Graph loadGraph(String filename) throws Exception {
        String path = "data/" + filename;
        return GraphReader.fromJson(path);
    }
}