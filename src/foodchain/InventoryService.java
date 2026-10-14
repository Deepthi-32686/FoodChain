package foodchain;

import foodchain.algorithms.DynamicProgramming;
import foodchain.algorithms.DynamicProgramming.BitmaskAllocationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles inventory levels, warehouse stock tracking, and limited warehouse resource allocation.
 * Integrates M3 Bitmask DP for optimal storage subset selection.
 */
public class InventoryService {

    private final List<Product> products;
    private final List<Warehouse> warehouses;

    public InventoryService(List<Product> products, List<Warehouse> warehouses) {
        this.products = products;
        this.warehouses = warehouses;
    }

    public void executeInventoryMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. View Current Inventory & Stock Levels");
            System.out.println("2. Check Warehouse Capacities & Utilization");
            System.out.println("3. Optimize Storage Allocation for Limited Warehouse Bay (Bitmask DP)");
            System.out.println("4. Low Stock Warning Alerts (< 50 kg)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select inventory operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewStockLevels();
                    break;
                case "2":
                    viewWarehouseCapacities();
                    break;
                case "3":
                    optimizeStorageAllocationWorkflow(scanner);
                    break;
                case "4":
                    checkLowStockAlerts();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, 3, 4, or 0.");
            }
        }
    }

    public void viewStockLevels() {
        System.out.println("\n=================================================================================");
        System.out.println("                           CURRENT INVENTORY STOCK LEVELS                        ");
        System.out.println("=================================================================================");
        System.out.printf("%-6s | %-18s | %-12s | %-12s | %-12s%n",
                "ID", "Product Name", "Available", "Storage Req", "Benefit Score");
        System.out.println("---------------------------------------------------------------------------------");
        double totalKg = 0;
        for (Product p : products) {
            totalKg += p.getQuantityKg();
            System.out.printf("%-6s | %-18s | %8.1f kg | %4d units    | %4d / 100%n",
                    p.getProductId(), p.getProductName(), p.getQuantityKg(),
                    p.getStorageResourceUnits(), p.getBenefitScore());
        }
        System.out.println("=================================================================================");
        System.out.printf("Total Tracked Inventory: %.1f kg across %d products%n", totalKg, products.size());
    }

    public void viewWarehouseCapacities() {
        System.out.println("\n=================================================================================");
        System.out.println("                         REGIONAL WAREHOUSE UTILIZATION                          ");
        System.out.println("=================================================================================");
        for (Warehouse w : warehouses) {
            System.out.println(w);
        }
        System.out.println("=================================================================================");
    }

    public void checkLowStockAlerts() {
        System.out.println("\n--- Low Stock Alerts (< 50 kg) ---");
        boolean any = false;
        for (Product p : products) {
            if (p.getQuantityKg() < 50.0) {
                System.out.printf("CRITICAL LOW STOCK: %s (%s) - Only %.1f kg remaining! (Unit Price: Rs. %.2f)%n",
                        p.getProductName(), p.getProductId(), p.getQuantityKg(), p.getPricePerKg());
                any = true;
            }
        }
        if (!any) {
            System.out.println("All product stock levels are healthy (>= 50 kg).");
        }
    }

    // =========================================================================
    // BITMASK DP STORAGE ALLOCATION WORKFLOW
    // =========================================================================

    public void optimizeStorageAllocationWorkflow(Scanner scanner) {
        System.out.println("\n=================================================================================");
        System.out.println("           LIMITED WAREHOUSE RESOURCE OPTIMIZATION (BITMASK DP)                  ");
        System.out.println("=================================================================================");
        System.out.println("A specialized temperature-controlled storage bay has limited resource units.");
        System.out.println("Selecting candidate food products for optimal storage benefit...");

        // Select a practical subset of N=10 candidate food products
        int candidateCount = Math.min(10, products.size());
        List<Product> candidates = new ArrayList<>();
        for (int i = 0; i < candidateCount; i++) {
            candidates.add(products.get(i));
        }

        System.out.println("\nCandidate Food Products Available for Bay Allocation:");
        System.out.printf("%-4s | %-6s | %-16s | %-14s | %-14s%n",
                "#", "ID", "Product Name", "Required Units", "Benefit Score");
        System.out.println("------------------------------------------------------------------");
        int[] weights = new int[candidateCount];
        int[] benefits = new int[candidateCount];

        for (int i = 0; i < candidateCount; i++) {
            Product p = candidates.get(i);
            weights[i] = p.getStorageResourceUnits();
            benefits[i] = p.getBenefitScore();
            System.out.printf("%-4d | %-6s | %-16s | %4d units     | %4d points%n",
                    (i + 1), p.getProductId(), p.getProductName(), weights[i], benefits[i]);
        }

        System.out.print("\nEnter available warehouse resource budget capacity (e.g., 15 - 30): ");
        int capacity = 20; // default
        try {
            String capInput = scanner.nextLine().trim();
            if (!capInput.isEmpty()) {
                capacity = Integer.parseInt(capInput);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input. Defaulting capacity to 20 units.");
            capacity = 20;
        }

        if (capacity <= 0) {
            System.out.println("Capacity must be positive.");
            return;
        }

        // Solve using Bitmask DP
        BitmaskAllocationResult result = DynamicProgramming.solveWarehouseAllocation(weights, benefits, capacity);

        System.out.println("\n------------------------------------------------------------------");
        System.out.println("                    OPTIMAL STORAGE ALLOCATION PLAN               ");
        System.out.println("------------------------------------------------------------------");
        System.out.println("Algorithm: Bitmask Dynamic Programming over " + (1 << candidateCount) + " subset states");
        System.out.println("Assigned Warehouse Bay Capacity: " + capacity + " resource units\n");
        System.out.println("Selected Products:");

        for (int idx : result.selectedIndices) {
            Product p = candidates.get(idx);
            System.out.printf("  [+] %-6s | %-16s | %2d units | Priority Benefit: %2d pts%n",
                    p.getProductId(), p.getProductName(), p.getStorageResourceUnits(), p.getBenefitScore());
        }

        System.out.println("------------------------------------------------------------------");
        System.out.println("Total Storage Units Used : " + result.totalWeightUsed + " units");
        System.out.println("Total Benefit Score      : " + result.totalBenefit + " points");
        System.out.println("Remaining Storage Space  : " + result.remainingCapacity + " units");
        System.out.println("------------------------------------------------------------------");
    }
}
