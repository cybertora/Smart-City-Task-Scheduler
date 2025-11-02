package graph;

import com.google.gson.*;
import java.io.FileReader;

public class GraphReader {
    public static Graph fromJson(String path) throws Exception {
        JsonObject obj = JsonParser.parseReader(new FileReader(path)).getAsJsonObject();
        int n = obj.get("n").getAsInt();
        int source = obj.get("source").getAsInt();
        boolean directed = obj.get("directed").getAsBoolean();

        Graph g = new Graph(n, source, directed);
        JsonArray edges = obj.getAsJsonArray("edges");
        for (JsonElement e : edges) {
            JsonObject edge = e.getAsJsonObject();
            int u = edge.get("u").getAsInt();
            int v = edge.get("v").getAsInt();
            int w = edge.get("w").getAsInt();
            g.addEdge(u, v, w);
        }
        return g;
    }
}
