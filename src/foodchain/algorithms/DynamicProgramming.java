package foodchain.algorithms;

import java.util.*;

/**
 * Dynamic Programming algorithms used throughout FoodChain.
 * Implements M3 algorithms:
 * 1. Standard Levenshtein Distance
 * 2. True Damerau-Levenshtein Distance (Lowrance-Wagner unrestricted edits)
 * 3. Bitmask DP for Limited Warehouse Resource Allocation
 * 4. Matrix-Chain Multiplication (MCM) DP for supply chain analytics pipeline
 * 5. Optimal Binary Search Tree (OBST) DP with dynamic tree node building & lookup
 */
public class DynamicProgramming {

    // =========================================================================
    // 1. LEVENSHTEIN DISTANCE
    // =========================================================================

    /**
     * Standard Levenshtein distance using 2D DP.
     * Allowed operations: Insertion, Deletion, Substitution.
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";

        int n = s1.length();
        int m = s2.length();
        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1,        // deletion
                             dp[i][j - 1] + 1),       // insertion
                    dp[i - 1][j - 1] + cost           // substitution
                );
            }
        }
        return dp[n][m];
    }

    // =========================================================================
    // 2. TRUE DAMERAU-LEVENSHTEIN DISTANCE (Lowrance-Wagner Algorithm)
    // =========================================================================

    /**
     * True Damerau-Levenshtein distance supporting repeated edits and transpositions.
     * Unlike restricted Optimal String Alignment (OSA), this correctly permits
     * non-adjacent transpositions and repeated modifications between transposed characters.
     */
    public static int damerauLevenshteinDistance(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";

        int n = a.length();
        int m = b.length();
        int inf = n + m;

        // DP table of size (n + 2) x (m + 2)
        int[][] h = new int[n + 2][m + 2];
        h[0][0] = inf;
        for (int i = 0; i <= n; i++) {
            h[i + 1][0] = inf;
            h[i + 1][1] = i;
        }
        for (int j = 0; j <= m; j++) {
            h[0][j + 1] = inf;
            h[1][j + 1] = j;
        }

        // Stores the last seen row for each character in alphabet
        Map<Character, Integer> da = new HashMap<>();

        for (int i = 1; i <= n; i++) {
            int db = 0;
            char ca = a.charAt(i - 1);

            for (int j = 1; j <= m; j++) {
                char cb = b.charAt(j - 1);
                int i1 = da.getOrDefault(cb, 0);
                int j1 = db;
                int cost = (ca == cb) ? 0 : 1;
                if (cost == 0) {
                    db = j;
                }

                int del = h[i][j + 1] + 1;
                int ins = h[i + 1][j] + 1;
                int sub = h[i][j] + cost;
                // Transposition with repeated edits
                int trans = h[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1);

                h[i + 1][j + 1] = Math.min(Math.min(del, ins), Math.min(sub, trans));
            }
            da.put(ca, i);
        }
        return h[n + 1][m + 1];
    }

    /**
     * Suggests the closest string from candidate list within distance threshold.
     */
    public static String suggestClosest(String input, List<String> candidates, int maxThreshold) {
        if (input == null || candidates == null || candidates.isEmpty()) return null;

        String bestMatch = null;
        int minDistance = Integer.MAX_VALUE;
        String normalizedInput = input.trim().toLowerCase();

        for (String cand : candidates) {
            if (cand == null) continue;
            String normalizedCand = cand.trim().toLowerCase();

            int dist = damerauLevenshteinDistance(normalizedInput, normalizedCand);
            if (dist < minDistance && dist <= maxThreshold) {
                minDistance = dist;
                bestMatch = cand;
            }
        }
        return bestMatch;
    }

    // =========================================================================
    // 3. BITMASK DP (LIMITED WAREHOUSE RESOURCE ALLOCATION)
    // =========================================================================

    public static class BitmaskAllocationResult {
        public final int bestMask;
        public final int totalWeightUsed;
        public final int totalBenefit;
        public final int remainingCapacity;
        public final List<Integer> selectedIndices;

        public BitmaskAllocationResult(int bestMask, int totalWeightUsed, int totalBenefit,
                                       int remainingCapacity, List<Integer> selectedIndices) {
            this.bestMask = bestMask;
            this.totalWeightUsed = totalWeightUsed;
            this.totalBenefit = totalBenefit;
            this.remainingCapacity = remainingCapacity;
            this.selectedIndices = selectedIndices;
        }
    }

    /**
     * Solves the subset allocation problem using Bitmask DP.
     * Evaluates all 2^N subsets to maximize benefit without exceeding capacity.
     */
    public static BitmaskAllocationResult solveWarehouseAllocation(int[] weights, int[] benefits, int capacity) {
        int n = weights.length;
        int numSubsets = 1 << n;

        int[] totalWeights = new int[numSubsets];
        int[] totalBenefits = new int[numSubsets];

        int bestMask = 0;
        int bestBenefit = 0;
        int bestWeight = 0;

        for (int mask = 1; mask < numSubsets; mask++) {
            // Extract lowest set bit
            int lsb = mask & -mask;
            int idx = Integer.numberOfTrailingZeros(lsb);
            int prevMask = mask ^ lsb;

            totalWeights[mask] = totalWeights[prevMask] + weights[idx];
            totalBenefits[mask] = totalBenefits[prevMask] + benefits[idx];

            if (totalWeights[mask] <= capacity) {
                if (totalBenefits[mask] > bestBenefit ||
                    (totalBenefits[mask] == bestBenefit && totalWeights[mask] < bestWeight)) {
                    bestBenefit = totalBenefits[mask];
                    bestWeight = totalWeights[mask];
                    bestMask = mask;
                }
            }
        }

        List<Integer> selected = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((bestMask & (1 << i)) != 0) {
                selected.add(i);
            }
        }

        return new BitmaskAllocationResult(bestMask, bestWeight, bestBenefit,
                                           capacity - bestWeight, selected);
    }

    // =========================================================================
    // 4. MATRIX-CHAIN MULTIPLICATION (MCM) DP
    // =========================================================================

    public static class MCMResult {
        public final int minScalarMultiplications;
        public final int[][] m;
        public final int[][] s;
        public final String optimalParenthesization;
        public final int[] dimensions;

        public MCMResult(int minScalarMultiplications, int[][] m, int[][] s,
                         String optimalParenthesization, int[] dimensions) {
            this.minScalarMultiplications = minScalarMultiplications;
            this.m = m;
            this.s = s;
            this.optimalParenthesization = optimalParenthesization;
            this.dimensions = dimensions;
        }
    }

    /**
     * Solves the Matrix-Chain Multiplication problem.
     * Dimensions array p contains dimensions such that Matrix A[i] is p[i-1] x p[i].
     */
    public static MCMResult solveMCM(int[] p) {
        int n = p.length - 1; // number of matrices
        int[][] m = new int[n + 1][n + 1];
        int[][] s = new int[n + 1][n + 1];

        // Chain length L from 2 to n
        for (int l = 2; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                m[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {
                    int cost = m[i][k] + m[k + 1][j] + p[i - 1] * p[k] * p[j];
                    if (cost < m[i][j]) {
                        m[i][j] = cost;
                        s[i][j] = k;
                    }
                }
            }
        }

        String paren = buildOptimalParenthesization(s, 1, n);
        return new MCMResult(m[1][n], m, s, paren, p);
    }

    private static String buildOptimalParenthesization(int[][] s, int i, int j) {
        if (i == j) {
            return "M" + i;
        } else {
            return "(" + buildOptimalParenthesization(s, i, s[i][j]) + " x " +
                   buildOptimalParenthesization(s, s[i][j] + 1, j) + ")";
        }
    }

    // =========================================================================
    // 5. OPTIMAL BINARY SEARCH TREE (OBST) DP
    // =========================================================================

    public static class OBSTNode {
        public final String key;
        public final Object data;
        public OBSTNode left;
        public OBSTNode right;

        public OBSTNode(String key, Object data) {
            this.key = key;
            this.data = data;
        }
    }

    public static class OBSTSearchResult {
        public final boolean found;
        public final Object data;
        public final int comparisons;

        public OBSTSearchResult(boolean found, Object data, int comparisons) {
            this.found = found;
            this.data = data;
            this.comparisons = comparisons;
        }
    }

    public static class OBSTResult {
        public final double expectedCost;
        public final OBSTNode root;
        public final int[][] rootTable;
        public final double[][] eTable;
        public final double[][] wTable;
        public final String[] sortedKeys;
        public final Object[] sortedData;

        public OBSTResult(double expectedCost, OBSTNode root, int[][] rootTable,
                          double[][] eTable, double[][] wTable,
                          String[] sortedKeys, Object[] sortedData) {
            this.expectedCost = expectedCost;
            this.root = root;
            this.rootTable = rootTable;
            this.eTable = eTable;
            this.wTable = wTable;
            this.sortedKeys = sortedKeys;
            this.sortedData = sortedData;
        }

        /**
         * Real-time search inside the constructed Optimal BST.
         * Returns matching object and exact comparison count.
         */
        public OBSTSearchResult search(String searchKey) {
            if (root == null || searchKey == null) {
                return new OBSTSearchResult(false, null, 0);
            }
            OBSTNode curr = root;
            int steps = 0;
            String target = searchKey.trim().toLowerCase();

            while (curr != null) {
                steps++;
                int cmp = target.compareTo(curr.key.toLowerCase());
                if (cmp == 0) {
                    return new OBSTSearchResult(true, curr.data, steps);
                } else if (cmp < 0) {
                    curr = curr.left;
                } else {
                    curr = curr.right;
                }
            }
            return new OBSTSearchResult(false, null, steps);
        }
    }

    /**
     * Builds the Optimal Binary Search Tree using Dynamic Programming.
     * Takes raw keys, associated data, and search probabilities/frequencies.
     */
    public static OBSTResult buildOptimalBST(String[] keys, Object[] data, double[] probabilities) {
        int n = keys.length;
        if (n == 0) {
            return new OBSTResult(0, null, new int[0][0], new double[0][0], new double[0][0], new String[0], new Object[0]);
        }

        // Pair up and sort keys lexicographically
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparing(a -> keys[a].toLowerCase()));

        String[] sortedKeys = new String[n];
        Object[] sortedData = new Object[n];
        double[] p = new double[n + 1]; // 1-indexed

        for (int i = 0; i < n; i++) {
            sortedKeys[i] = keys[order[i]];
            sortedData[i] = data[order[i]];
            p[i + 1] = probabilities[order[i]];
        }

        double[] q = new double[n + 1]; // Unsuccessful search probabilities (0 for pure key model)

        double[][] e = new double[n + 2][n + 1];
        double[][] w = new double[n + 2][n + 1];
        int[][] root = new int[n + 1][n + 1];

        for (int i = 1; i <= n + 1; i++) {
            e[i][i - 1] = q[i - 1];
            w[i][i - 1] = q[i - 1];
        }

        for (int l = 1; l <= n; l++) {
            for (int i = 1; i <= n - l + 1; i++) {
                int j = i + l - 1;
                e[i][j] = Double.MAX_VALUE;
                w[i][j] = w[i][j - 1] + p[j] + q[j];

                for (int r = i; r <= j; r++) {
                    double t = e[i][r - 1] + e[r + 1][j] + w[i][j];
                    if (t < e[i][j]) {
                        e[i][j] = t;
                        root[i][j] = r;
                    }
                }
            }
        }

        OBSTNode rootNode = constructOBST(1, n, sortedKeys, sortedData, root);
        return new OBSTResult(e[1][n], rootNode, root, e, w, sortedKeys, sortedData);
    }

    private static OBSTNode constructOBST(int i, int j, String[] keys, Object[] data, int[][] root) {
        if (i > j) {
            return null;
        }
        int r = root[i][j];
        OBSTNode node = new OBSTNode(keys[r - 1], data[r - 1]);
        node.left = constructOBST(i, r - 1, keys, data, root);
        node.right = constructOBST(r + 1, j, keys, data, root);
        return node;
    }
}
