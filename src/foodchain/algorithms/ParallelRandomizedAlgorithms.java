package foodchain.algorithms;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * High-performance Randomized and Parallel Algorithms for FoodChain.
 * Implements Module M6 (CO6):
 * 1. Randomized QuickSort with random pivot selection for ranking products by demand.
 * 2. Reservoir Sampling (Algorithm R) for continuous real-time inventory event streams.
 * 3. Miller-Rabin Primality Test for large numerical processing and batch crypto tokens.
 * 4. Blelloch Work-Efficient Parallel Prefix Scan for cumulative inventory analytics.
 * 5. Parallel Reduce (ForkJoin divide-and-conquer) for large catalog supply statistics.
 * 6. Brent's Theorem theoretical evaluation and speedup/efficiency calculator.
 */
public class ParallelRandomizedAlgorithms {

    private static final Random RNG = new Random(42); // Seeded for reproducibility

    // =========================================================================
    // 1. RANDOMIZED QUICKSORT (DEMAND RANKING)
    // =========================================================================

    public static class QuickSortBenchmarkResult {
        public final int arraySize;
        public final long deterministicComparisons;
        public final long randomizedComparisons;
        public final double speedupFactor;

        public QuickSortBenchmarkResult(int size, long det, long rand, double factor) {
            this.arraySize = size;
            this.deterministicComparisons = det;
            this.randomizedComparisons = rand;
            this.speedupFactor = factor;
        }
    }

    /**
     * In-place Randomized QuickSort for sorting lists of items with custom comparator.
     * Prevents O(N^2) degradation on sorted or adversary-ordered input.
     */
    public static <T> void randomizedQuickSort(List<T> list, Comparator<T> comp) {
        if (list == null || list.size() <= 1) return;
        randomizedQuickSortRec(list, 0, list.size() - 1, comp, RNG);
    }

    private static <T> void randomizedQuickSortRec(List<T> list, int low, int high, Comparator<T> comp, Random rng) {
        if (low < high) {
            int pivotIndex = partitionRandomized(list, low, high, comp, rng);
            randomizedQuickSortRec(list, low, pivotIndex - 1, comp, rng);
            randomizedQuickSortRec(list, pivotIndex + 1, high, comp, rng);
        }
    }

    private static <T> int partitionRandomized(List<T> list, int low, int high, Comparator<T> comp, Random rng) {
        int randomPivot = low + rng.nextInt(high - low + 1);
        swap(list, randomPivot, high);

        T pivotValue = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comp.compare(list.get(j), pivotValue) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Benchmarks Deterministic (first element pivot) vs Randomized QuickSort on already sorted data.
     */
    public static QuickSortBenchmarkResult compareQuickSortOnSortedData(int n) {
        int[] sortedDet = new int[n];
        int[] sortedRand = new int[n];
        for (int i = 0; i < n; i++) {
            sortedDet[i] = i;
            sortedRand[i] = i;
        }

        long[] detComparisons = new long[]{0};
        deterministicQuickSortCount(sortedDet, 0, n - 1, detComparisons);

        long[] randComparisons = new long[]{0};
        Random rng = new Random(12345);
        randomizedQuickSortCount(sortedRand, 0, n - 1, randComparisons, rng);

        double factor = (double) detComparisons[0] / Math.max(1, randComparisons[0]);
        return new QuickSortBenchmarkResult(n, detComparisons[0], randComparisons[0], factor);
    }

    private static void deterministicQuickSortCount(int[] arr, int low, int high, long[] compCount) {
        if (low < high) {
            int pivot = arr[low]; // Deterministic first element
            int i = low + 1;
            int j = high;

            while (i <= j) {
                while (i <= high) {
                    compCount[0]++;
                    if (arr[i] > pivot) break;
                    i++;
                }
                while (j >= low + 1) {
                    compCount[0]++;
                    if (arr[j] <= pivot) break;
                    j--;
                }
                if (i < j) {
                    int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
                }
            }
            int tmp = arr[low]; arr[low] = arr[j]; arr[j] = tmp;
            deterministicQuickSortCount(arr, low, j - 1, compCount);
            deterministicQuickSortCount(arr, j + 1, high, compCount);
        }
    }

    private static void randomizedQuickSortCount(int[] arr, int low, int high, long[] compCount, Random rng) {
        if (low < high) {
            int pIdx = low + rng.nextInt(high - low + 1);
            int tmp = arr[pIdx]; arr[pIdx] = arr[high]; arr[high] = tmp;

            int pivot = arr[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                compCount[0]++;
                if (arr[j] <= pivot) {
                    i++;
                    int t = arr[i]; arr[i] = arr[j]; arr[j] = t;
                }
            }
            int t = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = t;
            int pi = i + 1;

            randomizedQuickSortCount(arr, low, pi - 1, compCount, rng);
            randomizedQuickSortCount(arr, pi + 1, high, compCount, rng);
        }
    }

    // =========================================================================
    // 2. RESERVOIR SAMPLING (ALGORITHM R FOR STREAMING INVENTORY)
    // =========================================================================

    public static class ReservoirResult<T> {
        public final int sampleCapacityK;
        public final int totalStreamItemsSeen;
        public final List<T> sampledReservoir;
        public final double selectionProbability;

        public ReservoirResult(int k, int total, List<T> reservoir) {
            this.sampleCapacityK = k;
            this.totalStreamItemsSeen = total;
            this.sampledReservoir = reservoir;
            this.selectionProbability = (total == 0) ? 0.0 : Math.min(1.0, (double) k / total);
        }
    }

    /**
     * Reservoir Sampling (Algorithm R by Jeffrey Vitter).
     * Samples exactly k items with uniform probability (k / N) from an arbitrary or streaming sequence.
     */
    public static <T> ReservoirResult<T> sampleReservoir(Iterator<T> stream, int k) {
        List<T> reservoir = new ArrayList<>(k);
        int count = 0;
        Random rng = new Random();

        while (stream.hasNext()) {
            T item = stream.next();
            count++;

            if (count <= k) {
                reservoir.add(item);
            } else {
                int j = rng.nextInt(count); // Random integer in [0, count - 1]
                if (j < k) {
                    reservoir.set(j, item);
                }
            }
        }

        return new ReservoirResult<>(k, count, reservoir);
    }

    // =========================================================================
    // 3. MILLER-RABIN PRIMALITY TEST (BATCH SECURITY CRYPTO TOKENS)
    // =========================================================================

    public static class MillerRabinResult {
        public final long number;
        public final boolean isPrime;
        public final int roundsTested;
        public final Long compositeWitness;

        public MillerRabinResult(long number, boolean isPrime, int rounds, Long witness) {
            this.number = number;
            this.isPrime = isPrime;
            this.roundsTested = rounds;
            this.compositeWitness = witness;
        }
    }

    /**
     * Miller-Rabin probabilistic primality test for 64-bit integers.
     * Error probability bounded by 4^(-rounds).
     */
    public static MillerRabinResult testPrimality(long n, int rounds) {
        if (n <= 1) return new MillerRabinResult(n, false, 0, null);
        if (n <= 3) return new MillerRabinResult(n, true, 0, null);
        if (n % 2 == 0) return new MillerRabinResult(n, false, 1, 2L);

        // Write n - 1 as 2^s * d with d odd
        long d = n - 1;
        int s = 0;
        while (d % 2 == 0) {
            d /= 2;
            s++;
        }

        Random rng = new Random();
        for (int r = 0; r < rounds; r++) {
            // Pick random base a in [2, n - 2]
            long a = 2 + (Math.abs(rng.nextLong()) % (n - 3));
            long x = modularExponentiation(a, d, n);

            if (x == 1 || x == n - 1) {
                continue;
            }

            boolean composite = true;
            for (int i = 0; i < s - 1; i++) {
                x = modularMultiply(x, x, n);
                if (x == n - 1) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                return new MillerRabinResult(n, false, r + 1, a);
            }
        }

        return new MillerRabinResult(n, true, rounds, null);
    }

    private static long modularExponentiation(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = modularMultiply(result, base, mod);
            }
            base = modularMultiply(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    private static long modularMultiply(long a, long b, long mod) {
        return BigInteger.valueOf(a)
                .multiply(BigInteger.valueOf(b))
                .mod(BigInteger.valueOf(mod))
                .longValue();
    }

    // =========================================================================
    // 4. BLELLOCH PARALLEL PREFIX SCAN (WORK-EFFICIENT PREFIX SUM)
    // =========================================================================

    public static class BlellochScanResult {
        public final long[] originalInput;
        public final long[] exclusivePrefixSum;
        public final long[] inclusivePrefixSum;
        public final int paddedLength;
        public final int treeDepth;
        public final long totalWorkOperations;

        public BlellochScanResult(long[] original, long[] exclusive, long[] inclusive, int padded, int depth, long work) {
            this.originalInput = original;
            this.exclusivePrefixSum = exclusive;
            this.inclusivePrefixSum = inclusive;
            this.paddedLength = padded;
            this.treeDepth = depth;
            this.totalWorkOperations = work;
        }
    }

    /**
     * Blelloch Work-Efficient Parallel Prefix Scan.
     * Computes cumulative inventory loads across warehouse bays / time slots.
     * Total Work: O(N) linear operations.
     * Depth / Span: O(log N).
     */
    public static BlellochScanResult executeBlellochScan(long[] input) {
        int originalN = input.length;
        if (originalN == 0) {
            return new BlellochScanResult(new long[0], new long[0], new long[0], 0, 0, 0);
        }

        // Pad to next power of 2
        int m = 1;
        while (m < originalN) m <<= 1;

        long[] tree = new long[m];
        System.arraycopy(input, 0, tree, 0, originalN);

        long workOperations = 0;
        int depth = 0;

        // Phase 1: Up-Sweep (Parallel Reduce)
        for (int d = 1; d < m; d <<= 1) {
            depth++;
            int step = d << 1;
            for (int i = 0; i < m; i += step) {
                tree[i + step - 1] += tree[i + d - 1];
                workOperations++;
            }
        }

        // Set root element to 0
        tree[m - 1] = 0;

        // Phase 2: Down-Sweep
        for (int d = m >> 1; d >= 1; d >>= 1) {
            int step = d << 1;
            for (int i = 0; i < m; i += step) {
                long t = tree[i + d - 1];
                tree[i + d - 1] = tree[i + step - 1];
                tree[i + step - 1] += t;
                workOperations += 2;
            }
        }

        long[] exclusive = new long[originalN];
        long[] inclusive = new long[originalN];
        System.arraycopy(tree, 0, exclusive, 0, originalN);

        for (int i = 0; i < originalN; i++) {
            inclusive[i] = exclusive[i] + input[i];
        }

        return new BlellochScanResult(input.clone(), exclusive, inclusive, m, depth, workOperations);
    }

    // =========================================================================
    // 5. PARALLEL REDUCE (FORK-JOIN DIVIDE & CONQUER)
    // =========================================================================

    public static class ParallelReduceResult {
        public final double totalSum;
        public final double minValue;
        public final double maxValue;
        public final double meanValue;
        public final int sampleCount;
        public final int parallelThreadsUsed;
        public final long elapsedNanoSeconds;

        public ParallelReduceResult(double sum, double min, double max, int count, int threads, long time) {
            this.totalSum = sum;
            this.minValue = min;
            this.maxValue = max;
            this.meanValue = (count == 0) ? 0.0 : sum / count;
            this.sampleCount = count;
            this.parallelThreadsUsed = threads;
            this.elapsedNanoSeconds = time;
        }
    }

    private static class ReduceAggregateTask extends RecursiveTask<double[]> {
        private static final int THRESHOLD = 250;
        private final double[] data;
        private final int start;
        private final int end;

        public ReduceAggregateTask(double[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected double[] compute() {
            if (end - start <= THRESHOLD) {
                double sum = 0.0;
                double min = Double.POSITIVE_INFINITY;
                double max = Double.NEGATIVE_INFINITY;
                for (int i = start; i < end; i++) {
                    double val = data[i];
                    sum += val;
                    if (val < min) min = val;
                    if (val > max) max = val;
                }
                return new double[]{sum, min, max};
            }

            int mid = (start + end) >>> 1;
            ReduceAggregateTask left = new ReduceAggregateTask(data, start, mid);
            ReduceAggregateTask right = new ReduceAggregateTask(data, mid, end);
            left.fork();
            double[] rightResult = right.compute();
            double[] leftResult = left.join();

            return new double[]{
                    leftResult[0] + rightResult[0],
                    Math.min(leftResult[1], rightResult[1]),
                    Math.max(leftResult[2], rightResult[2])
            };
        }
    }

    /**
     * Executes parallel divide-and-conquer reduction using ForkJoinPool.
     */
    public static ParallelReduceResult executeParallelReduce(double[] values) {
        if (values == null || values.length == 0) {
            return new ParallelReduceResult(0, 0, 0, 0, 1, 0);
        }

        int parallelism = Runtime.getRuntime().availableProcessors();
        ForkJoinPool pool = new ForkJoinPool(parallelism);

        long start = System.nanoTime();
        double[] stats = pool.invoke(new ReduceAggregateTask(values, 0, values.length));
        long elapsed = System.nanoTime() - start;

        return new ParallelReduceResult(stats[0], stats[1], stats[2], values.length, parallelism, elapsed);
    }

    // =========================================================================
    // 6. BRENT'S THEOREM THEORETICAL EVALUATOR
    // =========================================================================

    public static class BrentScalingPoint {
        public final int processorCount;
        public final double theoreticalTimeTp;
        public final double speedup;
        public final double efficiencyPercent;

        public BrentScalingPoint(int p, double tp, double speedup, double eff) {
            this.processorCount = p;
            this.theoreticalTimeTp = tp;
            this.speedup = speedup;
            this.efficiencyPercent = eff;
        }
    }

    /**
     * Evaluates Brent's Scheduling Theorem for a parallel computation:
     * T_p <= ((W - D) / p) + D
     *
     * @param work W: Total number of sequential operations.
     * @param depth D: Span or critical path length of the computation DAG.
     * @param processorCounts Array of processor/core counts to evaluate.
     */
    public static List<BrentScalingPoint> evaluateBrentsTheorem(double work, double depth, int[] processorCounts) {
        List<BrentScalingPoint> points = new ArrayList<>();
        double t1 = work; // Sequential time on 1 processor

        for (int p : processorCounts) {
            double tp = ((work - depth) / p) + depth;
            double speedup = t1 / tp;
            double efficiency = (speedup / p) * 100.0;
            points.add(new BrentScalingPoint(p, tp, speedup, efficiency));
        }

        return points;
    }
}
