package foodchain;

import foodchain.algorithms.NPCompletenessAlgorithms;
import foodchain.algorithms.NPCompletenessAlgorithms.*;

import java.util.*;

/**
 * Handles supply chain operational constraint solving and NP-Completeness reductions.
 * Integrates Module M5:
 * 1. SAT / 3-SAT DPLL constraint solver for supplier scheduling and inventory rules
 * 2. 3-SAT -> CLIQUE polynomial-time reduction
 * 3. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER reduction chain for supply-conflict analysis
 * 4. Vertex-Cover 2-Approximation for critical distribution monitoring hubs
 */
public class ConstraintService {

    private final List<Supplier> suppliers;
    private final List<Warehouse> warehouses;

    public ConstraintService(List<Supplier> suppliers, List<Warehouse> warehouses) {
        this.suppliers = suppliers;
        this.warehouses = warehouses;
    }

    public void executeConstraintMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Supply Chain Constraints & NP Reductions (M5) ---");
            System.out.println("1. Solve Operational Supply Constraints (SAT / 3-SAT DPLL)");
            System.out.println("2. Demonstrate 3-SAT -> CLIQUE Polynomial Reduction");
            System.out.println("3. Supply Conflict Analysis (CLIQUE -> INDEPENDENT SET -> VERTEX COVER)");
            System.out.println("4. Identify Critical Distribution Monitoring Hubs (Vertex-Cover 2-Approx)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select constraint operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    runSATConstraintWorkflow();
                    break;
                case "2":
                    run3SatToCliqueReductionWorkflow();
                    break;
                case "3":
                    runConflictReductionChainWorkflow();
                    break;
                case "4":
                    runVertexCover2ApproxWorkflow();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose between 0 and 4.");
            }
        }
    }

    /**
     * Demonstrates SAT/3-SAT modeling for real supply chain decisions:
     * Variables:
     * x1: Contract Green Valley Farms (Grain Supplier)
     * x2: Contract Highland Dairy (Cold Chain)
     * x3: Activate Metro North Hub Cold Storage Bay
     * x4: Schedule Overnight Express Transit
     * x5: Enable Backup Climate Generators
     *
     * Constraints (3-CNF Clauses):
     * C1: Must have either Grain or Dairy or Express: (x1 v x2 v x4)
     * C2: If Highland Dairy contracted, Cold Bay or Backup Gen must be on: (~x2 v x3 v x5)
     * C3: If Cold Bay active, Highland Dairy must be on: (~x3 v x2 v x5)
     * C4: Cannot have both Express Transit and No Backup without Grain: (~x4 v x1 v x5)
     * C5: Operational budget restriction clause: (~x1 v ~x4 v x3)
     */
    public void runSATConstraintWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("           SUPPLY CHAIN OPERATIONAL CONSTRAINT SOLVER (3-SAT DPLL)               ");
        System.out.println("=================================================================================");
        System.out.println("Operational Decision Variables:");
        System.out.println("  x1: Contract Green Valley Farms (Grains)");
        System.out.println("  x2: Contract Highland Dairy Ltd (Dairy/Cold-Chain)");
        System.out.println("  x3: Activate Metro North Cold Storage Bay");
        System.out.println("  x4: Schedule Express Overnight Freight Route");
        System.out.println("  x5: Enable Auxiliary Backup Climate Generators\n");

        List<Clause> clauses = Arrays.asList(
                Clause.of(1, 2, 4),    // (x1 v x2 v x4)
                Clause.of(-2, 3, 5),   // (~x2 v x3 v x5)
                Clause.of(-3, 2, 5),   // (~x3 v x2 v x5)
                Clause.of(-4, 1, 5),   // (~x4 v x1 v x5)
                Clause.of(-1, -4, 3)   // (~x1 v ~x4 v x3)
        );

        System.out.println("Active 3-CNF Constraint Formula:");
        for (int i = 0; i < clauses.size(); i++) {
            System.out.println("  Clause " + (i + 1) + ": " + clauses.get(i));
        }

        SATResult result = NPCompletenessAlgorithms.solveSAT(clauses, 5);

        System.out.println("\nDPLL Solver Result:");
        System.out.println("  Formula Satisfiability : " + (result.satisfiable ? "SATISFIABLE [VALID PLAN]" : "UNSATISFIABLE [CONFLICT]"));
        System.out.println("  Search States Explored : " + result.stepsExplored);

        if (result.satisfiable) {
            System.out.println("\nFeasible Operational Configuration Found:");
            String[] names = {
                    "Green Valley Farms Contract",
                    "Highland Dairy Ltd Contract",
                    "Metro North Cold Bay Active",
                    "Express Overnight Freight",
                    "Backup Climate Generators"
            };
            for (int v = 1; v <= 5; v++) {
                boolean val = result.model.getOrDefault(v, false);
                System.out.printf("  x%d: %-32s -> %s%n", v, names[v - 1], val ? "[ENABLED]" : "[DISABLED]");
            }
        }
        System.out.println("=================================================================================");
    }

    public void run3SatToCliqueReductionWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("               3-SAT -> CLIQUE POLYNOMIAL REDUCTION DEMONSTRATION                ");
        System.out.println("=================================================================================");

        List<Clause> clauses = Arrays.asList(
                Clause.of(1, 2, 3),    // C1: (x1 v x2 v x3)
                Clause.of(-1, 2, -3),  // C2: (~x1 v x2 v ~x3)
                Clause.of(1, -2, 3)    // C3: (x1 v ~x2 v x3)
        );

        System.out.println("Given 3-SAT Instance with k = " + clauses.size() + " clauses:");
        for (int i = 0; i < clauses.size(); i++) {
            System.out.println("  Clause " + (i + 1) + ": " + clauses.get(i));
        }

        CliqueReductionResult red = NPCompletenessAlgorithms.reduce3SatToClique(clauses);

        System.out.println("\nConstructed Karp Reduction Graph G = (V, E):");
        System.out.printf("  Total Vertices |V| = 3 * k = %d%n", red.graph.V);
        System.out.printf("  Total Edges    |E| = %d%n", red.graph.edgeList.size());
        System.out.printf("  Target Clique Size k = %d%n", red.targetCliqueSize);

        System.out.println("\nReduction Theorem: Formula is Satisfiable <===> Graph has Clique of size " + red.targetCliqueSize);
        if (!red.cliqueVertices.isEmpty()) {
            System.out.println("Found Clique of size " + red.targetCliqueSize + ":");
            for (int v : red.cliqueVertices) {
                System.out.println("  Vertex " + v + " -> Literal: " + red.graph.vertexLabels.get(v));
            }

            System.out.println("\nTruth Assignment Reconstructed from Clique Vertices:");
            for (Map.Entry<Integer, Boolean> e : red.derivedModel.entrySet()) {
                System.out.printf("  x%d = %s%n", e.getKey(), e.getValue());
            }
        } else {
            System.out.println("No clique of size " + red.targetCliqueSize + " exists -> Formula is UNSATISFIABLE.");
        }
        System.out.println("=================================================================================");
    }

    public void runConflictReductionChainWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("  SUPPLY CONFLICT REDUCTION: CLIQUE -> INDEPENDENT SET -> VERTEX COVER           ");
        System.out.println("=================================================================================");

        // Supply facility conflict graph (nodes represent regional distribution routes)
        // Edges represent mutual contract exclusivity / scheduling conflicts
        List<String> routeLabels = Arrays.asList(
                "R1: Punjab-Delhi",
                "R2: Punjab-Mumbai",
                "R3: Maharashtra-Mumbai",
                "R4: Gujarat-Delhi",
                "R5: Karnataka-Bengaluru",
                "R6: Haryana-Delhi"
        );

        ReductionGraph conflictGraph = new ReductionGraph(6, routeLabels);
        conflictGraph.addEdge(0, 1); // R1 conflicts with R2
        conflictGraph.addEdge(0, 3); // R1 conflicts with R4
        conflictGraph.addEdge(1, 2); // R2 conflicts with R3
        conflictGraph.addEdge(2, 4); // R3 conflicts with R5
        conflictGraph.addEdge(3, 5); // R4 conflicts with R6

        ReductionChainResult chain = NPCompletenessAlgorithms.demonstrateReductionChain(conflictGraph);

        System.out.println("Conflict Graph (Logistics Routes with Shared Transit Corridors):");
        System.out.printf("  Facilities |V| = %d | Conflict Edges |E| = %d%n", conflictGraph.V, conflictGraph.edgeList.size());

        System.out.println("\n1. Maximum Clique in Original Graph:");
        System.out.printf("   Size: %d routes in mutual mutual-conflict cluster: %s%n",
                chain.maxCliqueInOriginal.size(), formatRouteList(chain.maxCliqueInOriginal, routeLabels));

        System.out.println("\n2. Maximum Independent Set (Non-Conflicting Concurrent Operations):");
        System.out.printf("   Size: %d routes can operate simultaneously without conflict: %s%n",
                chain.maxIndependentSetInConflict.size(), formatRouteList(chain.maxIndependentSetInConflict, routeLabels));

        System.out.println("\n3. Minimum Vertex Cover (Critical Oversight Facilities to Mediate Conflicts):");
        System.out.printf("   Size: %d hub locations needed to monitor all conflicting pairs: %s%n",
                chain.minVertexCoverInConflict.size(), formatRouteList(chain.minVertexCoverInConflict, routeLabels));

        System.out.println("\n" + chain.mathematicalProofSummary);
        System.out.println("=================================================================================");
    }

    private String formatRouteList(List<Integer> indices, List<String> labels) {
        List<String> names = new ArrayList<>();
        for (int idx : indices) {
            names.add(labels.get(idx));
        }
        return names.toString();
    }

    public void runVertexCover2ApproxWorkflow() {
        System.out.println("\n=================================================================================");
        System.out.println("    CRITICAL DISTRIBUTION MONITORING HUBS (VERTEX-COVER 2-APPROXIMATION)         ");
        System.out.println("=================================================================================");

        List<String> hubLabels = Arrays.asList(
                "Hub 1: Delhi North",
                "Hub 2: Jaipur Transit",
                "Hub 3: Ahmedabad Depot",
                "Hub 4: Mumbai West",
                "Hub 5: Pune Gateway",
                "Hub 6: Bengaluru South",
                "Hub 7: Chennai Port",
                "Hub 8: Hyderabad Central"
        );

        ReductionGraph transitNetwork = new ReductionGraph(8, hubLabels);
        transitNetwork.addEdge(0, 1); // Delhi - Jaipur
        transitNetwork.addEdge(1, 2); // Jaipur - Ahmedabad
        transitNetwork.addEdge(2, 3); // Ahmedabad - Mumbai
        transitNetwork.addEdge(3, 4); // Mumbai - Pune
        transitNetwork.addEdge(4, 7); // Pune - Hyderabad
        transitNetwork.addEdge(7, 5); // Hyderabad - Bengaluru
        transitNetwork.addEdge(5, 6); // Bengaluru - Chennai
        transitNetwork.addEdge(0, 7); // Delhi - Hyderabad Express
        transitNetwork.addEdge(3, 7); // Mumbai - Hyderabad Express

        VertexCoverApproxResult approx = NPCompletenessAlgorithms.solveVertexCover2Approx(transitNetwork);

        System.out.println("Logistics Transportation Grid: 8 Strategic Hubs, 9 Transit Corridors");
        System.out.println("Problem: Find the minimum set of inspection hubs to oversee EVERY transit corridor.");
        System.out.println("Complexity: NP-Hard (Solved in polynomial time via Maximal Matching 2-Approximation)\n");

        System.out.println("Maximal Matching Edges Selected:");
        for (int[] e : approx.selectedMaximalMatchingEdges) {
            System.out.printf("  Corridor: %s <===> %s%n", hubLabels.get(e[0]), hubLabels.get(e[1]));
        }

        System.out.println("\n2-Approximation Vertex Cover Selected (" + approx.approximateCover.size() + " hubs):");
        for (int h : approx.approximateCover) {
            System.out.println("  [MONITORING HUB] " + hubLabels.get(h));
        }

        System.out.println("\nExact Minimum Vertex Cover (" + approx.exactOptimalCover.size() + " hubs):");
        for (int h : approx.exactOptimalCover) {
            System.out.println("  [OPTIMAL HUB]    " + hubLabels.get(h));
        }

        System.out.printf("\nApproximation Ratio Achieved: %.2f (Theoretical Upper Bound: 2.0)%n", approx.empiricalRatio);
        System.out.println("Every transit corridor is guaranteed covered with polynomial-time execution!");
        System.out.println("=================================================================================");
    }
}
