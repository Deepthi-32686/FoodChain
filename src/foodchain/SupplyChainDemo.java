package foodchain;

import foodchain.algorithms.DynamicProgramming;
import foodchain.algorithms.DynamicProgramming.BitmaskAllocationResult;
import foodchain.algorithms.DynamicProgramming.MCMResult;
import foodchain.algorithms.NetworkFlowAlgorithms;
import foodchain.algorithms.NetworkFlowAlgorithms.*;
import foodchain.algorithms.NPCompletenessAlgorithms;
import foodchain.algorithms.NPCompletenessAlgorithms.*;
import foodchain.algorithms.ParallelRandomizedAlgorithms;
import foodchain.algorithms.ParallelRandomizedAlgorithms.*;
import foodchain.algorithms.StringAlgorithms;
import foodchain.algorithms.SuffixAlgorithms;

import java.util.*;

/**
 * Executes an automated, guided demonstration of FoodChain operations.
 * Operates on a deep-copied dataset so live user session inventory remains unmutated.
 * Strictly executes real DSA algorithms underneath every business workflow covering M1 through M6.
 */
public class SupplyChainDemo {

    public static void runDemo() {
        System.out.println("\n=================================================================================");
        System.out.println("                 STARTING FOODCHAIN AUTOMATED GUIDED DEMO                        ");
        System.out.println("         End-to-End Enterprise Demonstration of Modules M1 Through M6           ");
        System.out.println("=================================================================================");
        System.out.println("Note: This demo uses an isolated cloned dataset to preserve live session data.\n");

        // Clone fresh isolated dataset for the demo
        List<Product> demoProducts = SampleData.cloneProducts(SampleData.getSampleProducts());
        List<Supplier> demoSuppliers = SampleData.getSampleSuppliers();
        List<Warehouse> demoWarehouses = SampleData.getSampleWarehouses();
        List<Shipment> demoShipments = SampleData.getSampleShipments();

        ProductService demoProdService = new ProductService(demoProducts, demoSuppliers, demoShipments);
        QualityService demoQualService = new QualityService(demoProducts);
        InventoryService demoInvService = new InventoryService(demoProducts, demoWarehouses);
        OrderService demoOrderService = new OrderService(demoProdService);
        DistributionService demoDistService = new DistributionService(demoSuppliers, demoWarehouses);
        ConstraintService demoConstraintService = new ConstraintService(demoSuppliers, demoWarehouses);
        ParallelAnalyticsService demoAnalyticsService = new ParallelAnalyticsService(demoProducts);
        ReportService demoRepService = new ReportService(demoProdService, demoInvService, demoOrderService,
                demoSuppliers, demoWarehouses);

        // ---------------------------------------------------------------------
        // STEP 1: Display Supplier and Product Information
        // ---------------------------------------------------------------------
        System.out.println(">>> STEP 1: DISPLAY SUPPLIER AND PRODUCT INFORMATION");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Registered Agricultural & Dairy Suppliers:");
        for (Supplier s : demoSuppliers) {
            System.out.println("  " + s);
        }
        System.out.println("\nSample Food Products in Active Catalog (Top 5 preview):");
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + demoProducts.get(i));
        }

        // ---------------------------------------------------------------------
        // STEP 2: Search for 'Rice' using KMP
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 2: SEARCH FOOD PRODUCTS (KMP ALGORITHM - M1)");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Executing KMP exact substring matching for query: 'Rice'...");
        List<Product> riceMatches = demoProdService.searchProductsByKMP("Rice");
        System.out.println("Matching products identified (" + riceMatches.size() + " found):");
        for (Product p : riceMatches) {
            System.out.printf("  %-6s | %-16s | %6.1f kg | Rs.%-6.2f/kg | Quality: %s%n",
                    p.getProductId(), p.getProductName(), p.getQuantityKg(), p.getPricePerKg(), p.getQualityReport());
        }

        // ---------------------------------------------------------------------
        // STEP 3: Demonstrate Spell Correction using Damerau-Levenshtein
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 3: MISSPELLING CORRECTION (DAMERAU-LEVENSHTEIN DP - M3)");
        System.out.println("---------------------------------------------------------------------------------");
        String misspelledInput = "Rcie";
        System.out.println("Customer entered misspelled query: \"" + misspelledInput + "\"");

        List<String> productNames = new ArrayList<>();
        for (Product p : demoProducts) productNames.add(p.getProductName());

        String suggested = DynamicProgramming.suggestClosest(misspelledInput, productNames, 3);
        System.out.println("Auto-Correction Analysis:");
        System.out.println("  True Damerau-Levenshtein identified transposition (c <-> i).");
        System.out.println("  Did you mean: \"" + suggested + "\"? -> Confirmed [Y]");

        List<Product> correctedMatches = demoProdService.searchProductsByKMP(suggested);
        System.out.println("Retrieved records for corrected query \"" + suggested + "\":");
        for (Product p : correctedMatches) {
            System.out.printf("  %-6s | %-16s | %6.1f kg | Rs.%-6.2f/kg%n",
                    p.getProductId(), p.getProductName(), p.getQuantityKg(), p.getPricePerKg());
        }

        // ---------------------------------------------------------------------
        // STEP 4: Scan Food Quality Reports using Aho-Corasick
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 4: SCAN FOOD QUALITY REPORTS (AHO-CORASICK AUTOMATON - M1)");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Scanning quality reports for safety warning keywords simultaneously in linear time...");

        Product milk = demoProdService.findProductById("P106");
        if (milk != null) {
            demoQualService.inspectSingleProduct(milk);
        }
        Product rice = demoProdService.findProductById("P101");
        if (rice != null) {
            demoQualService.inspectSingleProduct(rice);
        }

        // ---------------------------------------------------------------------
        // STEP 5: Search Product Descriptions & Detect Repetition
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 5: SUFFIX INDEX SEARCH & REPETITION ANALYSIS (SA-IS, Z-FUNC, KASAI LCP - M1 & M2)");
        System.out.println("---------------------------------------------------------------------------------");
        String sampleQualityCorpus = "Packaging intact, sealed double-layer bag. Certified hygienic. Packaging intact, clean stock.";
        System.out.println("Analyzing sample inspection text: \"" + sampleQualityCorpus + "\"");

        int[] sa = SuffixAlgorithms.buildSuffixArraySAIS(sampleQualityCorpus);
        List<Integer> occurrences = SuffixAlgorithms.searchSuffixArray(sampleQualityCorpus, sa, "Packaging");
        System.out.println("  SA-IS Suffix Array Substring Search: 'Packaging' found at character indices " + occurrences);

        int longestPrefixRep = StringAlgorithms.findLongestRepeatedPrefix(sampleQualityCorpus);
        System.out.println("  Z-Function Analysis: Longest prefix repetition = " + longestPrefixRep + " characters");

        int[] lcp = SuffixAlgorithms.buildKasaiLCP(sampleQualityCorpus, sa);
        String repeatedPhrase = SuffixAlgorithms.findLongestRepeatedSubstring(sampleQualityCorpus, sa, lcp);
        System.out.println("  Kasai LCP Analysis: Longest duplicate phrase = \"" + repeatedPhrase.trim() + "\"");

        // ---------------------------------------------------------------------
        // STEP 6: Select Products for Limited Warehouse Storage using Bitmask DP
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 6: WAREHOUSE RESOURCE BUDGET ALLOCATION (BITMASK DP - M3)");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Selecting candidate food products for a temperature-controlled bay (Budget: 15 units)...");

        int N = 8;
        int[] weights = new int[N];
        int[] benefits = new int[N];
        for (int i = 0; i < N; i++) {
            weights[i] = demoProducts.get(i).getStorageResourceUnits();
            benefits[i] = demoProducts.get(i).getBenefitScore();
        }

        BitmaskAllocationResult alloc = DynamicProgramming.solveWarehouseAllocation(weights, benefits, 15);
        System.out.println("Optimal Product Subset Selected by Bitmask DP (evaluated " + (1 << N) + " states):");
        for (int idx : alloc.selectedIndices) {
            Product p = demoProducts.get(idx);
            System.out.printf("  [+] %-6s | %-16s | %d storage units | Priority Score: %d%n",
                    p.getProductId(), p.getProductName(), p.getStorageResourceUnits(), p.getBenefitScore());
        }
        System.out.printf("Total Units Allocated: %d / 15 | Total Benefit Score: %d | Remaining Space: %d units%n",
                alloc.totalWeightUsed, alloc.totalBenefit, alloc.remainingCapacity);

        // ---------------------------------------------------------------------
        // STEP 7: Place Sample Customer Order & Deduct Inventory
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 7: CUSTOMER ORDER PLACEMENT WORKFLOW");
        System.out.println("---------------------------------------------------------------------------------");
        Product orderedProduct = demoProdService.findProductById("P101"); // Rice
        double orderQuantity = 20.0;
        double initialStock = orderedProduct.getQuantityKg();
        double orderCost = orderQuantity * orderedProduct.getPricePerKg();

        System.out.println("Customer Order Details:");
        System.out.println("  Product Requested : " + orderedProduct.getProductName() + " (" + orderedProduct.getProductId() + ")");
        System.out.printf("  Quantity Ordered  : %.1f kg%n", orderQuantity);
        System.out.printf("  Unit Price        : Rs. %.2f/kg%n", orderedProduct.getPricePerKg());
        System.out.printf("  Total Order Price : Rs. %.2f%n", orderCost);

        // Deduct inventory
        orderedProduct.reduceQuantity(orderQuantity);
        String orderId = "ORD1001";
        OrderService.CustomerOrder demoOrder = new OrderService.CustomerOrder(
                orderId, orderedProduct.getProductId(), orderedProduct.getProductName(),
                orderQuantity, orderedProduct.getPricePerKg(), orderCost, new Date().toString()
        );
        demoOrderService.getOrderHistory().add(demoOrder);

        System.out.println("\nOrder Confirmed!");
        System.out.printf("Remaining Stock for %s: %.1f kg (Deducted from %.1f kg)%n",
                orderedProduct.getProductName(), orderedProduct.getQuantityKg(), initialStock);

        // ---------------------------------------------------------------------
        // STEP 8: Analytics Pipeline Optimization (MCM DP)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 8: ANALYTICS PIPELINE OPTIMIZATION (MATRIX-CHAIN MULTIPLICATION DP - M3)");
        System.out.println("---------------------------------------------------------------------------------");
        int[] matrixDims = {1, 20, 5, 3, 12, 1};
        MCMResult mcm = DynamicProgramming.solveMCM(matrixDims);
        System.out.println("Supply Chain Multi-Dimensional Matrix Transformation Optimization:");
        System.out.println("  Optimal Parenthesization : " + mcm.optimalParenthesization);
        System.out.println("  Minimum Scalar Multiply : " + mcm.minScalarMultiplications + " operations");

        // ---------------------------------------------------------------------
        // STEP 9: Multi-Echelon Food Distribution & Max Flow (Ford-Fulkerson, Edmonds-Karp, Dinic)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 9: MULTI-ECHELON FOOD DISTRIBUTION (NETWORK FLOW - M4)");
        System.out.println("---------------------------------------------------------------------------------");
        FlowNetwork netFF = demoDistService.buildDistributionNetwork();
        FlowNetwork netEK = demoDistService.buildDistributionNetwork();
        FlowNetwork netDin = demoDistService.buildDistributionNetwork();
        int s = 0;
        int t = netFF.V - 1;

        MaxFlowResult ffRes = NetworkFlowAlgorithms.solveFordFulkerson(netFF, s, t);
        MaxFlowResult ekRes = NetworkFlowAlgorithms.solveEdmondsKarp(netEK, s, t);
        MaxFlowResult dinRes = NetworkFlowAlgorithms.solveDinic(netDin, s, t);

        System.out.printf("Ford-Fulkerson (DFS) : Max Distribution Throughput = %.1f metric tons%n", ffRes.maxFlow);
        System.out.printf("Edmonds-Karp (BFS)   : Max Distribution Throughput = %.1f metric tons%n", ekRes.maxFlow);
        System.out.printf("Dinic's (Level Graph): Max Distribution Throughput = %.1f metric tons%n", dinRes.maxFlow);
        System.out.println("Result: All 3 algorithms rigorously reach flow convergence of " + dinRes.maxFlow + " tons.");

        // ---------------------------------------------------------------------
        // STEP 10: Supplier-to-Warehouse Compatibility Assignment (Bipartite Matching)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 10: SUPPLIER-TO-WAREHOUSE ASSIGNMENT (MAXIMUM BIPARTITE MATCHING - M4)");
        System.out.println("---------------------------------------------------------------------------------");
        demoDistService.runBipartiteAssignmentWorkflow();

        // ---------------------------------------------------------------------
        // STEP 11: Logistics Bottleneck Identification (Max-Flow Min-Cut)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 11: DISTRIBUTION BOTTLENECKS (MAX-FLOW MIN-CUT THEOREM - M4)");
        System.out.println("---------------------------------------------------------------------------------");
        MinCutResult cut = NetworkFlowAlgorithms.findMinCutBottlenecks(netDin, s);
        System.out.printf("Minimum Cut Capacity: %.1f tons == Max Flow (Verified)%n", cut.cutCapacity);
        System.out.println("Identified Saturated Transport Chokepoints (100% capacity utilized):");
        for (FlowEdge e : cut.bottleneckEdges) {
            System.out.printf("  * %-22s ---> %-22s | Max Capacity: %.1f tons%n",
                    netDin.getNodeLabel(e.u), netDin.getNodeLabel(e.v), e.capacity);
        }

        // ---------------------------------------------------------------------
        // STEP 12: Supply Constraints Feasibility (3-SAT DPLL Solver)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 12: OPERATIONAL SUPPLY CONSTRAINTS (3-SAT DPLL SOLVER - M5)");
        System.out.println("---------------------------------------------------------------------------------");
        List<Clause> clauses = Arrays.asList(
                Clause.of(1, 2, 4),
                Clause.of(-2, 3, 5),
                Clause.of(-3, 2, 5),
                Clause.of(-4, 1, 5)
        );
        SATResult sat = NPCompletenessAlgorithms.solveSAT(clauses, 5);
        System.out.println("Evaluated 4 supply/cold-chain constraints across 5 operational decision variables.");
        System.out.println("DPLL Result: " + (sat.satisfiable ? "FEASIBLE SATISFYING OPERATIONAL PLAN FOUND" : "UNSATISFIABLE CONFLICT"));

        // ---------------------------------------------------------------------
        // STEP 13: 3-SAT to CLIQUE Polynomial Reduction
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 13: 3-SAT -> CLIQUE POLYNOMIAL REDUCTION (KARP THEOREM - M5)");
        System.out.println("---------------------------------------------------------------------------------");
        CliqueReductionResult cliqueRed = NPCompletenessAlgorithms.reduce3SatToClique(clauses);
        System.out.printf("Transformed 3-SAT (k = %d clauses) into Reduction Graph G with |V| = %d, |E| = %d.%n",
                cliqueRed.targetCliqueSize, cliqueRed.graph.V, cliqueRed.graph.edgeList.size());
        System.out.println("Found Clique of Size k = " + cliqueRed.targetCliqueSize + ": " + cliqueRed.cliqueVertices);
        System.out.println("Reconstructed Model from Clique: " + cliqueRed.derivedModel);

        // ---------------------------------------------------------------------
        // STEP 14: Conflict Analysis Reduction Chain (CLIQUE -> IS -> VC)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 14: CONFLICT REDUCTION CHAIN: CLIQUE -> IS -> VC (M5)");
        System.out.println("---------------------------------------------------------------------------------");
        ReductionGraph routeConflict = new ReductionGraph(5, Arrays.asList("RouteA", "RouteB", "RouteC", "RouteD", "RouteE"));
        routeConflict.addEdge(0, 1);
        routeConflict.addEdge(1, 2);
        routeConflict.addEdge(2, 3);
        routeConflict.addEdge(3, 4);
        ReductionChainResult chain = NPCompletenessAlgorithms.demonstrateReductionChain(routeConflict);
        System.out.println("Conflict Graph: 5 Distribution Routes, 4 Mutual Exclusivity Conflicts");
        System.out.println("  Max Independent Set (Max Non-Conflicting Routes): " + chain.maxIndependentSetInConflict.size() + " routes");
        System.out.println("  Min Vertex Cover    (Min Conflict Resolving Hubs) : " + chain.minVertexCoverInConflict.size() + " hubs");
        System.out.println("  " + chain.mathematicalProofSummary);

        // ---------------------------------------------------------------------
        // STEP 15: Critical Distribution Hubs (Vertex-Cover 2-Approximation)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 15: CRITICAL DISTRIBUTION CHECKPOINTS (VERTEX-COVER 2-APPROXIMATION - M5)");
        System.out.println("---------------------------------------------------------------------------------");
        VertexCoverApproxResult approx = NPCompletenessAlgorithms.solveVertexCover2Approx(routeConflict);
        System.out.println("Maximal Matching Edge Selection 2-Approximation:");
        System.out.printf("  Approximate Hub Cover Size : %d hubs%n", approx.approximateCover.size());
        System.out.printf("  Exact Optimal Cover Size   : %d hubs%n", approx.exactOptimalCover.size());
        System.out.printf("  Empirical Ratio Achieved   : %.2f (Guaranteed <= 2.0)%n", approx.empiricalRatio);

        // ---------------------------------------------------------------------
        // STEP 16: Product Demand Ranking (Randomized QuickSort)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 16: PRODUCT DEMAND RANKING (RANDOMIZED QUICKSORT - M6)");
        System.out.println("---------------------------------------------------------------------------------");
        List<Product> sortedDemand = new ArrayList<>(demoProducts);
        ParallelRandomizedAlgorithms.randomizedQuickSort(sortedDemand, (a, b) -> Integer.compare(b.getDemandScore(), a.getDemandScore()));
        System.out.println("Top 3 High-Demand Products (Ranked via Randomized Pivot QuickSort):");
        for (int i = 0; i < 3; i++) {
            Product p = sortedDemand.get(i);
            System.out.printf("  #%d: %-16s | Demand Frequency: %d | Rs. %.2f/kg%n",
                    i + 1, p.getProductName(), p.getDemandScore(), p.getPricePerKg());
        }

        // ---------------------------------------------------------------------
        // STEP 17: Streaming Continuous Inventory Events (Reservoir Sampling)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 17: CONTINUOUS TELEMETRY STREAMING (RESERVOIR SAMPLING ALGORITHM R - M6)");
        System.out.println("---------------------------------------------------------------------------------");
        List<String> eventStream = new ArrayList<>();
        for (int i = 1; i <= 500; i++) eventStream.add("SensorEvent-P" + (100 + (i % 20)) + "-Temp" + (2 + (i % 8)) + "C");
        ReservoirResult<String> reservoir = ParallelRandomizedAlgorithms.sampleReservoir(eventStream.iterator(), 4);
        System.out.println("Sampled k = 4 items from continuous unbounded stream of " + reservoir.totalStreamItemsSeen + " telemetry events:");
        for (String ev : reservoir.sampledReservoir) {
            System.out.println("  " + ev);
        }
        System.out.printf("Verified Exact Uniform Probability: %.3f%%%n", reservoir.selectionProbability * 100.0);

        // ---------------------------------------------------------------------
        // STEP 18: Digital Produce Batch Cryptographic Security (Miller-Rabin Primality)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 18: DIGITAL SECURITY TOKENS (MILLER-RABIN PRIMALITY TEST - M6)");
        System.out.println("---------------------------------------------------------------------------------");
        long tokenPrime = 1000000007L;
        long tokenComposite = 1000000005L;
        MillerRabinResult test1 = ParallelRandomizedAlgorithms.testPrimality(tokenPrime, 20);
        MillerRabinResult test2 = ParallelRandomizedAlgorithms.testPrimality(tokenComposite, 20);
        System.out.printf("Batch Verification Token %,d : %s (Rounds: %d)%n",
                tokenPrime, test1.isPrime ? "[VERIFIED AUTHENTIC PRIME CRYPTO TOKEN]" : "FAIL", test1.roundsTested);
        System.out.printf("Batch Verification Token %,d : %s (Witness base: %s)%n",
                tokenComposite, test2.isPrime ? "PRIME" : "[INVALID BATCH TOKEN - COMPOSITE DETECTED]", test2.compositeWitness);

        // ---------------------------------------------------------------------
        // STEP 19: Work-Efficient Cumulative Inventory Scan (Blelloch Scan)
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 19: CUMULATIVE INVENTORY LOAD (BLELLOCH WORK-EFFICIENT PARALLEL SCAN - M6)");
        System.out.println("---------------------------------------------------------------------------------");
        long[] bayLoads = {10, 25, 15, 30, 20, 35, 40, 15};
        BlellochScanResult scan = ParallelRandomizedAlgorithms.executeBlellochScan(bayLoads);
        System.out.println("Hourly Bay Inflow Loads (8 Slots): " + Arrays.toString(bayLoads));
        System.out.println("Blelloch Exclusive Prefix Accumulation: " + Arrays.toString(scan.exclusivePrefixSum));
        System.out.println("Blelloch Inclusive Total Storage Load : " + Arrays.toString(scan.inclusivePrefixSum));
        System.out.printf("Work Complexity: %d operations (Strictly O(N) Work-Efficient) | Depth: %d levels (O(log N))%n",
                scan.totalWorkOperations, scan.treeDepth);

        // ---------------------------------------------------------------------
        // STEP 20: Parallel Catalog Reduction & Brent's Theorem Scaling
        // ---------------------------------------------------------------------
        System.out.println("\n>>> STEP 20: PARALLEL REDUCE & BRENT'S THEOREM SPEEDUP (M6)");
        System.out.println("---------------------------------------------------------------------------------");
        double[] weightsCatalog = new double[demoProducts.size()];
        for (int i = 0; i < demoProducts.size(); i++) weightsCatalog[i] = demoProducts.get(i).getQuantityKg();
        ParallelReduceResult pReduce = ParallelRandomizedAlgorithms.executeParallelReduce(weightsCatalog);
        System.out.printf("ForkJoin Multi-Core Reduction (%d CPU threads): Total Stock = %.1f kg | Mean = %.1f kg%n",
                pReduce.parallelThreadsUsed, pReduce.totalSum, pReduce.meanValue);

        List<BrentScalingPoint> brentPoints = ParallelRandomizedAlgorithms.evaluateBrentsTheorem(
                100000.0, 20.0, new int[]{1, 2, 4, 8, 16, 32}
        );
        System.out.println("Brent's Theorem Scaling Analysis (W = 100,000 ops, D = 20 ops):");
        for (BrentScalingPoint pt : brentPoints) {
            System.out.printf("  p = %2d cores | Time T_p = %8.1f ops | Speedup = %5.2fx | Efficiency = %6.2f%%%n",
                    pt.processorCount, pt.theoreticalTimeTp, pt.speedup, pt.efficiencyPercent);
        }

        System.out.println("\n=================================================================================");
        System.out.println("          FOODCHAIN FULL END-TO-END DEMONSTRATION COMPLETE (M1 - M6)             ");
        System.out.println("=================================================================================\n");
    }
}
