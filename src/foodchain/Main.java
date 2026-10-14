package foodchain;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for FoodChain – Food Supply & Distribution Management Platform.
 * Pure Java console application covering all 6 modules (M1 - M6).
 * Runnable in Eclipse IDE by right-clicking Main.java -> Run As -> Java Application.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize in-memory repository from SampleData
        List<Product> products = SampleData.getSampleProducts();
        List<Supplier> suppliers = SampleData.getSampleSuppliers();
        List<Warehouse> warehouses = SampleData.getSampleWarehouses();
        List<Shipment> shipments = SampleData.getSampleShipments();

        // Initialize core domain services for all modules
        ProductService productService = new ProductService(products, suppliers, shipments);
        QualityService qualityService = new QualityService(products);
        InventoryService inventoryService = new InventoryService(products, warehouses);
        OrderService orderService = new OrderService(productService);
        DistributionService distributionService = new DistributionService(suppliers, warehouses);
        ConstraintService constraintService = new ConstraintService(suppliers, warehouses);
        ParallelAnalyticsService analyticsService = new ParallelAnalyticsService(products);
        ReportService reportService = new ReportService(productService, inventoryService, orderService, suppliers, warehouses);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (0-10): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleViewProducts(productService, scanner);
                    break;
                case "2":
                    productService.executeInteractiveSearch(scanner);
                    break;
                case "3":
                    qualityService.executeQualityMenu(scanner);
                    break;
                case "4":
                    inventoryService.executeInventoryMenu(scanner);
                    break;
                case "5":
                    orderService.executeOrderMenu(scanner);
                    break;
                case "6":
                    distributionService.executeDistributionMenu(scanner);
                    break;
                case "7":
                    constraintService.executeConstraintMenu(scanner);
                    break;
                case "8":
                    analyticsService.executeAnalyticsMenu(scanner);
                    break;
                case "9":
                    reportService.executeReportMenu(scanner);
                    break;
                case "10":
                    SupplyChainDemo.runDemo();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nThank you for using FoodChain Management Platform. Goodbye!");
                    break;
                default:
                    System.out.println("\n[!] Invalid selection. Please enter a valid menu number between 0 and 10.");
            }
        }

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n=================================================================================");
        System.out.println("                              WELCOME TO FOODCHAIN                               ");
        System.out.println("                   Food Supply & Distribution Management Platform                ");
        System.out.println("=================================================================================");
        System.out.println(" 1. View Available Food Products");
        System.out.println(" 2. Search Food Products (KMP, Rabin-Karp, OBST & Spell Correction)");
        System.out.println(" 3. Check Food Quality (Aho-Corasick, Suffix Arrays, SA-IS & Kasai LCP)");
        System.out.println(" 4. Manage Inventory (Stock Levels & Bitmask DP Bay Allocation)");
        System.out.println(" 5. Place Customer Order");
        System.out.println(" 6. Plan Food Distribution (Network Flow: Ford-Fulkerson, Dinic, Min-Cut)");
        System.out.println(" 7. Solve Supply Constraints & NP Reductions (SAT/3-SAT, Clique, Vertex Cover)");
        System.out.println(" 8. Parallel & Streaming Analytics (QuickSort, Reservoir, Blelloch, Brent)");
        System.out.println(" 9. View Supply Chain Reports (Executive Dashboards & MCM Optimization)");
        System.out.println("10. Run Complete Supply Chain Demo (M1 -> M6 End-to-End Walkthrough)");
        System.out.println(" 0. Exit");
        System.out.println("=================================================================================");
    }

    private static void handleViewProducts(ProductService productService, Scanner scanner) {
        productService.displayAllProducts();
        System.out.print("\nEnter Product ID to view full specification sheet (or press Enter to return): ");
        String pid = scanner.nextLine().trim();
        if (!pid.isEmpty()) {
            productService.displayProductDetails(pid);
        }
    }
}
