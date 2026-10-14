package foodchain;

import foodchain.algorithms.ParallelRandomizedAlgorithms;
import foodchain.algorithms.ParallelRandomizedAlgorithms.*;

import java.util.*;

/**
 * Handles high-performance randomized algorithms, streaming telemetry, and parallel analytics.
 * Integrates Module M6:
 * 1. Randomized QuickSort for customer demand ranking
 * 2. Reservoir Sampling (Algorithm R) for continuous inventory telemetry streams
 * 3. Miller-Rabin primality testing for digital food-batch crypto verification
 * 4. Blelloch Work-Efficient Parallel Prefix Scan for cumulative inventory analysis
 * 5. Parallel Reduce (ForkJoin) for catalog supply statistics
 * 6. Brent's Theorem parallel efficiency & processor scaling calculator
 */
public class ParallelAnalyticsService {

    private final List<Product> products;

    public ParallelAnalyticsService(List<Product> products) {
        this.products = products;
    }

    public void executeAnalyticsMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- High-Performance Parallel & Randomized Analytics (M6) ---");
            System.out.println("1. Rank Products by Customer Demand (Randomized QuickSort)");
            System.out.println("2. Continuous Inventory Stream Sampling (Reservoir Sampling Algorithm R)");
            System.out.println("3. Verify Batch Security Tokens & Crypto Hashes (Miller-Rabin Primality)");
            System.out.println("4. Cumulative Inventory Load Analysis (Blelloch Work-Efficient Parallel Scan)");
            System.out.println("5. Multi-Core Supply Statistics Reduction (ForkJoin Parallel Reduce)");
            System.out.println("6. Parallel Processor Scaling Calculator (Brent's Scheduling Theorem)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select analytics operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runRandomizedQuickSortWorkflow();
                    break;
                case "2":
                    runReservoirSamplingWorkflow();
                    break;
                case "3":
                    runMillerRabinWorkflow(scanner);
                    break;
                case "4":
                    runBlellochScanWorkflow();
                    break;
                case "5":
                    runParallelReduceWorkflow();
                    break;
                case "6":
                    runBrentsTheoremWorkflow();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid selection. Please enter a choice between 0 and 6.");
            }
        }
    }

    public void runRandomizedQuickSortWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("          PRODUCT DEMAND RANKING (RANDOMIZED QUICKSORT O(N log N))               ");
        System.out.println("=================================================================================");

        List<Product> rankingList = new ArrayList<>(products);
        // Sort descending by search frequency / demand
        ParallelRandomizedAlgorithms.randomizedQuickSort(rankingList, (a, b) -> Integer.compare(b.getDemandScore(), a.getDemandScore()));

        System.out.println("Products Ranked by Demand / Search Frequency (Top 10):");
        System.out.printf("%-4s | %-6s | %-18s | %-12s | %-12s%n", "Rank", "ID", "Product Name", "Demand Score", "Price/kg");
        System.out.println("---------------------------------------------------------------------------------");
        for (int i = 0; i < Math.min(10, rankingList.size()); i++) {
            Product p = rankingList.get(i);
            System.out.printf("#%-3d | %-6s | %-18s | %-12d | Rs. %-8.2f%n",
                    i + 1, p.getProductId(), p.getProductName(), p.getDemandScore(), p.getPricePerKg());
        }

        System.out.println("\n--- Benchmark on Pre-Sorted Array (N = 5,000 items) ---");
        QuickSortBenchmarkResult bench = ParallelRandomizedAlgorithms.compareQuickSortOnSortedData(5000);
        System.out.printf("Deterministic QuickSort (First Pivot): %,d comparisons (Degrades towards O(N^2))%n",
                bench.deterministicComparisons);
        System.out.printf("Randomized Pivot QuickSort          : %,d comparisons (Guaranteed O(N log N))%n",
                bench.randomizedComparisons);
        System.out.printf("Randomized Pivot Advantage          : %.1fx fewer comparisons on sorted data%n",
                bench.speedupFactor);
        System.out.println("=================================================================================");
    }

    public static class InventoryEvent {
        public final int eventId;
        public final String timestamp;
        public final String productId;
        public final String warehouseId;
        public final double temperatureCelsius;
        public final String eventType;

        public InventoryEvent(int eventId, String timestamp, String productId, String warehouseId, double temp, String type) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.productId = productId;
            this.warehouseId = warehouseId;
            this.temperatureCelsius = temp;
            this.eventType = type;
        }

        @Override
        public String toString() {
            return String.format("[EVT-%04d] %s | %s at %s | Temp: %4.1f C | %s",
                    eventId, timestamp, productId, warehouseId, temperatureCelsius, eventType);
        }
    }

    public void runReservoirSamplingWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("      CONTINUOUS INVENTORY STREAM SAMPLING (RESERVOIR SAMPLING ALGORITHM R)     ");
        System.out.println("=================================================================================");

        int streamSize = 1000;
        int reservoirSize = 5;

        // Generate a simulated stream of real-time inventory telemetry events
        List<InventoryEvent> stream = new ArrayList<>();
        Random rng = new Random(101);
        String[] types = {"DISPATCH", "RECEIVE", "COLD_CHECK", "PALLET_MOVE", "QUALITY_SAMPLE"};
        for (int i = 1; i <= streamSize; i++) {
            stream.add(new InventoryEvent(
                    i, "14-Oct 22:" + String.format("%02d:%02d", (i / 60) % 60, i % 60),
                    "P1" + String.format("%02d", 1 + rng.nextInt(20)),
                    "WH0" + (1 + rng.nextInt(3)),
                    2.0 + (rng.nextDouble() * 8.0),
                    types[rng.nextInt(types.length)]
            ));
        }

        System.out.printf("Streaming Continuous Inventory Events: %,d items arriving sequentially...%n", streamSize);
        System.out.printf("Maintaining Fixed Reservoir Buffer   : k = %d items in memory%n", reservoirSize);

        ReservoirResult<InventoryEvent> res = ParallelRandomizedAlgorithms.sampleReservoir(stream.iterator(), reservoirSize);

        System.out.println("\nUniform Random Reservoir Sampled from Continuous Stream:");
        for (InventoryEvent e : res.sampledReservoir) {
            System.out.println("  " + e);
        }

        System.out.println("\nMathematical Guarantee:");
        System.out.printf("  Each event from %,d total items had EXACT uniform selection probability: %.4f%% (k / N = %d / %d)%n",
                res.totalStreamItemsSeen, res.selectionProbability * 100.0, reservoirSize, streamSize);
        System.out.println("  Memory Footprint: O(k) strictly constant space independent of stream length N.");
        System.out.println("=================================================================================");
    }

    public void runMillerRabinWorkflow(Scanner scanner) {
        System.out.println("\n=================================================================================");
        System.out.println("    BATCH SECURITY TOKEN CRYPTO VERIFICATION (MILLER-RABIN PRIMALITY TEST)      ");
        System.out.println("=================================================================================");
        System.out.println("FoodChain uses large prime security tokens for cryptographic batch traceability");
        System.out.println("to prevent counterfeit produce certification.\n");

        long[] testTokens = {
                1000000007L,       // Standard 10^9+7 Prime
                2147483647L,       // 2^31 - 1 Mersenne Prime
                1000000009L,       // Twin Prime
                999999999989L,     // 12-digit Prime
                1000000005L,       // Composite (divisible by 5)
                1000000000000L     // Composite
        };

        System.out.println("Verifying Sample Supply Chain Security Authentication Tokens (20 Miller-Rabin Rounds):");
        for (long token : testTokens) {
            MillerRabinResult r = ParallelRandomizedAlgorithms.testPrimality(token, 20);
            if (r.isPrime) {
                System.out.printf("  Token %,15d: [AUTHENTIC PRIME BATCH TOKEN] (Error probability < 10^-12)%n", token);
            } else {
                System.out.printf("  Token %,15d: [INVALID / COMPOSITE] (Witness base: %s)%n", token, r.compositeWitness);
            }
        }
        System.out.println("=================================================================================");
    }

    public void runBlellochScanWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("  CUMULATIVE INVENTORY LOAD ANALYSIS (BLELLOCH WORK-EFFICIENT PARALLEL SCAN)     ");
        System.out.println("=================================================================================");

        // Warehouse hourly intake of grain shipments (in tons) across 8 operating time slots
        long[] hourlyTons = {15, 22, 18, 30, 25, 40, 35, 20};

        System.out.print("Hourly Intake Stream (8 Shifts, Tons): [");
        for (int i = 0; i < hourlyTons.length; i++) {
            System.out.print(hourlyTons[i] + (i < hourlyTons.length - 1 ? ", " : ""));
        }
        System.out.println("]");

        BlellochScanResult scan = ParallelRandomizedAlgorithms.executeBlellochScan(hourlyTons);

        System.out.println("\nBlelloch Parallel Tree Execution Details:");
        System.out.printf("  Padded Tree Size  : %d leaves (next power of 2)%n", scan.paddedLength);
        System.out.printf("  Tree Depth / Span : %d levels (O(log N))%n", scan.treeDepth);
        System.out.printf("  Total Operations  : %d operations (Strictly O(N) Work-Efficient)%n", scan.totalWorkOperations);

        System.out.println("\nResults Computed by Parallel Up-Sweep & Down-Sweep:");
        System.out.printf("  Exclusive Cumulative Intake : %s tons%n", Arrays.toString(scan.exclusivePrefixSum));
        System.out.printf("  Inclusive Total Stock Pile  : %s tons%n", Arrays.toString(scan.inclusivePrefixSum));
        System.out.println("=================================================================================");
    }

    public void runParallelReduceWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("     MULTI-CORE SUPPLY REDUCTION (FORKJOIN PARALLEL DIVIDE-AND-CONQUER)         ");
        System.out.println("=================================================================================");

        // Extract product catalog inventory weights
        double[] catalogWeights = new double[products.size()];
        for (int i = 0; i < products.size(); i++) {
            catalogWeights[i] = products.get(i).getQuantityKg();
        }

        ParallelReduceResult res = ParallelRandomizedAlgorithms.executeParallelReduce(catalogWeights);

        System.out.printf("Parallel Thread Pool Size : %d active CPU worker threads%n", res.parallelThreadsUsed);
        System.out.printf("Catalog Items Reduced     : %d products%n", res.sampleCount);
        System.out.printf("Total Food Stock Sum      : %.1f kg (%.2f metric tons)%n", res.totalSum, res.totalSum / 1000.0);
        System.out.printf("Average Batch Weight      : %.1f kg%n", res.meanValue);
        System.out.printf("Minimum Stock Quantity    : %.1f kg%n", res.minValue);
        System.out.printf("Maximum Stock Quantity    : %.1f kg%n", res.maxValue);
        System.out.printf("Parallel Reduction Time   : %,d ns%n", res.elapsedNanoSeconds);
        System.out.println("=================================================================================");
    }

    public void runBrentsTheoremWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("    PARALLEL PROCESSOR SCALING EVALUATOR (BRENT'S SCHEDULING THEOREM)            ");
        System.out.println("=================================================================================");

        // Large enterprise food supply processing job:
        // Total Work W = 100,000 operations
        // Span / Critical Path Depth D = 20 operations (log2(100,000) ~ 17 + merge overhead)
        double workW = 100000.0;
        double depthD = 20.0;
        int[] cores = {1, 2, 4, 8, 16, 32, 64};

        System.out.println("Supply Chain Parallel Pipeline Parameters:");
        System.out.printf("  Total Work (W) : %,.0f sequential unit operations%n", workW);
        System.out.printf("  Critical Span  : %,.0f operations (Tree Depth D)%n", depthD);
        System.out.println("  Theorem Bound  : T_p <= ((W - D) / p) + D\n");

        List<BrentScalingPoint> points = ParallelRandomizedAlgorithms.evaluateBrentsTheorem(workW, depthD, cores);

        System.out.printf("%-10s | %-16s | %-12s | %-14s%n", "Processors", "Time T_p", "Speedup S_p", "Efficiency");
        System.out.println("---------------------------------------------------------------------------------");
        for (BrentScalingPoint pt : points) {
            System.out.printf("%-10d | %12.1f ops | %10.2fx | %10.2f%%%n",
                    pt.processorCount, pt.theoreticalTimeTp, pt.speedup, pt.efficiencyPercent);
        }
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Architectural Insight: Near-linear speedup up to 16 cores, after which critical span D");
        System.out.println("places an asymptotic lower bound on latency (Amdahl & Brent limit).");
        System.out.println("=================================================================================");
    }
}
