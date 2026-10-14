package foodchain.algorithms;

import java.util.*;

/**
 * Network Flow algorithms for FoodChain platform.
 * Implements Module M4 (CO4):
 * 1. Ford-Fulkerson algorithm for food distribution modeling.
 * 2. Edmonds-Karp algorithm (BFS shortest augmenting paths) for distribution capacity analysis.
 * 3. Dinic's algorithm (Level graph & blocking flows) for large supply networks.
 * 4. Maximum Bipartite Matching for assigning suppliers to eligible warehouses.
 * 5. Max-Flow Min-Cut theorem for identifying distribution bottlenecks and chokepoints.
 */
public class NetworkFlowAlgorithms {

    // =========================================================================
    // FLOW EDGE & FLOW NETWORK REPRESENTATIONS
    // =========================================================================

    /**
     * Represents a directed flow edge with residual network capabilities.
     */
    public static class FlowEdge {
        public final int u; // From node
        public final int v; // To node
        public final double capacity;
        public double flow;
        public FlowEdge residualEdge;

        public FlowEdge(int u, int v, double capacity) {
            this.u = u;
            this.v = v;
            this.capacity = capacity;
            this.flow = 0.0;
        }

        public double residualCapacity() {
            return capacity - flow;
        }

        public void addResidualFlow(double delta) {
            this.flow += delta;
            if (this.residualEdge != null) {
                this.residualEdge.flow -= delta;
            }
        }

        @Override
        public String toString() {
            return String.format("[%d -> %d | Flow: %.1f / %.1f]", u, v, flow, capacity);
        }
    }

    /**
     * Residual flow network represented as an adjacency list of FlowEdge references.
     */
    public static class FlowNetwork {
        public final int V;
        public final List<List<FlowEdge>> adj;
        public final List<FlowEdge> edges; // Primary forward edges
        private final Map<Integer, String> nodeLabels;

        public FlowNetwork(int V) {
            this.V = V;
            this.adj = new ArrayList<>(V);
            for (int i = 0; i < V; i++) {
                this.adj.add(new ArrayList<>());
            }
            this.edges = new ArrayList<>();
            this.nodeLabels = new HashMap<>();
        }

        public void setNodeLabel(int u, String label) {
            nodeLabels.put(u, label);
        }

        public String getNodeLabel(int u) {
            return nodeLabels.getOrDefault(u, "Node-" + u);
        }

        public void addEdge(int u, int v, double capacity) {
            FlowEdge forward = new FlowEdge(u, v, capacity);
            FlowEdge backward = new FlowEdge(v, u, 0.0);
            forward.residualEdge = backward;
            backward.residualEdge = forward;

            adj.get(u).add(forward);
            adj.get(v).add(backward);
            edges.add(forward);
        }

        public FlowNetwork cloneCleanNetwork() {
            FlowNetwork clone = new FlowNetwork(this.V);
            for (Map.Entry<Integer, String> e : this.nodeLabels.entrySet()) {
                clone.setNodeLabel(e.getKey(), e.getValue());
            }
            for (FlowEdge e : this.edges) {
                clone.addEdge(e.u, e.v, e.capacity);
            }
            return clone;
        }
    }

    // =========================================================================
    // RESULT CONTAINERS
    // =========================================================================

    public static class MaxFlowResult {
        public final double maxFlow;
        public final int augmentingPathsFound;
        public final List<FlowEdge> activeFlowEdges;
        public final String algorithmName;

        public MaxFlowResult(double maxFlow, int augmentingPathsFound, List<FlowEdge> activeFlowEdges, String algorithmName) {
            this.maxFlow = maxFlow;
            this.augmentingPathsFound = augmentingPathsFound;
            this.activeFlowEdges = activeFlowEdges;
            this.algorithmName = algorithmName;
        }
    }

    public static class MinCutResult {
        public final double cutCapacity;
        public final Set<Integer> sourceSet;
        public final Set<Integer> sinkSet;
        public final List<FlowEdge> bottleneckEdges;

        public MinCutResult(double cutCapacity, Set<Integer> sourceSet, Set<Integer> sinkSet, List<FlowEdge> bottleneckEdges) {
            this.cutCapacity = cutCapacity;
            this.sourceSet = sourceSet;
            this.sinkSet = sinkSet;
            this.bottleneckEdges = bottleneckEdges;
        }
    }

    public static class BipartiteMatchResult {
        public final int maxMatchingSize;
        public final Map<String, String> supplierToWarehouse;
        public final List<String> unassignedSuppliers;
        public final List<String> unassignedWarehouses;

        public BipartiteMatchResult(int maxMatchingSize, Map<String, String> supplierToWarehouse,
                                    List<String> unassignedSuppliers, List<String> unassignedWarehouses) {
            this.maxMatchingSize = maxMatchingSize;
            this.supplierToWarehouse = supplierToWarehouse;
            this.unassignedSuppliers = unassignedSuppliers;
            this.unassignedWarehouses = unassignedWarehouses;
        }
    }

    // =========================================================================
    // 1. FORD-FULKERSON (DFS AUGMENTING PATHS)
    // =========================================================================

    /**
     * Solves maximum flow using standard Ford-Fulkerson with DFS path search.
     */
    public static MaxFlowResult solveFordFulkerson(FlowNetwork network, int s, int t) {
        double maxFlow = 0.0;
        int paths = 0;

        while (true) {
            boolean[] visited = new boolean[network.V];
            FlowEdge[] edgeTo = new FlowEdge[network.V];

            double bottle = dfsAugment(network, s, t, Double.POSITIVE_INFINITY, visited, edgeTo);
            if (bottle <= 1e-9) {
                break;
            }

            maxFlow += bottle;
            paths++;
        }

        List<FlowEdge> active = new ArrayList<>();
        for (FlowEdge e : network.edges) {
            if (e.flow > 1e-9) {
                active.add(e);
            }
        }
        return new MaxFlowResult(maxFlow, paths, active, "Ford-Fulkerson (DFS)");
    }

    private static double dfsAugment(FlowNetwork network, int u, int t, double bottleneck, boolean[] visited, FlowEdge[] edgeTo) {
        visited[u] = true;
        if (u == t) return bottleneck;

        for (FlowEdge edge : network.adj.get(u)) {
            int v = edge.v;
            if (!visited[v] && edge.residualCapacity() > 1e-9) {
                edgeTo[v] = edge;
                double pushed = dfsAugment(network, v, t, Math.min(bottleneck, edge.residualCapacity()), visited, edgeTo);
                if (pushed > 1e-9) {
                    edge.addResidualFlow(pushed);
                    return pushed;
                }
            }
        }
        return 0.0;
    }

    // =========================================================================
    // 2. EDMONDS-KARP (BFS SHORTEST AUGMENTING PATHS)
    // =========================================================================

    /**
     * Solves maximum flow using Edmonds-Karp (BFS).
     * Guarantees polynomial runtime O(V * E^2).
     */
    public static MaxFlowResult solveEdmondsKarp(FlowNetwork network, int s, int t) {
        double maxFlow = 0.0;
        int paths = 0;

        while (true) {
            FlowEdge[] edgeTo = new FlowEdge[network.V];
            boolean[] visited = new boolean[network.V];
            Queue<Integer> queue = new ArrayDeque<>();

            visited[s] = true;
            queue.add(s);

            while (!queue.isEmpty() && !visited[t]) {
                int u = queue.poll();
                for (FlowEdge edge : network.adj.get(u)) {
                    int v = edge.v;
                    if (!visited[v] && edge.residualCapacity() > 1e-9) {
                        visited[v] = true;
                        edgeTo[v] = edge;
                        queue.add(v);
                    }
                }
            }

            if (!visited[t]) {
                break; // No augmenting path found
            }

            // Find bottleneck capacity along BFS path
            double bottle = Double.POSITIVE_INFINITY;
            for (int curr = t; curr != s; ) {
                FlowEdge e = edgeTo[curr];
                bottle = Math.min(bottle, e.residualCapacity());
                curr = e.u;
            }

            // Augment residual flow along path
            for (int curr = t; curr != s; ) {
                FlowEdge e = edgeTo[curr];
                e.addResidualFlow(bottle);
                curr = e.u;
            }

            maxFlow += bottle;
            paths++;
        }

        List<FlowEdge> active = new ArrayList<>();
        for (FlowEdge e : network.edges) {
            if (e.flow > 1e-9) {
                active.add(e);
            }
        }
        return new MaxFlowResult(maxFlow, paths, active, "Edmonds-Karp (BFS)");
    }

    // =========================================================================
    // 3. DINIC'S ALGORITHM (LEVEL GRAPH & BLOCKING FLOWS)
    // =========================================================================

    /**
     * Solves maximum flow using Dinic's Algorithm.
     * Uses BFS level graph and DFS blocking flow with work pointer optimization.
     * Runtime O(V^2 * E).
     */
    public static MaxFlowResult solveDinic(FlowNetwork network, int s, int t) {
        double maxFlow = 0.0;
        int phases = 0;
        int[] level = new int[network.V];
        int[] nextEdge = new int[network.V];

        while (buildDinicLevelGraph(network, s, t, level)) {
            Arrays.fill(nextEdge, 0);
            while (true) {
                double pushed = sendDinicBlockingFlow(network, s, t, Double.POSITIVE_INFINITY, level, nextEdge);
                if (pushed <= 1e-9) {
                    break;
                }
                maxFlow += pushed;
            }
            phases++;
        }

        List<FlowEdge> active = new ArrayList<>();
        for (FlowEdge e : network.edges) {
            if (e.flow > 1e-9) {
                active.add(e);
            }
        }
        return new MaxFlowResult(maxFlow, phases, active, "Dinic's Algorithm (Level Graph)");
    }

    private static boolean buildDinicLevelGraph(FlowNetwork network, int s, int t, int[] level) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(s);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (FlowEdge edge : network.adj.get(u)) {
                int v = edge.v;
                if (level[v] < 0 && edge.residualCapacity() > 1e-9) {
                    level[v] = level[u] + 1;
                    q.add(v);
                }
            }
        }
        return level[t] >= 0;
    }

    private static double sendDinicBlockingFlow(FlowNetwork network, int u, int t, double flow, int[] level, int[] nextEdge) {
        if (u == t) return flow;

        List<FlowEdge> edges = network.adj.get(u);
        for (; nextEdge[u] < edges.size(); nextEdge[u]++) {
            FlowEdge edge = edges.get(nextEdge[u]);
            int v = edge.v;

            if (level[v] == level[u] + 1 && edge.residualCapacity() > 1e-9) {
                double currFlow = Math.min(flow, edge.residualCapacity());
                double pushed = sendDinicBlockingFlow(network, v, t, currFlow, level, nextEdge);
                if (pushed > 1e-9) {
                    edge.addResidualFlow(pushed);
                    return pushed;
                }
            }
        }
        return 0.0;
    }

    // =========================================================================
    // 4. MAX-FLOW MIN-CUT THEOREM (BOTTLENECK IDENTIFICATION)
    // =========================================================================

    /**
     * Identifies the minimum s-t cut and supply chain distribution bottlenecks.
     * Cut capacity equals maximum flow.
     */
    public static MinCutResult findMinCutBottlenecks(FlowNetwork network, int s) {
        boolean[] reachable = new boolean[network.V];
        Queue<Integer> queue = new ArrayDeque<>();
        reachable[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (FlowEdge edge : network.adj.get(u)) {
                int v = edge.v;
                if (!reachable[v] && edge.residualCapacity() > 1e-9) {
                    reachable[v] = true;
                    queue.add(v);
                }
            }
        }

        Set<Integer> sourceSet = new LinkedHashSet<>();
        Set<Integer> sinkSet = new LinkedHashSet<>();
        for (int i = 0; i < network.V; i++) {
            if (reachable[i]) {
                sourceSet.add(i);
            } else {
                sinkSet.add(i);
            }
        }

        double cutCapacity = 0.0;
        List<FlowEdge> bottleneckEdges = new ArrayList<>();
        for (FlowEdge edge : network.edges) {
            // An edge is in the cut if its source is in sourceSet and target is in sinkSet
            if (reachable[edge.u] && !reachable[edge.v]) {
                cutCapacity += edge.capacity;
                bottleneckEdges.add(edge);
            }
        }

        return new MinCutResult(cutCapacity, sourceSet, sinkSet, bottleneckEdges);
    }

    // =========================================================================
    // 5. MAXIMUM BIPARTITE MATCHING (SUPPLIER TO WAREHOUSE ASSIGNMENT)
    // =========================================================================

    /**
     * Assigns agricultural suppliers to compatible warehouses using maximum bipartite matching.
     *
     * @param suppliers List of supplier names/IDs.
     * @param warehouses List of warehouse names/IDs.
     * @param eligibilityMatrix Map of supplier -> set of compatible warehouses.
     * @return BipartiteMatchResult containing maximum cardinality assignment.
     */
    public static BipartiteMatchResult assignSuppliersToWarehouses(
            List<String> suppliers,
            List<String> warehouses,
            Map<String, Set<String>> eligibilityMatrix) {

        int nSuppliers = suppliers.size();
        int nWarehouses = warehouses.size();

        // Node indexing:
        // 0: Source S
        // 1 .. nSuppliers: Suppliers
        // nSuppliers + 1 .. nSuppliers + nWarehouses: Warehouses
        // nSuppliers + nWarehouses + 1: Sink T
        int s = 0;
        int t = nSuppliers + nWarehouses + 1;
        FlowNetwork network = new FlowNetwork(t + 1);

        network.setNodeLabel(s, "Source-Super");
        network.setNodeLabel(t, "Sink-Super");

        for (int i = 0; i < nSuppliers; i++) {
            network.setNodeLabel(i + 1, suppliers.get(i));
            network.addEdge(s, i + 1, 1.0); // Source -> Supplier
        }

        for (int j = 0; j < nWarehouses; j++) {
            network.setNodeLabel(nSuppliers + 1 + j, warehouses.get(j));
            network.addEdge(nSuppliers + 1 + j, t, 1.0); // Warehouse -> Sink
        }

        for (int i = 0; i < nSuppliers; i++) {
            String sup = suppliers.get(i);
            Set<String> eligible = eligibilityMatrix.getOrDefault(sup, Collections.emptySet());
            for (int j = 0; j < nWarehouses; j++) {
                String wh = warehouses.get(j);
                if (eligible.contains(wh)) {
                    network.addEdge(i + 1, nSuppliers + 1 + j, 1.0);
                }
            }
        }

        solveDinic(network, s, t);

        Map<String, String> assignment = new LinkedHashMap<>();
        Set<String> matchedWarehouses = new HashSet<>();

        for (FlowEdge e : network.edges) {
            if (e.u >= 1 && e.u <= nSuppliers && e.v > nSuppliers && e.v < t && e.flow > 0.5) {
                String sup = suppliers.get(e.u - 1);
                String wh = warehouses.get(e.v - nSuppliers - 1);
                assignment.put(sup, wh);
                matchedWarehouses.add(wh);
            }
        }

        List<String> unassignedSuppliers = new ArrayList<>();
        for (String sup : suppliers) {
            if (!assignment.containsKey(sup)) {
                unassignedSuppliers.add(sup);
            }
        }

        List<String> unassignedWarehouses = new ArrayList<>();
        for (String wh : warehouses) {
            if (!matchedWarehouses.contains(wh)) {
                unassignedWarehouses.add(wh);
            }
        }

        return new BipartiteMatchResult(assignment.size(), assignment, unassignedSuppliers, unassignedWarehouses);
    }
}
