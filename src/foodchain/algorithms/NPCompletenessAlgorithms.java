package foodchain.algorithms;

import java.util.*;

/**
 * NP-Completeness Reductions and Approximation Algorithms for FoodChain.
 * Implements Module M5 (CO5):
 * 1. SAT / 3-SAT DPLL solver for supply and inventory constraints.
 * 2. 3-SAT -> CLIQUE polynomial-time reduction.
 * 3. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER reduction chain for supply-conflict analysis.
 * 4. Vertex-Cover 2-Approximation algorithm for critical distribution hubs.
 */
public class NPCompletenessAlgorithms {

    // =========================================================================
    // 1. SAT / 3-SAT FOR SUPPLY & INVENTORY CONSTRAINTS
    // =========================================================================

    /**
     * Represents a single literal in a Boolean clause.
     * Positive integer x represents variable x.
     * Negative integer -x represents NOT variable x.
     */
    public static class Literal {
        public final int var; // 1-based variable index
        public final boolean isPositive;

        public Literal(int signedVar) {
            if (signedVar == 0) throw new IllegalArgumentException("Variable index cannot be 0");
            this.var = Math.abs(signedVar);
            this.isPositive = signedVar > 0;
        }

        public static Literal of(int signedVar) {
            return new Literal(signedVar);
        }

        public int toSignedInt() {
            return isPositive ? var : -var;
        }

        @Override
        public String toString() {
            return isPositive ? "x" + var : "~x" + var;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Literal)) return false;
            Literal literal = (Literal) o;
            return var == literal.var && isPositive == literal.isPositive;
        }

        @Override
        public int hashCode() {
            return Objects.hash(var, isPositive);
        }
    }

    /**
     * A disjunctive clause in Conjunctive Normal Form (CNF): (l1 OR l2 OR ... OR lk).
     */
    public static class Clause {
        public final List<Literal> literals;

        public Clause(List<Literal> literals) {
            this.literals = new ArrayList<>(literals);
        }

        public static Clause of(int... signedVars) {
            List<Literal> list = new ArrayList<>();
            for (int v : signedVars) {
                list.add(Literal.of(v));
            }
            return new Clause(list);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.size(); i++) {
                sb.append(literals.get(i));
                if (i < literals.size() - 1) sb.append(" v ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    public static class SATResult {
        public final boolean satisfiable;
        public final Map<Integer, Boolean> model; // var index -> true/false
        public final int stepsExplored;

        public SATResult(boolean satisfiable, Map<Integer, Boolean> model, int stepsExplored) {
            this.satisfiable = satisfiable;
            this.model = model;
            this.stepsExplored = stepsExplored;
        }
    }

    /**
     * Solves SAT/3-SAT instances using DPLL (Unit Propagation + Pure Literal Elimination + Backtracking).
     */
    public static SATResult solveSAT(List<Clause> formula, int numVariables) {
        int[] steps = new int[]{0};
        Map<Integer, Boolean> assignment = new HashMap<>();
        boolean sat = dpll(formula, assignment, numVariables, steps);
        return new SATResult(sat, sat ? assignment : Collections.emptyMap(), steps[0]);
    }

    private static boolean dpll(List<Clause> clauses, Map<Integer, Boolean> assignment, int numVars, int[] steps) {
        steps[0]++;

        // Check if formula is empty -> all clauses satisfied!
        if (clauses.isEmpty()) {
            // Fill unassigned variables with default true
            for (int v = 1; v <= numVars; v++) {
                assignment.putIfAbsent(v, true);
            }
            return true;
        }

        // Check if any clause is empty -> contradiction/conflict!
        for (Clause c : clauses) {
            if (c.literals.isEmpty()) {
                return false;
            }
        }

        // Unit Propagation: Look for single-literal clauses
        for (Clause c : clauses) {
            if (c.literals.size() == 1) {
                Literal unit = c.literals.get(0);
                Map<Integer, Boolean> newAssign = new HashMap<>(assignment);
                newAssign.put(unit.var, unit.isPositive);
                List<Clause> simplified = simplifyFormula(clauses, unit.var, unit.isPositive);
                if (dpll(simplified, newAssign, numVars, steps)) {
                    assignment.putAll(newAssign);
                    return true;
                }
                return false;
            }
        }

        // Pick unassigned variable
        int chosenVar = -1;
        for (Clause c : clauses) {
            for (Literal lit : c.literals) {
                if (!assignment.containsKey(lit.var)) {
                    chosenVar = lit.var;
                    break;
                }
            }
            if (chosenVar != -1) break;
        }

        if (chosenVar == -1) {
            return true;
        }

        // Branch 1: Try chosenVar = true
        Map<Integer, Boolean> branchTrue = new HashMap<>(assignment);
        branchTrue.put(chosenVar, true);
        List<Clause> simplifiedTrue = simplifyFormula(clauses, chosenVar, true);
        if (dpll(simplifiedTrue, branchTrue, numVars, steps)) {
            assignment.putAll(branchTrue);
            return true;
        }

        // Branch 2: Try chosenVar = false
        Map<Integer, Boolean> branchFalse = new HashMap<>(assignment);
        branchFalse.put(chosenVar, false);
        List<Clause> simplifiedFalse = simplifyFormula(clauses, chosenVar, false);
        if (dpll(simplifiedFalse, branchFalse, numVars, steps)) {
            assignment.putAll(branchFalse);
            return true;
        }

        return false;
    }

    private static List<Clause> simplifyFormula(List<Clause> clauses, int var, boolean value) {
        List<Clause> result = new ArrayList<>();
        for (Clause c : clauses) {
            boolean clauseSatisfied = false;
            List<Literal> remainingLiterals = new ArrayList<>();

            for (Literal lit : c.literals) {
                if (lit.var == var) {
                    if (lit.isPositive == value) {
                        clauseSatisfied = true;
                        break;
                    }
                    // Opposite value -> literal evaluates to false, omit from simplified clause
                } else {
                    remainingLiterals.add(lit);
                }
            }

            if (!clauseSatisfied) {
                result.add(new Clause(remainingLiterals));
            }
        }
        return result;
    }

    // =========================================================================
    // 2. 3-SAT -> CLIQUE POLYNOMIAL REDUCTION
    // =========================================================================

    public static class ReductionGraph {
        public final int V;
        public final List<String> vertexLabels;
        public final boolean[][] adj;
        public final List<int[]> edgeList;

        public ReductionGraph(int V, List<String> vertexLabels) {
            this.V = V;
            this.vertexLabels = new ArrayList<>(vertexLabels);
            this.adj = new boolean[V][V];
            this.edgeList = new ArrayList<>();
        }

        public void addEdge(int u, int v) {
            if (u != v && !adj[u][v]) {
                adj[u][v] = true;
                adj[v][u] = true;
                edgeList.add(new int[]{u, v});
            }
        }
    }

    public static class CliqueReductionResult {
        public final List<Clause> original3SatClauses;
        public final ReductionGraph graph;
        public final int targetCliqueSize; // k = number of clauses
        public final List<Integer> cliqueVertices;
        public final Map<Integer, Boolean> derivedModel;

        public CliqueReductionResult(List<Clause> clauses, ReductionGraph graph, int k,
                                    List<Integer> cliqueVertices, Map<Integer, Boolean> derivedModel) {
            this.original3SatClauses = clauses;
            this.graph = graph;
            this.targetCliqueSize = k;
            this.cliqueVertices = cliqueVertices;
            this.derivedModel = derivedModel;
        }
    }

    /**
     * Implements Karp's polynomial-time reduction from 3-SAT to Maximum CLIQUE.
     * Given 3-SAT formula with k clauses:
     * - Graph has 3k vertices (one for each literal in each clause).
     * - Edge between vertex (r, a) and (s, b) iff r != s and literal(r,a) != ~literal(s,b).
     * - Formula is satisfiable iff graph has a clique of size k.
     */
    public static CliqueReductionResult reduce3SatToClique(List<Clause> clauses) {
        int k = clauses.size();
        int totalVertices = 3 * k;
        List<String> labels = new ArrayList<>();
        List<Literal> literalAtVertex = new ArrayList<>();
        List<Integer> clauseIndexOfVertex = new ArrayList<>();

        for (int c = 0; c < k; c++) {
            Clause cl = clauses.get(c);
            for (int j = 0; j < 3; j++) {
                Literal lit = (j < cl.literals.size()) ? cl.literals.get(j) : cl.literals.get(0);
                labels.add("C" + (c + 1) + ":" + lit);
                literalAtVertex.add(lit);
                clauseIndexOfVertex.add(c);
            }
        }

        ReductionGraph graph = new ReductionGraph(totalVertices, labels);

        // Connect vertices across different clauses if literals are compatible
        for (int u = 0; u < totalVertices; u++) {
            int clauseU = clauseIndexOfVertex.get(u);
            Literal litU = literalAtVertex.get(u);

            for (int v = u + 1; v < totalVertices; v++) {
                int clauseV = clauseIndexOfVertex.get(v);
                Literal litV = literalAtVertex.get(v);

                // Condition 1: Must be in different clauses
                if (clauseU != clauseV) {
                    // Condition 2: Must not be complementary (e.g. x1 and ~x1 cannot both be true)
                    boolean contradictory = (litU.var == litV.var && litU.isPositive != litV.isPositive);
                    if (!contradictory) {
                        graph.addEdge(u, v);
                    }
                }
            }
        }

        // Find a clique of size k (representing satisfying assignment)
        List<Integer> clique = findCliqueOfSizeK(graph, k);
        Map<Integer, Boolean> model = new HashMap<>();
        if (!clique.isEmpty()) {
            for (int vIdx : clique) {
                Literal lit = literalAtVertex.get(vIdx);
                model.put(lit.var, lit.isPositive);
            }
        }

        return new CliqueReductionResult(clauses, graph, k, clique, model);
    }

    private static List<Integer> findCliqueOfSizeK(ReductionGraph g, int k) {
        List<Integer> current = new ArrayList<>();
        List<Integer> best = new ArrayList<>();
        backtrackClique(g, 0, current, best, k);
        return best;
    }

    private static boolean backtrackClique(ReductionGraph g, int start, List<Integer> current, List<Integer> result, int k) {
        if (current.size() == k) {
            result.addAll(current);
            return true;
        }

        for (int v = start; v < g.V; v++) {
            if (current.size() + (g.V - v) < k) {
                return false; // Prune search branch
            }

            boolean canAdd = true;
            for (int u : current) {
                if (!g.adj[u][v]) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                current.add(v);
                if (backtrackClique(g, v + 1, current, result, k)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }
        return false;
    }

    // =========================================================================
    // 3. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER REDUCTION CHAIN
    // =========================================================================

    public static class ReductionChainResult {
        public final ReductionGraph conflictGraph;
        public final ReductionGraph complementGraph;
        public final List<Integer> maxCliqueInOriginal;
        public final List<Integer> maxIndependentSetInConflict;
        public final List<Integer> minVertexCoverInConflict;
        public final String mathematicalProofSummary;

        public ReductionChainResult(ReductionGraph conflictGraph, ReductionGraph complementGraph,
                                    List<Integer> maxCliqueInOriginal,
                                    List<Integer> maxIndependentSetInConflict,
                                    List<Integer> minVertexCoverInConflict,
                                    String proof) {
            this.conflictGraph = conflictGraph;
            this.complementGraph = complementGraph;
            this.maxCliqueInOriginal = maxCliqueInOriginal;
            this.maxIndependentSetInConflict = maxIndependentSetInConflict;
            this.minVertexCoverInConflict = minVertexCoverInConflict;
            this.mathematicalProofSummary = proof;
        }
    }

    /**
     * Demonstrates the classic Karp reduction chain:
     * 1. Clique in G <=> Independent Set in G_complement
     * 2. Independent Set S in H <=> Vertex Cover (V \ S) in H
     * Applied to mutual exclusivity / conflict networks in food distribution.
     */
    public static ReductionChainResult demonstrateReductionChain(ReductionGraph conflictGraph) {
        int n = conflictGraph.V;
        ReductionGraph complement = new ReductionGraph(n, conflictGraph.vertexLabels);

        // Build complement graph G_comp
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!conflictGraph.adj[i][j]) {
                    complement.addEdge(i, j);
                }
            }
        }

        // 1. Find Max Clique in conflictGraph
        List<Integer> maxClique = findMaximumClique(conflictGraph);

        // 2. Find Max Independent Set in conflictGraph
        // By reduction theorem: Max IS in conflictGraph == Max Clique in complementGraph
        List<Integer> maxIS = findMaximumClique(complement);

        // 3. Compute Min Vertex Cover in conflictGraph
        // By reduction theorem: Min VC in conflictGraph == V \ (Max IS in conflictGraph)
        Set<Integer> isSet = new HashSet<>(maxIS);
        List<Integer> minVC = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!isSet.contains(i)) {
                minVC.add(i);
            }
        }

        String proof = String.format(
            "Karp Theorem Verification: |V| = %d | |Max IS| = %d | |Min VC| = %d | Identity: |IS| + |VC| = %d == |V|",
            n, maxIS.size(), minVC.size(), (maxIS.size() + minVC.size())
        );

        return new ReductionChainResult(conflictGraph, complement, maxClique, maxIS, minVC, proof);
    }

    private static List<Integer> findMaximumClique(ReductionGraph g) {
        List<Integer> best = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        for (int target = g.V; target >= 1; target--) {
            current.clear();
            if (backtrackClique(g, 0, current, best, target)) {
                return best;
            }
        }
        return best;
    }

    // =========================================================================
    // 4. VERTEX-COVER 2-APPROXIMATION ALGORITHM
    // =========================================================================

    public static class VertexCoverApproxResult {
        public final Set<Integer> approximateCover;
        public final List<int[]> selectedMaximalMatchingEdges;
        public final List<Integer> exactOptimalCover;
        public final double empiricalRatio;

        public VertexCoverApproxResult(Set<Integer> approximateCover, List<int[]> matchingEdges,
                                      List<Integer> exactOptimalCover, double empiricalRatio) {
            this.approximateCover = approximateCover;
            this.selectedMaximalMatchingEdges = matchingEdges;
            this.exactOptimalCover = exactOptimalCover;
            this.empiricalRatio = empiricalRatio;
        }
    }

    /**
     * Polynomial-time 2-Approximation for Vertex Cover using Maximal Matching.
     * Guarantees |C| <= 2 * |C_optimal|.
     * Identifies the minimum set of critical distribution hub monitoring points
     * required to inspect all food transit corridors.
     */
    public static VertexCoverApproxResult solveVertexCover2Approx(ReductionGraph graph) {
        Set<Integer> cover = new LinkedHashSet<>();
        List<int[]> matchingEdges = new ArrayList<>();
        boolean[] inCover = new boolean[graph.V];

        // Process all edges: if neither endpoint is covered, select both
        for (int[] edge : graph.edgeList) {
            int u = edge[0];
            int v = edge[1];
            if (!inCover[u] && !inCover[v]) {
                inCover[u] = true;
                inCover[v] = true;
                cover.add(u);
                cover.add(v);
                matchingEdges.add(new int[]{u, v});
            }
        }

        // Find exact optimal cover via complement of max independent set for verification
        ReductionGraph complement = new ReductionGraph(graph.V, graph.vertexLabels);
        for (int i = 0; i < graph.V; i++) {
            for (int j = i + 1; j < graph.V; j++) {
                if (!graph.adj[i][j]) complement.addEdge(i, j);
            }
        }
        List<Integer> maxIS = findMaximumClique(complement);
        Set<Integer> isSet = new HashSet<>(maxIS);
        List<Integer> exactOptimal = new ArrayList<>();
        for (int i = 0; i < graph.V; i++) {
            if (!isSet.contains(i)) {
                exactOptimal.add(i);
            }
        }

        double ratio = (exactOptimal.isEmpty()) ? 1.0 : (double) cover.size() / exactOptimal.size();
        return new VertexCoverApproxResult(cover, matchingEdges, exactOptimal, ratio);
    }
}
