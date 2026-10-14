# FoodChain – Food Supply & Distribution Management Platform

A pure Java console enterprise platform for agricultural food supply management, quality verification, warehouse inventory tracking, logistics distribution, constraint satisfaction, and parallel streaming analytics powered internally by advanced Data Structures and Algorithms (DSA Modules M1 through M6).

---

## 1. Quick Start: Running in Eclipse IDE & Terminal

This project uses standard Java SE (no external frameworks, Maven, Gradle, or third-party dependencies required). It includes pre-configured Eclipse `.project` and `.classpath` descriptors.

### Eclipse IDE:
1. Open **Eclipse IDE**.
2. Go to **File** > **Import...**
3. Select **General** > **Existing Projects into Workspace** and click **Next**.
4. Browse to the project folder (`FoodChain`) and click **Finish**.
5. In the Package Explorer, navigate to:
   `FoodChain` > `src` > `foodchain` > `Main.java`.
6. Right-click `Main.java` > **Run As** > **Java Application**.
7. To run the automated validation test suite, right-click `FoodChainTest.java` > **Run As** > **Java Application**.

### Terminal / Command Prompt / PowerShell:
```powershell
# Navigate into the FoodChain project folder
cd "FoodChain"

# Compile all source files:
javac -d bin src/foodchain/*.java src/foodchain/algorithms/*.java

# Run interactive platform:
java -cp bin foodchain.Main

# Run the 29-point DSA validation test suite:
java -cp bin foodchain.FoodChainTest
```

---

## 2. Complete Main Console Menu

```
=================================================================================
                              WELCOME TO FOODCHAIN                               
                   Food Supply & Distribution Management Platform                
=================================================================================
 1. View Available Food Products
 2. Search Food Products (KMP, Rabin-Karp, OBST & Spell Correction)
 3. Check Food Quality (Aho-Corasick, Suffix Arrays, SA-IS & Kasai LCP)
 4. Manage Inventory (Stock Levels & Bitmask DP Bay Allocation)
 5. Place Customer Order
 6. Plan Food Distribution (Network Flow: Ford-Fulkerson, Dinic, Min-Cut)
 7. Solve Supply Constraints & NP Reductions (SAT/3-SAT, Clique, Vertex Cover)
 8. Parallel & Streaming Analytics (QuickSort, Reservoir, Blelloch, Brent)
 9. View Supply Chain Reports (Executive Dashboards & MCM Optimization)
10. Run Complete Supply Chain Demo (M1 -> M6 End-to-End Walkthrough)
 0. Exit
=================================================================================
```

---

## 3. Project Architecture & Directory Layout

```
FoodChain/
├── .project                                     # Eclipse project metadata
├── .classpath                                   # Eclipse classpath configuration
├── .gitignore                                   # Git exclusion rules
├── README.md                                    # Comprehensive system documentation
└── src/
    └── foodchain/
        ├── Main.java                            # Interactive console UI entry point
        ├── Product.java                         # Food product entity model
        ├── Supplier.java                        # Agricultural supplier entity model
        ├── Warehouse.java                       # Storage depot model
        ├── Shipment.java                        # Logistics transit shipment record
        ├── SampleData.java                      # In-memory datasets (20 products, 5 suppliers, 3 depots)
        ├── ProductService.java                  # Product browsing, KMP, Rabin-Karp, OBST
        ├── QualityService.java                  # Inspection audits, Aho-Corasick, Suffix structures
        ├── InventoryService.java                # Stock monitoring, Bitmask DP bay allocation
        ├── OrderService.java                    # Customer ordering, Damerau-Levenshtein spell-checking
        ├── DistributionService.java             # Network flow, capacity planning, chokepoint analysis
        ├── ConstraintService.java               # SAT/3-SAT solver, Karp reductions, vertex cover approx
        ├── ParallelAnalyticsService.java        # Demand ranking, streaming sampling, parallel scan/reduce
        ├── ReportService.java                   # Dashboards, Matrix-Chain Multiplication DP
        ├── SupplyChainDemo.java                 # Automated 20-step guided walkthrough
        ├── FoodChainTest.java                   # 29-point rigorous DSA validation test suite
        └── algorithms/
            ├── StringAlgorithms.java            # M1: KMP, Rabin-Karp, Z-Function, Aho-Corasick
            ├── SuffixAlgorithms.java            # M2: Suffix Array, SA-IS, Kasai LCP, Tree, Automaton
            ├── DynamicProgramming.java          # M3: Levenshtein, True Damerau-Levenshtein, Bitmask, MCM, OBST
            ├── NetworkFlowAlgorithms.java       # M4: Ford-Fulkerson, Edmonds-Karp, Dinic, Bipartite, Min-Cut
            ├── NPCompletenessAlgorithms.java    # M5: 3-SAT DPLL, 3-SAT->Clique, Clique->IS->VC, 2-Approx
            └── ParallelRandomizedAlgorithms.java# M6: Rand-QuickSort, Reservoir, Miller-Rabin, Blelloch, Brent
```

---

## 4. Comprehensive DSA Module Mapping & Viva Reference Guide

### Module M1: String Matching Algorithms (CO1)

| Algorithm | Business Function in FoodChain | Time Complexity | Space Complexity |
|---|---|---|---|
| **Knuth-Morris-Pratt (KMP)** | Fast exact substring search for product names & quality terminology | $O(N + M)$ | $O(M)$ |
| **Rabin-Karp** | Rolling polynomial hash search for product and shipment codes (`SHP-2026-001`) | Avg $O(N + M)$, Worst $O(NM)$ | $O(1)$ |
| **Z-Function (Z-Algorithm)** | Pinpoint repetitive inspection boilerplate across batch quality logs | $O(N)$ strictly linear | $O(N)$ |
| **Aho-Corasick Automaton** | Simultaneous multi-keyword quality hazard warning detection (`expired`, `mold`, `compromised`) | $O(N + \sum M_i + \text{matches})$ | $O(\sum M_i \cdot \|\Sigma\|)$ |

### Module M2: Suffix Data Structures – Document Similarity (CO2)

| Algorithm | Business Function in FoodChain | Time Complexity | Space Complexity |
|---|---|---|---|
| **Suffix Array & Binary Search** | Full-text substring search across large catalog quality descriptions | Query $O(\|P\| \log N)$ | $O(N)$ |
| **SA-IS (Induced Sorting)** | Linear-time suffix array construction for large food documents | $O(N)$ strictly linear | $O(N)$ |
| **Kasai's LCP Algorithm** | Identify longest duplicate text phrases in inspection records | $O(N)$ | $O(N)$ |
| **Suffix Tree** | Hierarchical suffix tree substring queries and pattern matching | Query $O(\|P\|)$ | $O(N)$ |
| **Suffix Automaton (DAWG)** | Directed acyclic word graph for ultra-fast substring verification | Query $O(\|P\|)$ | $O(N)$ states |

### Module M3: Advanced Dynamic Programming (CO3)

| Algorithm | Business Function in FoodChain | Time Complexity | Space Complexity |
|---|---|---|---|
| **Levenshtein Distance** | Baseline string editing distance for misspelling suggestions | $O(N \cdot M)$ | $O(N \cdot M)$ |
| **True Damerau-Levenshtein** | Lowrance-Wagner unrestricted transpositions correcting swapped letters (`Rcie` -> `Rice`) | $O(N \cdot M)$ | $O(N \cdot M)$ |
| **Bitmask DP** | Optimal food product subset selection for limited warehouse cold-storage bays | $O(2^N)$ | $O(2^N)$ |
| **Matrix-Chain Multiplication** | Optimizes parenthesization order for multi-stage data processing pipelines | $O(N^3)$ | $O(N^2)$ |
| **Optimal Binary Search Tree** | Arranges frequently queried products closer to the root based on demand frequencies | $O(N^3)$ | $O(N^2)$ |

### Module M4: Network Flow (CO4)

| Algorithm | Business Function in FoodChain | Time Complexity | Space Complexity |
|---|---|---|---|
| **Ford-Fulkerson** | Models food supply transit from suppliers to retail markets using DFS augmenting paths | $O(E \cdot f_{\max})$ | $O(V + E)$ |
| **Edmonds-Karp** | Analyzes distribution network flow capacity using BFS shortest augmenting paths | $O(V \cdot E^2)$ | $O(V + E)$ |
| **Dinic's Algorithm** | Optimizes large multi-tier supply networks using level graphs and blocking flows | $O(V^2 \cdot E)$ | $O(V + E)$ |
| **Maximum Bipartite Matching** | Matches agricultural suppliers to compatible regional warehouses | $O(V \cdot E)$ | $O(V + E)$ |
| **Max-Flow Min-Cut Theorem** | Identifies critical distribution bottlenecks where expansion directly increases throughput | $O(V \cdot E^2)$ | $O(V + E)$ |

### Module M5: NP-Completeness and Approximation (CO5)

| Algorithm | Business Function in FoodChain | Theoretical Context & Significance |
|---|---|---|
| **SAT / 3-SAT DPLL Solver** | Verifies feasibility of complex operational constraints (cold chain, freight, quotas) | Exponential worst-case, fast in practice with Unit Propagation |
| **3-SAT $\to$ CLIQUE Reduction** | Demonstrates Karp's polynomial-time reduction from 3-SAT to Maximum CLIQUE | $O(k^2)$ reduction graph construction; formula is SAT $\iff$ graph has $k$-clique |
| **CLIQUE $\to$ IS $\to$ VERTEX-COVER** | Supply-conflict analysis showing $|IS| + \|VC\| = \|V\|$ in mutual exclusivity graphs | Proves equivalence between non-conflicting schedules and conflict mediators |
| **Vertex-Cover 2-Approximation** | Identifies the minimum set of critical monitoring checkpoints using Maximal Matching | Polynomial time $O(V + E)$; guarantees cover size $\|C\| \le 2 \cdot \|C^*\|$ |

### Module M6: Randomized and Parallel Algorithms (CO6)

| Algorithm | Business Function in FoodChain | Theoretical Guarantee |
|---|---|---|
| **Randomized QuickSort** | Ranks food products by customer demand with randomized pivot selection | Expected $O(N \log N)$ time; eliminates $O(N^2)$ worst-case on sorted data |
| **Reservoir Sampling (Algorithm R)** | Continuous uniform sampling of $k$ items from unbounded inventory telemetry streams | Each stream item has exact uniform probability $\frac{k}{N}$ in $O(k)$ memory |
| **Miller-Rabin Primality Test** | Authenticates digital batch security tokens and tracking crypto identifiers | Probabilistic test; error bound $\le 4^{-k}$ over $k$ randomized witness rounds |
| **Blelloch Parallel Prefix Scan** | Work-efficient parallel prefix sum for cumulative warehouse intake analytics | Work $O(N)$, Span/Depth $O(\log N)$ via Up-Sweep and Down-Sweep |
| **ForkJoin Parallel Reduce** | Multi-threaded divide-and-conquer tree reduction for catalog statistics | Linear work $O(N)$ with near-linear parallel multicore scaling |
| **Brent's Scheduling Theorem** | Evaluates theoretical runtime $T_p \le \frac{W - D}{p} + D$, speedup, and efficiency | Quantifies optimal processor scaling limits for parallel supply operations |

---

## 5. Verification & Validation Test Suite Results

A comprehensive 29-point test suite (`FoodChainTest.java`) tests all algorithms without external test dependencies:

| Test # | Module | Algorithm / Feature Verified | Test Outcome |
|:---:|:---:|:---|:---:|
| 1 | M1 | KMP Pattern Search & LPS Array Construction | **PASSED** |
| 2 | M1 | Rabin-Karp Rolling Hash Verification & Collision Avoidance | **PASSED** |
| 3 | M1 | Aho-Corasick Multi-Pattern Quality Warning Detection | **PASSED** |
| 4 | M1 | Z-Algorithm (Z-Function) Prefix Computation | **PASSED** |
| 5 | M2 | Suffix Array & SA-IS Linear-Time Equivalence | **PASSED** |
| 6 | M2 | Kasai LCP Array & Longest Duplicate Text Identification | **PASSED** |
| 7 | M2 | Suffix Tree Substring Lookup & Structure Verification | **PASSED** |
| 8 | M2 | Suffix Automaton (DAWG) $O(\|P\|)$ Substring Verification | **PASSED** |
| 9 | M3 | Levenshtein & True Damerau-Levenshtein Edit Distances | **PASSED** |
| 10 | M3 | Bitmask DP Warehouse Allocation vs Brute-Force Baseline | **PASSED** |
| 11 | M3 | Matrix-Chain Multiplication (MCM) DP Optimization | **PASSED** |
| 12 | M3 | Optimal BST Construction & Physical Tree Lookups | **PASSED** |
| 13 | Business | Customer Order Placement & Stock Mutation Logic | **PASSED** |
| 14 | Business | Automated Guided Demo Non-Mutating Data Isolation | **PASSED** |
| 15 | M4 | Ford-Fulkerson Max Flow Residual Augmentation | **PASSED** |
| 16 | M4 | Edmonds-Karp BFS Shortest Augmenting Path Equivalence | **PASSED** |
| 17 | M4 | Dinic's Algorithm Level Graph & Blocking Flow Equivalence | **PASSED** |
| 18 | M4 | Maximum Bipartite Matching Supplier-to-Warehouse | **PASSED** |
| 19 | M4 | Max-Flow Min-Cut Theorem & Bottleneck Capacity Verification | **PASSED** |
| 20 | M5 | SAT / 3-SAT DPLL Solver Unit Propagation & Model Verification | **PASSED** |
| 21 | M5 | 3-SAT $\to$ CLIQUE Polynomial Karp Reduction & Model Derivation | **PASSED** |
| 22 | M5 | Reduction Chain: CLIQUE $\to$ IS $\to$ VERTEX-COVER Identity | **PASSED** |
| 23 | M5 | Vertex-Cover 2-Approximation Maximal Matching Ratio Bound | **PASSED** |
| 24 | M6 | Randomized QuickSort In-Place Demand Ranking | **PASSED** |
| 25 | M6 | Reservoir Sampling (Algorithm R) Uniform Bounded Stream Selection | **PASSED** |
| 26 | M6 | Miller-Rabin Primality Test for Batch Crypto Security Tokens | **PASSED** |
| 27 | M6 | Blelloch Work-Efficient Parallel Prefix Scan Up/Down Sweep | **PASSED** |
| 28 | M6 | ForkJoin Parallel Reduce Multi-Core Aggregation | **PASSED** |
| 29 | M6 | Brent's Scheduling Theorem Processor Speedup & Efficiency | **PASSED** |

**Summary: 29 PASSED | 0 FAILED (100% Success Rate)**

---

## 6. Git Push Instructions (How to Push Today)

To push this completed project to your GitHub/GitLab repository today:

```powershell
# 1. Open Terminal or PowerShell in the FoodChain project folder:
cd "D:\TERM 4\DSA PROJECT\FoodChain" # or your local project path

# 2. Initialize git repository (if not already initialized):
git init

# 3. Stage all source code, tests, and documentation:
git add .

# 4. Commit all project files:
git commit -m "Complete FoodChain platform with DSA Modules M1 through M6"

# 5. Set default branch to main:
git branch -M main

# 6. Add your remote GitHub repository URL (replace with your repository link):
git remote add origin https://github.com/Deepthi-32686/FoodChain.git

# 7. Push to GitHub:
git push -u origin main
```
