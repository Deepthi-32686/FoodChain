package foodchain;

import foodchain.algorithms.DynamicProgramming;
import foodchain.algorithms.DynamicProgramming.MCMResult;
import foodchain.algorithms.DynamicProgramming.OBSTResult;
import foodchain.algorithms.DynamicProgramming.OBSTSearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Generates Supply Chain reports and business intelligence optimizations.
 * Integrates M3 Matrix-Chain Multiplication (MCM) and Optimal Binary Search Tree (OBST).
 */
public class ReportService {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final List<Supplier> suppliers;
    private final List<Warehouse> warehouses;

    public ReportService(ProductService productService, InventoryService inventoryService,
                         OrderService orderService, List<Supplier> suppliers, List<Warehouse> warehouses) {
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;
        this.suppliers = suppliers;
        this.warehouses = warehouses;
    }

    public void executeReportMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Supply Chain Reports & Computational Analytics ---");
            System.out.println("1. Executive Summary & Inventory Valuation");
            System.out.println("2. Optimize Analytics Data Pipeline (Matrix-Chain Multiplication)");
            System.out.println("3. Frequently Searched Product Index (Optimal BST Structure)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select report: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    displayExecutiveSummary();
                    break;
                case "2":
                    runMCMOptimizationWorkflow(scanner);
                    break;
                case "3":
                    displayOptimalBSTReportWorkflow(scanner);
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 0.");
            }
        }
    }

    // =========================================================================
    // 1. EXECUTIVE SUMMARY & VALUATION
    // =========================================================================

    public void displayExecutiveSummary() {
        List<Product> products = productService.getAllProducts();
        double totalKg = 0;
        double totalValuation = 0;

        for (Product p : products) {
            totalKg += p.getQuantityKg();
            totalValuation += p.getQuantityKg() * p.getPricePerKg();
        }

        List<OrderService.CustomerOrder> orders = orderService.getOrderHistory();
        double totalRevenue = 0;
        for (OrderService.CustomerOrder o : orders) {
            totalRevenue += o.getTotalPrice();
        }

        System.out.println("\n=================================================================================");
        System.out.println("                   FOODCHAIN PLATFORM EXECUTIVE DASHBOARD                        ");
        System.out.println("=================================================================================");
        System.out.printf("Total Tracked Products       : %d%n", products.size());
        System.out.printf("Total Agricultural Suppliers : %d%n", suppliers.size());
        System.out.printf("Active Regional Warehouses   : %d%n", warehouses.size());
        System.out.printf("Total Stored Food Quantity   : %.1f kg%n", totalKg);
        System.out.printf("Total Inventory Asset Value  : Rs. %.2f%n", totalValuation);
        System.out.printf("Customer Orders Fulfilled    : %d%n", orders.size());
        System.out.printf("Realized Order Sales Revenue : Rs. %.2f%n", totalRevenue);
        System.out.println("=================================================================================");
        inventoryService.viewWarehouseCapacities();
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    // =========================================================================
    // 2. MATRIX-CHAIN MULTIPLICATION (MCM) DP
    // =========================================================================

    public void runMCMOptimizationWorkflow(Scanner scanner) {
        System.out.println("\n=================================================================================");
        System.out.println("     SUPPLY CHAIN ANALYTICS PIPELINE OPTIMIZATION (MATRIX-CHAIN MULTIPLICATION)  ");
        System.out.println("=================================================================================");
        System.out.println("The FoodChain Business Intelligence engine aggregates multi-dimensional data:");
        System.out.println("  Matrix M1 [1  x 20]: Daily Regional Demand Vector");
        System.out.println("  Matrix M2 [20 x  5]: Product-to-Supplier Sourcing Matrix");
        System.out.println("  Matrix M3 [5  x  3]: Supplier-to-Warehouse Allocation Matrix");
        System.out.println("  Matrix M4 [3  x 12]: Warehouse-to-Retail Depot Distribution Matrix");
        System.out.println("  Matrix M5 [12 x  1]: Regional Tariff & Freight Factor Matrix");
        System.out.println("---------------------------------------------------------------------------------");

        // Standard dimensions: p = {1, 20, 5, 3, 12, 1}
        int[] defaultDims = {1, 20, 5, 3, 12, 1};

        System.out.print("Run standard 5-stage pipeline optimization? (Y/N, default Y): ");
        String ans = scanner.nextLine().trim();

        int[] dims = defaultDims;
        if (ans.equalsIgnoreCase("N")) {
            System.out.println("Enter custom matrix dimensions separated by commas (e.g., 10,20,30,40,30): ");
            String custom = scanner.nextLine().trim();
            try {
                String[] parts = custom.split(",");
                int[] parsed = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    parsed[i] = Integer.parseInt(parts[i].trim());
                }
                if (parsed.length >= 3) {
                    dims = parsed;
                } else {
                    System.out.println("At least 2 matrices (3 dimensions) needed. Using default.");
                }
            } catch (Exception e) {
                System.out.println("Invalid format. Reverting to default dimensions.");
            }
        }

        // Solve using Matrix-Chain Multiplication DP
        MCMResult result = DynamicProgramming.solveMCM(dims);

        System.out.println("\n---------------------------------------------------------------------------------");
        System.out.println("                      OPTIMAL COMPUTATION EXECUTION PLAN                         ");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Dimension Vector p            : " + java.util.Arrays.toString(result.dimensions));
        System.out.println("Number of Analytical Matrices : " + (result.dimensions.length - 1));
        System.out.println("Optimal Parenthesization      : " + result.optimalParenthesization);
        System.out.println("Minimum Scalar Operations     : " + result.minScalarMultiplications + " multiplications");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("IMPORTANT CLARIFICATION:");
        System.out.println("Matrix-Chain Multiplication (MCM) determines the mathematically optimal order");
        System.out.println("to evaluate algebraic matrix expressions in our data warehouse analytics engine.");
        System.out.println("It optimizes CPU computations, NOT physical truck transportation routes.");
        System.out.println("---------------------------------------------------------------------------------");
    }

    // =========================================================================
    // 3. OPTIMAL BINARY SEARCH TREE (OBST) REPORT
    // =========================================================================

    public void displayOptimalBSTReportWorkflow(Scanner scanner) {
        OBSTResult obst = productService.getCachedOBST();

        System.out.println("\n=================================================================================");
        System.out.println("              FREQUENTLY SEARCHED PRODUCT INDEX (OPTIMAL BST)                    ");
        System.out.println("=================================================================================");
        System.out.printf("Total Indexed Product Keys : %d%n", obst.sortedKeys.length);
        System.out.printf("Theoretical Expected Cost  : %.4f comparisons / lookup%n", obst.expectedCost);
        System.out.println("---------------------------------------------------------------------------------");
        System.out.println("Top Indexed Products by Customer Search Frequency:");
        System.out.printf("%-6s | %-18s | %-12s | %-14s%n", "Rank", "Product Name", "Search Demand", "Unit Price");
        System.out.println("---------------------------------------------------------------------------------");

        // Sort by demand score descending for display
        List<Product> sortedDemand = new ArrayList<>(productService.getAllProducts());
        sortedDemand.sort((a, b) -> Integer.compare(b.getDemandScore(), a.getDemandScore()));

        for (int i = 0; i < Math.min(8, sortedDemand.size()); i++) {
            Product p = sortedDemand.get(i);
            System.out.printf("%-6d | %-18s | %4d queries | Rs.%-6.2f/kg%n",
                    (i + 1), p.getProductName(), p.getDemandScore(), p.getPricePerKg());
        }

        System.out.println("---------------------------------------------------------------------------------");
        System.out.print("Test real-time lookup inside the constructed Optimal BST? (Enter product name, or Enter to skip): ");
        String testKey = scanner.nextLine().trim();
        if (!testKey.isEmpty()) {
            OBSTSearchResult res = obst.search(testKey);
            if (res.found && res.data instanceof Product) {
                Product p = (Product) res.data;
                System.out.println("\n[Lookup Succeeded!]");
                System.out.println("Product         : " + p.getProductName() + " (ID: " + p.getProductId() + ")");
                System.out.println("Tree Steps Used : " + res.comparisons + " comparison step(s)");
                System.out.printf("Available Stock : %.1f kg%n", p.getQuantityKg());
            } else {
                System.out.println("Key '" + testKey + "' not found. Evaluated " + res.comparisons + " steps.");
            }
        }
    }
}
