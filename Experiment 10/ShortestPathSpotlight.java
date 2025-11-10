import java.util.*;

public class ShortestPathSpotlight {

    // Inner class to represent an edge
    static class Edge {
        int source;
        int destination;
        int weight;

        public Edge(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    // Inner class to represent a node in the priority queue for Dijkstra
    static class Node implements Comparable<Node> {
        int id;
        int distance;

        public Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    // Graph representation
    static class Graph {
        int vertices;
        List<List<Edge>> adjacencyList;
        List<Edge> allEdges; // For Bellman-Ford

        public Graph(int vertices) {
            this.vertices = vertices;
            this.adjacencyList = new ArrayList<>(vertices);
            for (int i = 0; i < vertices; i++) {
                this.adjacencyList.add(new ArrayList<>());
            }
            this.allEdges = new ArrayList<>();
        }

        public void addEdge(int source, int destination, int weight) {
            Edge edge = new Edge(source, destination, weight);
            adjacencyList.get(source).add(edge);
            allEdges.add(edge);
            // For undirected graph, add reverse edge too
             Edge reverseEdge = new Edge(destination, source, weight);
             adjacencyList.get(destination).add(reverseEdge);
             allEdges.add(reverseEdge);
        }
    }

    // =========================================
    // 1. Dijkstra's Algorithm Implementation
    // =========================================
    public static Result dijkstra(Graph graph, int startNode, int endNode) {
        int[] distances = new int[graph.vertices];
        int[] predecessors = new int[graph.vertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);

        distances[startNode] = 0;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(startNode, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;

            if (u == endNode) break; // Optimization: stop if end node reached

            if (current.distance > distances[u]) continue;

            for (Edge edge : graph.adjacencyList.get(u)) {
                int v = edge.destination;
                int weight = edge.weight;

                if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    predecessors[v] = u;
                    pq.add(new Node(v, distances[v]));
                }
            }
        }

        return new Result(reconstructPath(predecessors, startNode, endNode), distances[endNode]);
    }

    // =========================================
    // 2. Bellman-Ford Algorithm Implementation
    // =========================================
    public static Result bellmanFord(Graph graph, int startNode, int endNode) {
        int[] distances = new int[graph.vertices];
        int[] predecessors = new int[graph.vertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);

        distances[startNode] = 0;

        // Relax edges |V| - 1 times
        for (int i = 1; i < graph.vertices; ++i) {
            for (Edge edge : graph.allEdges) {
                int u = edge.source;
                int v = edge.destination;
                int weight = edge.weight;
                if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    predecessors[v] = u;
                }
            }
        }

        // Check for negative weight cycles
        for (Edge edge : graph.allEdges) {
             int u = edge.source;
             int v = edge.destination;
             int weight = edge.weight;
             if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                 return new Result(new ArrayList<>(), Integer.MIN_VALUE);
             }
        }

        return new Result(reconstructPath(predecessors, startNode, endNode), distances[endNode]);
    }

    // Helper to reconstruct path from predecessors array
    private static List<Integer> reconstructPath(int[] predecessors, int startNode, int endNode) {
        List<Integer> path = new LinkedList<>();
        int current = endNode;
        while (current != -1) {
            path.add(0, current);
            if (current == startNode) break;
            current = predecessors[current];
        }
        if (path.size() == 0 || path.get(0) != startNode) {
            return new ArrayList<>(); // Path not found
        }
        return path;
    }

    // Helper class to store algorithm results
    static class Result {
        List<Integer> path;
        int distance;

        public Result(List<Integer> path, int distance) {
            this.path = path;
            this.distance = distance;
        }
    }

    // =========================================
    // 3. Main Driver Code
    // =========================================
    public static void main(String[] args) {
        System.out.println("--- Experiment 10: Shortest Path Spotlight (Java) ---");

        int numNodes = 10;
        Graph graph = new Graph(numNodes);

        // Creating a specific graph for reproducible results
        // Node 0 is start, Node 9 is end
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 7, 8);
        graph.addEdge(1, 2, 8);
        graph.addEdge(1, 7, 11);
        graph.addEdge(2, 3, 7);
        graph.addEdge(2, 8, 2);
        graph.addEdge(2, 5, 4);
        graph.addEdge(3, 4, 9);
        graph.addEdge(3, 5, 14);
        graph.addEdge(4, 5, 10);
        graph.addEdge(5, 6, 2);
        graph.addEdge(6, 7, 1);
        graph.addEdge(6, 8, 6);
        graph.addEdge(7, 8, 7);
        // Connecting to node 9
        graph.addEdge(8, 9, 10);
        graph.addEdge(4, 9, 8);
        graph.addEdge(5, 9, 15); // Intentionally long path

        int startNode = 0;
        int endNode = 9;

        System.out.println("Graph created with " + numNodes + " nodes.");
        System.out.println("Finding shortest path from Node " + startNode + " to Node " + endNode + "...\n");

        // Run Dijkstra's
        long startTime = System.nanoTime();
        Result dResult = dijkstra(graph, startNode, endNode);
        long dTime = System.nanoTime() - startTime;

        System.out.println("[Dijkstra's Algorithm]");
        System.out.println("Path found: " + dResult.path);
        System.out.println("Total Distance: " + dResult.distance);
        System.out.println("Execution Time: " + dTime + " ns\n");

        // Run Bellman-Ford
        startTime = System.nanoTime();
        Result bfResult = bellmanFord(graph, startNode, endNode);
        long bfTime = System.nanoTime() - startTime;

        System.out.println("[Comparison: Bellman-Ford Algorithm]");
        System.out.println("Path found: " + bfResult.path);
        System.out.println("Total Distance: " + bfResult.distance);
        System.out.println("Execution Time: " + bfTime + " ns\n");

        // Conclusion
        System.out.println("--- Comparison Conclusion ---");
        if (dResult.distance == bfResult.distance) {
            System.out.println("Success: Both algorithms found the same shortest distance.");
            if (dTime < bfTime) {
                 System.out.println("Dijkstra was faster by " + (bfTime - dTime) + " ns.");
            } else {
                 System.out.println("Bellman-Ford was faster by " + (dTime - bfTime) + " ns.");
            }
        } else {
            System.out.println("Error: Algorithms found different distances.");
        }
    }
}