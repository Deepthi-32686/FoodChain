package foodchain;

import foodchain.algorithms.DynamicProgramming;
import foodchain.algorithms.DynamicProgramming.BitmaskAllocationResult;
import foodchain.algorithms.DynamicProgramming.MCMResult;
import foodchain.algorithms.DynamicProgramming.OBSTResult;
import foodchain.algorithms.DynamicProgramming.OBSTSearchResult;
import foodchain.algorithms.NetworkFlowAlgorithms;
import foodchain.algorithms.NetworkFlowAlgorithms.*;
import foodchain.algorithms.NPCompletenessAlgorithms;
import foodchain.algorithms.NPCompletenessAlgorithms.*;
import foodchain.algorithms.ParallelRandomizedAlgorithms;
import foodchain.algorithms.ParallelRandomizedAlgorithms.*;
import foodchain.algorithms.StringAlgorithms;
import foodchain.algorithms.StringAlgorithms.AhoCorasickScanner;
import foodchain.algorithms.StringAlgorithms.QualityMatch;
import foodchain.algorithms.SuffixAlgorithms;
import foodchain.algorithms.SuffixAlgorithms.SuffixAutomaton;
import foodchain.algorithms.SuffixAlgorithms.SuffixTree;

import java.util.*;

/**
 * Comprehensive 29-Point DSA Validation Test Suite for FoodChain.
 * Validates all algorithms in Modules M1 through M6 with strict assertions
 * without any external test dependencies (JUnit/TestNG).
 */
public class FoodChainTest {

    private static int totalPassed = 0;
    private static int totalFailed = 0;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("             RUNNING FOODCHAIN DSA VALIDATION TEST SUITE           ");
        System.out.println("          Comprehensive Verification of Modules M1 Through M6     ");
        System.out.println("==================================================================\n");

        // Module M1: String Algorithms
        runTest("1. KMP Pattern Search & LPS Array", FoodChainTest::testKMP);
        runTest("2. Rabin-Karp Rolling Hash Verification", FoodChainTest::testRabinKarp);
        runTest("3. Aho-Corasick Multi-Pattern Warning Detection", FoodChainTest::testAhoCorasick);
        runTest("4. Z-Algorithm (Z-Function) Prefix Computation", FoodChainTest::testZFunction);

        // Module M2: Suffix Structures
        runTest("5. Suffix Array & SA-IS Linear-Time Equivalence", FoodChainTest::testSAISAgainstStandard);
        runTest("6. Kasai LCP Array & Longest Duplicate Text", FoodChainTest::testKasaiLCP);
        runTest("7. Suffix Tree Substring Lookup", FoodChainTest::testSuffixTree);
        runTest("8. Suffix Automaton (DAWG) Substring Verification", FoodChainTest::testSuffixAutomaton);

        // Module M3: Advanced Dynamic Programming
        runTest("9. Levenshtein & True Damerau-Levenshtein Distance", FoodChainTest::testEditDistances);
        runTest("10. Bitmask DP Warehouse Allocation vs Brute Force", FoodChainTest::testBitmaskDP);
        runTest("11. Matrix-Chain Multiplication (MCM) Optimization", FoodChainTest::testMCM);
        runTest("12. Optimal BST Construction & Tree Lookups", FoodChainTest::testOptimalBST);

        // Domain Business Workflows
        runTest("13. Customer Order Placement & Stock Mutation Logic", FoodChainTest::testOrderLogic);
        runTest("14. Automated Supply Chain Demo Non-Mutating Execution", FoodChainTest::testDemoIsolation);

        // Module M4: Network Flow
        runTest("15. Ford-Fulkerson Max Flow Residual Augmentation", FoodChainTest::testFordFulkerson);
        runTest("16. Edmonds-Karp BFS Shortest Augmenting Path Equivalence", FoodChainTest::testEdmondsKarp);
        runTest("17. Dinic's Level Graph & Blocking Flow Equivalence", FoodChainTest::testDinic);
        runTest("18. Maximum Bipartite Matching Supplier-to-Warehouse", FoodChainTest::testBipartiteMatching);
        runTest("19. Max-Flow Min-Cut Theorem & Bottleneck Capacity", FoodChainTest::testMinCut);

        // Module M5: NP-Completeness & Approximation
        runTest("20. SAT / 3-SAT DPLL Solver Unit Propagation & Model", FoodChainTest::testSATDPLL);
        runTest("21. 3-SAT -> CLIQUE Polynomial Reduction & Model", FoodChainTest::test3SatToCliqueReduction);
        runTest("22. Reduction Chain: CLIQUE -> IS -> VERTEX-COVER Identity", FoodChainTest::testReductionChain);
        runTest("23. Vertex-Cover 2-Approximation Maximal Matching Ratio", FoodChainTest::testVertexCover2Approx);

        // Module M6: Randomized & Parallel Algorithms
        runTest("24. Randomized QuickSort In-Place Demand Ranking", FoodChainTest::testRandomizedQuickSort);
        runTest("25. Reservoir Sampling (Algorithm R) Uniform Stream Selection", FoodChainTest::testReservoirSampling);
        runTest("26. Miller-Rabin Primality Test for Batch Crypto Tokens", FoodChainTest::testMillerRabin);
        runTest("27. Blelloch Work-Efficient Parallel Prefix Scan", FoodChainTest::testBlellochScan);
        runTest("28. ForkJoin Parallel Reduce Multi-Core Aggregation", FoodChainTest::testParallelReduce);
        runTest("29. Brent's Scheduling Theorem Speedup & Efficiency", FoodChainTest::testBrentsTheorem);

        System.out.println("\n==================================================================");
        System.out.printf("TEST RESULTS: %d PASSED | %d FAILED (Success Rate: %.1f%%)%n",
                totalPassed, totalFailed, (totalPassed * 100.0) / (totalPassed + totalFailed));
        System.out.println("==================================================================");

        if (totalFailed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String name, Runnable test) {
        System.out.print("[TEST] " + name + " ... ");
        try {
            test.run();
            System.out.println("PASSED");
            totalPassed++;
        } catch (Throwable t) {
            System.out.println("FAILED");
            System.err.println("       Error: " + t.getMessage());
            t.printStackTrace(System.err);
            totalFailed++;
        }
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new AssertionError("Assertion Failed: " + msg);
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Assertion Failed: " + msg + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    // =========================================================================
    // M1 TESTS
    // =========================================================================

    private static void testKMP() {
        String text = "Fresh organic Rice grains and Brown Rice stock";
        int[] lps = StringAlgorithms.computeLPSArray("ABABCABAB");
        int[] expectedLPS = {0, 0, 1, 2, 0, 1, 2, 3, 4};
        assertTrue(Arrays.equals(lps, expectedLPS), "LPS array mismatch");

        List<Integer> matches = StringAlgorithms.searchKMP(text, "Rice", true);
        assertEquals(2, matches.size(), "Should find 2 occurrences of 'Rice'");
        assertEquals(14, (int) matches.get(0), "First match index");
        assertEquals(36, (int) matches.get(1), "Second match index");

        assertTrue(!StringAlgorithms.containsKMP(text, "Tomato", false), "Should not find 'Tomato'");
    }

    private static void testRabinKarp() {
        String catalog = "Shipment SHP-2026-001 carries product P101, shipment SHP-2026-002 carries P106";
        List<Integer> matchesP101 = StringAlgorithms.searchRabinKarp(catalog, "P101", true);
        assertEquals(1, matchesP101.size(), "Should find 1 match for P101");

        List<Integer> matchesSHP = StringAlgorithms.searchRabinKarp(catalog, "SHP-2026", true);
        assertEquals(2, matchesSHP.size(), "Should find 2 matches for SHP-2026");

        assertTrue(StringAlgorithms.searchRabinKarp(catalog, "P999", true).isEmpty(), "P999 not present");
    }

    private static void testAhoCorasick() {
        List<String> keywords = Arrays.asList("spoiled", "damaged", "mold", "unsafe");
        AhoCorasickScanner scanner = new AhoCorasickScanner(keywords);

        String report = "5% batch spoiled during transit. Some mold detected on crates. Overall unsafe.";
        List<QualityMatch> matches = scanner.scan(report);

        assertEquals(3, matches.size(), "Should detect 3 warning keywords");
        assertEquals("spoiled", matches.get(0).getKeyword(), "First keyword");
        assertEquals("mold", matches.get(1).getKeyword(), "Second keyword");
        assertEquals("unsafe", matches.get(2).getKeyword(), "Third keyword");
    }

    private static void testZFunction() {
        String s = "aabzaa";
        int[] z = StringAlgorithms.computeZArray(s);
        int[] expectedZ = {6, 1, 0, 0, 2, 1};
        assertTrue(Arrays.equals(z, expectedZ), "Z-array calculation mismatch");

        List<Integer> zSearch = StringAlgorithms.searchZ("Premium Rice Flour and White Rice Flour", "Rice", true);
        assertEquals(2, zSearch.size(), "Z-search occurrences match");
    }

    // =========================================================================
    // M2 TESTS
    // =========================================================================

    private static void testSAISAgainstStandard() {
        String text = "banana$";
        int[] saStandard = SuffixAlgorithms.buildSuffixArrayStandard(text);
        int[] saSAIS = SuffixAlgorithms.buildSuffixArraySAIS(text);

        assertTrue(Arrays.equals(saStandard, saSAIS), "SA-IS output must match standard Suffix Array exactly");

        String corpus = "fresh organic grains from green valley farms$";
        int[] sa1 = SuffixAlgorithms.buildSuffixArrayStandard(corpus);
        int[] sa2 = SuffixAlgorithms.buildSuffixArraySAIS(corpus);
        assertTrue(Arrays.equals(sa1, sa2), "SA-IS must match on long agricultural text");
    }

    private static void testKasaiLCP() {
        String text = "banana$";
        int[] sa = SuffixAlgorithms.buildSuffixArrayStandard(text);
        int[] lcp = SuffixAlgorithms.buildKasaiLCP(text, sa);

        String repetitiveText = "Packaging intact, double sealed bag. Packaging intact, verified dry.";
        int[] repSA = SuffixAlgorithms.buildSuffixArraySAIS(repetitiveText);
        int[] repLCP = SuffixAlgorithms.buildKasaiLCP(repetitiveText, repSA);
        String longestDup = SuffixAlgorithms.findLongestRepeatedSubstring(repetitiveText, repSA, repLCP);

        assertEquals("Packaging intact, ", longestDup, "Longest duplicate substring via Kasai LCP");
    }

    private static void testSuffixTree() {
        String text = "organic basmati rice grains";
        SuffixTree tree = new SuffixTree(text);

        assertTrue(tree.contains("basmati"), "Suffix Tree should contain 'basmati'");
        assertTrue(tree.contains("rice"), "Suffix Tree should contain 'rice'");
        assertTrue(!tree.contains("wheat"), "Suffix Tree should not contain 'wheat'");
    }

    private static void testSuffixAutomaton() {
        String text = "Royal Basmati Rice Grain";
        SuffixAutomaton sam = new SuffixAutomaton(text);

        assertTrue(sam.contains("Basmati"), "SAM should match 'Basmati'");
        assertTrue(sam.contains("Grain"), "SAM should match 'Grain'");
        assertTrue(sam.contains("mati"), "SAM should match substring 'mati'");
        assertTrue(!sam.contains("Wheat"), "SAM should not match 'Wheat'");
    }

    // =========================================================================
    // M3 TESTS
    // =========================================================================

    private static void testEditDistances() {
        int lev = DynamicProgramming.levenshteinDistance("kitten", "sitting");
        assertEquals(3, lev, "Levenshtein('kitten', 'sitting') should be 3");

        int dam = DynamicProgramming.damerauLevenshteinDistance("Rcie", "Rice");
        assertEquals(1, dam, "Damerau-Levenshtein('Rcie', 'Rice') transposition cost should be 1");

        int damTrue = DynamicProgramming.damerauLevenshteinDistance("CA", "ABC");
        assertEquals(2, damTrue, "True Damerau-Levenshtein for 'CA' -> 'ABC' must be 2");

        List<String> names = Arrays.asList("Rice", "Wheat", "Tomato", "Potato", "Milk");
        String suggestion = DynamicProgramming.suggestClosest("Tomtao", names, 2);
        assertEquals("Tomato", suggestion, "Should suggest Tomato for 'Tomtao'");
    }

    private static void testBitmaskDP() {
        int[] weights = {4, 3, 5, 2, 4, 3};
        int[] benefits = {40, 30, 50, 25, 45, 35};
        int capacity = 10;

        BitmaskAllocationResult dpResult = DynamicProgramming.solveWarehouseAllocation(weights, benefits, capacity);

        int bestBenefit = 0;
        int n = weights.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            int w = 0, b = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    w += weights[i];
                    b += benefits[i];
                }
            }
            if (w <= capacity && b > bestBenefit) {
                bestBenefit = b;
            }
        }

        assertEquals(bestBenefit, dpResult.totalBenefit, "Bitmask DP must match brute force optimal benefit");
        assertTrue(dpResult.totalWeightUsed <= capacity, "Allocated weight must not exceed capacity");
    }

    private static void testMCM() {
        int[] dims = {10, 20, 30, 40, 30};
        MCMResult res = DynamicProgramming.solveMCM(dims);
        assertEquals(30000, res.minScalarMultiplications, "MCM cost for {10, 20, 30, 40, 30} should be 30000");
        assertTrue(res.optimalParenthesization.contains("M"), "Should generate valid parenthesization expression");
    }

    private static void testOptimalBST() {
        String[] keys = {"Apple", "Milk", "Rice"};
        Object[] data = {1, 2, 3};
        double[] freq = {0.2, 0.5, 0.3};

        OBSTResult obst = DynamicProgramming.buildOptimalBST(keys, data, freq);
        assertTrue(obst.root != null, "OBST root must not be null");
        assertEquals("Milk", obst.root.key, "Highest frequency key 'Milk' should be root");

        OBSTSearchResult searchRes = obst.search("Milk");
        assertTrue(searchRes.found, "Should find 'Milk'");
        assertEquals(1, searchRes.comparisons, "Root lookup takes exactly 1 comparison");
    }

    private static void testOrderLogic() {
        List<Product> products = SampleData.cloneProducts(SampleData.getSampleProducts());
        Product p = products.get(0);
        double initialStock = p.getQuantityKg();

        ProductService prodService = new ProductService(products, SampleData.getSampleSuppliers(), SampleData.getSampleShipments());
        OrderService orderService = new OrderService(prodService);

        double orderQty = 25.0;
        p.reduceQuantity(orderQty);
        assertEquals(initialStock - orderQty, p.getQuantityKg(), "Product stock must be accurately deducted");
    }

    private static void testDemoIsolation() {
        List<Product> original = SampleData.getSampleProducts();
        double origRiceStock = original.get(0).getQuantityKg();

        SupplyChainDemo.runDemo();

        assertEquals(origRiceStock, original.get(0).getQuantityKg(),
                "Demo must operate on cloned data and not mutate original session inventory");
    }

    // =========================================================================
    // M4 TESTS: NETWORK FLOW
    // =========================================================================

    private static FlowNetwork createTestNetwork() {
        // Classic 4-node flow network:
        // 0(s) -> 1 (cap 10)
        // 0(s) -> 2 (cap 10)
        // 1 -> 2 (cap 1)
        // 1 -> 3(t) (cap 10)
        // 2 -> 3(t) (cap 10)
        // Max Flow = 20
        FlowNetwork net = new FlowNetwork(4);
        net.addEdge(0, 1, 10.0);
        net.addEdge(0, 2, 10.0);
        net.addEdge(1, 2, 1.0);
        net.addEdge(1, 3, 10.0);
        net.addEdge(2, 3, 10.0);
        return net;
    }

    private static void testFordFulkerson() {
        FlowNetwork net = createTestNetwork();
        MaxFlowResult res = NetworkFlowAlgorithms.solveFordFulkerson(net, 0, 3);
        assertEquals(20.0, res.maxFlow, "Ford-Fulkerson max flow on test network must be 20.0");
        assertTrue(res.augmentingPathsFound > 0, "Must find augmenting paths");
    }

    private static void testEdmondsKarp() {
        FlowNetwork net = createTestNetwork();
        MaxFlowResult res = NetworkFlowAlgorithms.solveEdmondsKarp(net, 0, 3);
        assertEquals(20.0, res.maxFlow, "Edmonds-Karp max flow must be 20.0");

        // Verify flow conservation at intermediate nodes 1 and 2
        double netFlow1 = 0.0;
        for (FlowEdge e : net.edges) {
            if (e.v == 1) netFlow1 += e.flow;
            if (e.u == 1) netFlow1 -= e.flow;
        }
        assertEquals(0.0, Math.abs(netFlow1), "Flow conservation at node 1");
    }

    private static void testDinic() {
        FlowNetwork net = createTestNetwork();
        MaxFlowResult res = NetworkFlowAlgorithms.solveDinic(net, 0, 3);
        assertEquals(20.0, res.maxFlow, "Dinic max flow must equal 20.0");
    }

    private static void testBipartiteMatching() {
        List<String> suppliers = Arrays.asList("SupA", "SupB", "SupC");
        List<String> warehouses = Arrays.asList("WhX", "WhY", "WhZ");

        Map<String, Set<String>> eligibility = new HashMap<>();
        eligibility.put("SupA", new HashSet<>(Arrays.asList("WhX", "WhY")));
        eligibility.put("SupB", new HashSet<>(Collections.singletonList("WhX")));
        eligibility.put("SupC", new HashSet<>(Arrays.asList("WhY", "WhZ")));

        BipartiteMatchResult match = NetworkFlowAlgorithms.assignSuppliersToWarehouses(suppliers, warehouses, eligibility);
        assertEquals(3, match.maxMatchingSize, "Should find perfect matching of size 3");
        assertEquals(0, match.unassignedSuppliers.size(), "All suppliers matched");
        assertEquals("WhX", match.supplierToWarehouse.get("SupB"), "SupB must be assigned to WhX (only option)");
    }

    private static void testMinCut() {
        FlowNetwork net = createTestNetwork();
        NetworkFlowAlgorithms.solveDinic(net, 0, 3);
        MinCutResult cut = NetworkFlowAlgorithms.findMinCutBottlenecks(net, 0);

        assertEquals(20.0, cut.cutCapacity, "Min-Cut capacity must equal Max Flow (20.0)");
        assertTrue(cut.sourceSet.contains(0), "Source set must contain node 0");
        assertTrue(cut.sinkSet.contains(3), "Sink set must contain node 3");
        assertTrue(!cut.bottleneckEdges.isEmpty(), "Must identify bottleneck edges");
    }

    // =========================================================================
    // M5 TESTS: NP-COMPLETENESS & APPROXIMATION
    // =========================================================================

    private static void testSATDPLL() {
        // Satisfiable formula: (x1 v x2) ^ (~x1 v x2)
        List<Clause> formula = Arrays.asList(
                Clause.of(1, 2),
                Clause.of(-1, 2)
        );
        SATResult sat = NPCompletenessAlgorithms.solveSAT(formula, 2);
        assertTrue(sat.satisfiable, "Formula should be SAT");
        assertEquals(true, sat.model.get(2), "x2 must be true for satisfaction");

        // Unsatisfiable formula: (x1) ^ (~x1)
        List<Clause> unsatFormula = Arrays.asList(
                Clause.of(1),
                Clause.of(-1)
        );
        SATResult unsat = NPCompletenessAlgorithms.solveSAT(unsatFormula, 1);
        assertTrue(!unsat.satisfiable, "Contradictory unit clauses must be UNSAT");
    }

    private static void test3SatToCliqueReduction() {
        List<Clause> clauses = Arrays.asList(
                Clause.of(1, 2, 3),
                Clause.of(-1, 2, 3)
        );
        CliqueReductionResult red = NPCompletenessAlgorithms.reduce3SatToClique(clauses);
        assertEquals(2, red.targetCliqueSize, "Target clique size equals number of clauses k = 2");
        assertEquals(6, red.graph.V, "Vertices in reduction graph equals 3 * k = 6");
        assertEquals(2, red.cliqueVertices.size(), "Must find a clique of size k = 2");
        assertTrue(red.derivedModel.size() > 0, "Derived model must assign variables");
    }

    private static void testReductionChain() {
        // Simple cycle graph on 4 vertices: C4
        // Edges: (0,1), (1,2), (2,3), (3,0)
        ReductionGraph g = new ReductionGraph(4, Arrays.asList("v0", "v1", "v2", "v3"));
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);

        ReductionChainResult chain = NPCompletenessAlgorithms.demonstrateReductionChain(g);
        assertEquals(2, chain.maxIndependentSetInConflict.size(), "Max IS of C4 is 2");
        assertEquals(2, chain.minVertexCoverInConflict.size(), "Min VC of C4 is 2");

        // Karp Reduction Identity: |IS| + |VC| == |V|
        assertEquals(g.V, chain.maxIndependentSetInConflict.size() + chain.minVertexCoverInConflict.size(),
                "Identity |IS| + |VC| == |V| must strictly hold");
    }

    private static void testVertexCover2Approx() {
        // Triangle graph K3: optimal cover size is 2
        ReductionGraph g = new ReductionGraph(3, Arrays.asList("A", "B", "C"));
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(0, 2);

        VertexCoverApproxResult approx = NPCompletenessAlgorithms.solveVertexCover2Approx(g);
        assertTrue(approx.approximateCover.size() <= 2 * approx.exactOptimalCover.size(),
                "2-Approximation bound |C| <= 2 * |C*| must hold");
        assertTrue(approx.empiricalRatio <= 2.0, "Empirical ratio must be <= 2.0");
    }

    // =========================================================================
    // M6 TESTS: RANDOMIZED & PARALLEL ALGORITHMS
    // =========================================================================

    private static void testRandomizedQuickSort() {
        List<Integer> list = Arrays.asList(45, 12, 85, 32, 89, 39, 69, 44, 42, 1, 45);
        List<Integer> expected = new ArrayList<>(list);
        Collections.sort(expected);

        ParallelRandomizedAlgorithms.randomizedQuickSort(list, Integer::compare);
        assertEquals(expected, list, "Randomized QuickSort must correctly sort list in ascending order");

        // Benchmark on sorted input
        QuickSortBenchmarkResult bench = ParallelRandomizedAlgorithms.compareQuickSortOnSortedData(100);
        assertTrue(bench.randomizedComparisons > 0, "Randomized quicksort executed comparisons");
    }

    private static void testReservoirSampling() {
        List<Integer> stream = new ArrayList<>();
        for (int i = 1; i <= 200; i++) stream.add(i);

        int k = 10;
        ReservoirResult<Integer> res = ParallelRandomizedAlgorithms.sampleReservoir(stream.iterator(), k);
        assertEquals(k, res.sampledReservoir.size(), "Reservoir must contain exactly k items");
        assertEquals(200, res.totalStreamItemsSeen, "Must see all 200 stream items");

        Set<Integer> uniqueItems = new HashSet<>(res.sampledReservoir);
        assertEquals(k, uniqueItems.size(), "Sampled items in reservoir must all be unique");
    }

    private static void testMillerRabin() {
        assertTrue(ParallelRandomizedAlgorithms.testPrimality(1000000007L, 20).isPrime, "1000000007 is Prime");
        assertTrue(ParallelRandomizedAlgorithms.testPrimality(2147483647L, 20).isPrime, "2^31 - 1 is Prime");
        assertTrue(!ParallelRandomizedAlgorithms.testPrimality(1000000005L, 20).isPrime, "1000000005 is Composite");
        assertTrue(!ParallelRandomizedAlgorithms.testPrimality(4L, 20).isPrime, "4 is Composite");
    }

    private static void testBlellochScan() {
        long[] input = {3, 1, 7, 0, 4, 1, 6, 3};
        BlellochScanResult scan = ParallelRandomizedAlgorithms.executeBlellochScan(input);

        long[] expectedExclusive = {0, 3, 4, 11, 11, 15, 16, 22};
        long[] expectedInclusive = {3, 4, 11, 11, 15, 16, 22, 25};

        assertTrue(Arrays.equals(expectedExclusive, scan.exclusivePrefixSum), "Exclusive prefix sum match");
        assertTrue(Arrays.equals(expectedInclusive, scan.inclusivePrefixSum), "Inclusive prefix sum match");
        assertEquals(8, scan.paddedLength, "Padded size should be 8");
        assertEquals(3, scan.treeDepth, "Tree depth should be log2(8) = 3");
    }

    private static void testParallelReduce() {
        double[] vals = new double[1000];
        double expectedSum = 0.0;
        for (int i = 0; i < vals.length; i++) {
            vals[i] = i + 1.0;
            expectedSum += vals[i];
        }

        ParallelReduceResult res = ParallelRandomizedAlgorithms.executeParallelReduce(vals);
        assertEquals(expectedSum, res.totalSum, "Parallel reduce sum must match sequential summation");
        assertEquals(1.0, res.minValue, "Min value must be 1.0");
        assertEquals(1000.0, res.maxValue, "Max value must be 1000.0");
    }

    private static void testBrentsTheorem() {
        double w = 1000.0;
        double d = 10.0;
        List<BrentScalingPoint> pts = ParallelRandomizedAlgorithms.evaluateBrentsTheorem(w, d, new int[]{1, 2, 4});

        assertEquals(1, pts.get(0).processorCount, "p = 1");
        assertEquals(1000.0, pts.get(0).theoreticalTimeTp, "T_1 = W = 1000.0");
        assertEquals(1.0, pts.get(0).speedup, "Speedup at 1 core = 1.0");

        // At p = 2: T_2 <= (1000 - 10)/2 + 10 = 495 + 10 = 505.0
        assertEquals(505.0, pts.get(1).theoreticalTimeTp, "T_2 <= ((W - D)/2) + D = 505.0");
        assertTrue(pts.get(1).speedup > 1.9, "Speedup at 2 cores > 1.9x");
    }
}
