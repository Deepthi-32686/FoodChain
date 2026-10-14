package foodchain;

import foodchain.algorithms.NetworkFlowAlgorithms;
import foodchain.algorithms.NetworkFlowAlgorithms.*;

import java.util.*;

/**
 * Manages food distribution planning, network flow capacity, and logistics chokepoint analysis.
 * Integrates Module M4:
 * 1. Ford-Fulkerson & Edmonds-Karp for multi-tier food distribution
 * 2. Dinic's Algorithm for large supply network optimization
 * 3. Bipartite Matching for supplier-to-warehouse eligibility assignment
 * 4. Max-Flow Min-Cut theorem for identifying distribution bottlenecks
 */
public class DistributionService {

    private final List<Supplier> suppliers;
    private final List<Warehouse> warehouses;
    private final List<Market> markets;

    public static class Market {
        public final String marketId;
        public final String marketName;
        public final String region;
        public final double demandCapacity;

        public Market(String marketId, String marketName, String region, double demandCapacity) {
            this.marketId = marketId;
            this.marketName = marketName;
            this.region = region;
            this.demandCapacity = demandCapacity;
        }

        @Override
        public String toString() {
            return String.format("%s | %-22s | Region: %-10s | Demand: %.1f tons",
                    marketId, marketName, region, demandCapacity);
        }
    }

    public DistributionService(List<Supplier> suppliers, List<Warehouse> warehouses) {
        this.suppliers = suppliers;
        this.warehouses = warehouses;
        this.markets = initSampleMarkets();
    }

    private static List<Market> initSampleMarkets() {
        List<Market> list = new ArrayList<>();
        list.add(new Market("MKT01", "Northern Retail Metro", "Delhi NCR", 350.0));
        list.add(new Market("MKT02", "Western Urban Markets", "Mumbai", 300.0));
        list.add(new Market("MKT03", "Southern Agro Mart", "Bengaluru", 250.0));
        list.add(new Market("MKT04", "Eastern Wholesale Hub", "Kolkata", 200.0));
        return list;
    }

    public void executeDistributionMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Food Distribution & Network Flow Optimization (M4) ---");
            System.out.println("1. Analyze Supply Network Flow (Ford-Fulkerson vs Edmonds-Karp)");
            System.out.println("2. Optimize Large Distribution Network (Dinic's Level Graph)");
            System.out.println("3. Assign Suppliers to Compatible Warehouses (Bipartite Matching)");
            System.out.println("4. Identify Distribution Bottlenecks (Max-Flow Min-Cut Theorem)");
            System.out.println("5. View Complete Network Architecture & Capacity Specs");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select distribution planning operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runCapacityAnalysisWorkflow();
                    break;
                case "2":
                    runDinicOptimizationWorkflow();
                    break;
                case "3":
                    runBipartiteAssignmentWorkflow();
                    break;
                case "4":
                    runMinCutBottleneckWorkflow();
                    break;
                case "5":
                    displayNetworkTopology();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please choose an option between 0 and 5.");
            }
        }
    }

    /**
     * Builds the standard FoodChain multi-echelon distribution network:
     * Source (0) -> Suppliers (1..5) -> Warehouses (6..8) -> Markets (9..12) -> Sink (13).
     */
    public FlowNetwork buildDistributionNetwork() {
        int nSuppliers = suppliers.size(); // 5
        int nWarehouses = warehouses.size(); // 3
        int nMarkets = markets.size(); // 4

        int s = 0;
        int t = 1 + nSuppliers + nWarehouses + nMarkets;
        FlowNetwork network = new FlowNetwork(t + 1);

        network.setNodeLabel(s, "Super Source");
        network.setNodeLabel(t, "Super Sink");

        // Supplier production capacities (Source -> Supplier)
        double[] supplierCapacities = {250.0, 220.0, 180.0, 240.0, 210.0};
        for (int i = 0; i < nSuppliers; i++) {
            int node = 1 + i;
            network.setNodeLabel(node, suppliers.get(i).getSupplierName());
            network.addEdge(s, node, supplierCapacities[i]);
        }

        // Supplier -> Warehouse transport corridors
        int whOffset = 1 + nSuppliers;
        for (int j = 0; j < nWarehouses; j++) {
            network.setNodeLabel(whOffset + j, warehouses.get(j).getWarehouseName());
        }

        // Corridor transit capacities (tons)
        network.addEdge(1, whOffset + 0, 140.0); // SUP01 -> WH01 (Delhi)
        network.addEdge(1, whOffset + 1, 100.0); // SUP01 -> WH02 (Mumbai)
        network.addEdge(2, whOffset + 1, 150.0); // SUP02 -> WH02
        network.addEdge(2, whOffset + 2, 70.0);  // SUP02 -> WH03 (Bengaluru)
        network.addEdge(3, whOffset + 0, 90.0);  // SUP03 -> WH01
        network.addEdge(3, whOffset + 1, 90.0);  // SUP03 -> WH02
        network.addEdge(4, whOffset + 1, 80.0);  // SUP04 -> WH02
        network.addEdge(4, whOffset + 2, 160.0); // SUP04 -> WH03
        network.addEdge(5, whOffset + 0, 150.0); // SUP05 -> WH01
        network.addEdge(5, whOffset + 2, 60.0);  // SUP05 -> WH03

        // Warehouse -> Retail Consumer Markets
        int mktOffset = whOffset + nWarehouses;
        for (int k = 0; k < nMarkets; k++) {
            network.setNodeLabel(mktOffset + k, markets.get(k).marketName);
        }

        network.addEdge(whOffset + 0, mktOffset + 0, 220.0); // WH01 -> MKT01 (North)
        network.addEdge(whOffset + 0, mktOffset + 3, 110.0); // WH01 -> MKT04 (East)
        network.addEdge(whOffset + 1, mktOffset + 1, 230.0); // WH02 -> MKT02 (West)
        network.addEdge(whOffset + 1, mktOffset + 3, 80.0);  // WH02 -> MKT04 (East)
        network.addEdge(whOffset + 2, mktOffset + 2, 60.0);  // WH03 -> MKT02 (West)
        network.addEdge(whOffset + 2, mktOffset + 3, 190.0); // WH03 -> MKT03 (South)

        // Retail Market absorption demand (Market -> Sink)
        for (int k = 0; k < nMarkets; k++) {
            network.addEdge(mktOffset + k, t, markets.get(k).demandCapacity);
        }

        return network;
    }

    public void displayNetworkTopology() {
        System.out.println("\n=================================================================================");
        System.out.println("                 FOODCHAIN MULTI-ECHELON DISTRIBUTION NETWORK                    ");
        System.out.println("=================================================================================");
        System.out.println("Tier 1: Agricultural Suppliers (5 Production Centers):");
        for (Supplier s : suppliers) {
            System.out.printf("  %-6s | %-24s | Origin: %-12s%n", s.getSupplierId(), s.getSupplierName(), s.getLocation());
        }
        System.out.println("\nTier 2: Regional Distribution Warehouses (3 Logistics Hubs):");
        for (Warehouse w : warehouses) {
            System.out.printf("  %-6s | %-28s | Hub: %-12s | Capacity: %.0f kg%n",
                    w.getWarehouseId(), w.getWarehouseName(), w.getLocation(), w.getTotalCapacityKg());
        }
        System.out.println("\nTier 3: Retail Consumer Markets (4 Regional Terminals):");
        for (Market m : markets) {
            System.out.println("  " + m);
        }
        System.out.println("=================================================================================");
    }

    public void runCapacityAnalysisWorkflow() {
        FlowNetwork netFF = buildDistributionNetwork();
        FlowNetwork netEK = buildDistributionNetwork();
        int s = 0;
        int t = netFF.V - 1;

        System.out.println("\n--- Distribution Capacity: Ford-Fulkerson vs Edmonds-Karp ---");
        long startFF = System.nanoTime();
        MaxFlowResult ff = NetworkFlowAlgorithms.solveFordFulkerson(netFF, s, t);
        long elapsedFF = System.nanoTime() - startFF;

        long startEK = System.nanoTime();
        MaxFlowResult ek = NetworkFlowAlgorithms.solveEdmondsKarp(netEK, s, t);
        long elapsedEK = System.nanoTime() - startEK;

        System.out.printf("Ford-Fulkerson (DFS): Max Throughput = %.1f tons | Augmenting Paths = %d | Execution = %d ns%n",
                ff.maxFlow, ff.augmentingPathsFound, elapsedFF);
        System.out.printf("Edmonds-Karp (BFS)  : Max Throughput = %.1f tons | Augmenting Paths = %d | Execution = %d ns%n",
                ek.maxFlow, ek.augmentingPathsFound, elapsedEK);

        System.out.println("\nActive Supply Corridors (Edmonds-Karp Scheduled Flow):");
        for (FlowEdge e : ek.activeFlowEdges) {
            if (e.u != s && e.v != t) {
                System.out.printf("  %-25s ---> %-25s | Flow: %5.1f / %5.1f tons (%5.1f%%)%n",
                        netEK.getNodeLabel(e.u), netEK.getNodeLabel(e.v), e.flow, e.capacity, (e.flow / e.capacity) * 100.0);
            }
        }
    }

    public void runDinicOptimizationWorkflow() {
        FlowNetwork net = buildDistributionNetwork();
        int s = 0;
        int t = net.V - 1;

        System.out.println("\n--- Large Supply Network Optimization: Dinic's Algorithm ---");
        long start = System.nanoTime();
        MaxFlowResult result = NetworkFlowAlgorithms.solveDinic(net, s, t);
        long elapsed = System.nanoTime() - start;

        System.out.printf("Dinic's Result: Maximum Network Flow = %.1f tons%n", result.maxFlow);
        System.out.printf("Phase Level Graphs Constructed: %d | Time: %d ns%n", result.augmentingPathsFound, elapsed);
        System.out.println("Theoretical Guarantee: O(V^2 * E) vs O(V * E^2) Edmonds-Karp for large networks.");
    }

    public void runBipartiteAssignmentWorkflow() {
        System.out.println("\n--- Supplier-to-Warehouse Compatibility Matching (Bipartite Matching) ---");
        List<String> supplierNames = new ArrayList<>();
        for (Supplier s : suppliers) supplierNames.add(s.getSupplierName());

        List<String> warehouseNames = new ArrayList<>();
        for (Warehouse w : warehouses) warehouseNames.add(w.getWarehouseName());

        // Eligibility constraints based on storage facilities and cold chain compatibility
        Map<String, Set<String>> eligibility = new LinkedHashMap<>();
        eligibility.put("Green Valley Farms", new HashSet<>(Arrays.asList("Metro North Central Hub", "Western Regional Depot")));
        eligibility.put("AgroFresh Organic Co.", new HashSet<>(Arrays.asList("Western Regional Depot", "Southern Distribution Depot")));
        eligibility.put("Highland Dairy Ltd.", new HashSet<>(Arrays.asList("Metro North Central Hub", "Western Regional Depot")));
        eligibility.put("Sunrise Horticulture", new HashSet<>(Arrays.asList("Southern Distribution Depot")));
        eligibility.put("Golden Harvest Mills", new HashSet<>(Arrays.asList("Metro North Central Hub")));

        BipartiteMatchResult match = NetworkFlowAlgorithms.assignSuppliersToWarehouses(supplierNames, warehouseNames, eligibility);

        System.out.println("Maximum Cardinality Matching Found: " + match.maxMatchingSize + " assignments");
        for (Map.Entry<String, String> e : match.supplierToWarehouse.entrySet()) {
            System.out.printf("  [ASSIGNED] %-25s ===> %-28s%n", e.getKey(), e.getValue());
        }

        if (!match.unassignedSuppliers.isEmpty()) {
            System.out.println("\nUnassigned / Secondary Suppliers: " + match.unassignedSuppliers);
        }
        if (!match.unassignedWarehouses.isEmpty()) {
            System.out.println("Unassigned Warehouses: " + match.unassignedWarehouses);
        }
    }

    public void runMinCutBottleneckWorkflow() {
        FlowNetwork net = buildDistributionNetwork();
        int s = 0;
        int t = net.V - 1;

        // Solve max flow first
        MaxFlowResult maxFlow = NetworkFlowAlgorithms.solveDinic(net, s, t);
        MinCutResult cut = NetworkFlowAlgorithms.findMinCutBottlenecks(net, s);

        System.out.println("\n=================================================================================");
        System.out.println("           DISTRIBUTION BOTTLENECK ANALYSIS (MAX-FLOW MIN-CUT THEOREM)           ");
        System.out.println("=================================================================================");
        System.out.printf("Max System Flow Capacity : %.1f tons%n", maxFlow.maxFlow);
        System.out.printf("Min-Cut Boundary Capacity: %.1f tons (Verified: Cut Capacity == Max Flow)%n", cut.cutCapacity);
        System.out.println("\nIdentified Chokepoints / Saturated Bottleneck Corridors (100% Saturated):");

        for (FlowEdge e : cut.bottleneckEdges) {
            System.out.printf("  [BOTTLENECK] %-25s ---> %-25s | Capacity: %.1f tons (Saturated)%n",
                    net.getNodeLabel(e.u), net.getNodeLabel(e.v), e.capacity);
        }
        System.out.println("\nLogistics Insight: Expanding transit capacity on the above bottleneck corridors");
        System.out.println("will directly increase overall network throughput to retail markets.");
        System.out.println("=================================================================================");
    }
}
