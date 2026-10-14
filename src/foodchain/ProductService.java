package foodchain;

import foodchain.algorithms.DynamicProgramming;
import foodchain.algorithms.DynamicProgramming.OBSTResult;
import foodchain.algorithms.DynamicProgramming.OBSTSearchResult;
import foodchain.algorithms.StringAlgorithms;

import java.util.*;

/**
 * Handles food product catalog operations, product viewing, and search workflows.
 * Integrates M1 (KMP, Rabin-Karp) and M3 (Levenshtein, Damerau-Levenshtein, Optimal BST).
 */
public class ProductService {

    private final List<Product> products;
    private final List<Supplier> suppliers;
    private final List<Shipment> shipments;

    // Cache for Optimal BST built from current product search frequencies
    private OBSTResult cachedOBST;
    private boolean obstNeedsRebuild = true;

    public ProductService(List<Product> products, List<Supplier> suppliers, List<Shipment> shipments) {
        this.products = products;
        this.suppliers = suppliers;
        this.shipments = shipments;
    }

    // =========================================================================
    // FEATURE 1: VIEW AVAILABLE FOOD PRODUCTS
    // =========================================================================

    public void displayAllProducts() {
        System.out.println("\n=================================================================================");
        System.out.println("                         AVAILABLE FOOD PRODUCTS IN CATALOG                      ");
        System.out.println("=================================================================================");
        System.out.printf("%-6s | %-18s | %-12s | %-12s | %-10s%n",
                "ID", "Product Name", "Quantity", "Price", "Supplier");
        System.out.println("---------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.printf("%-6s | %-18s | %8.1f kg | Rs.%-8.2f | %-10s%n",
                    p.getProductId(),
                    p.getProductName(),
                    p.getQuantityKg(),
                    p.getPricePerKg(),
                    p.getSupplierId());
        }
        System.out.println("=================================================================================");
        System.out.println("Total Products Listed: " + products.size());
    }

    public void displayProductDetails(String productId) {
        Product p = findProductById(productId);
        if (p == null) {
            System.out.println("Error: No product found with ID '" + productId + "'.");
            return;
        }

        Supplier sup = findSupplierById(p.getSupplierId());
        String supplierName = (sup != null) ? sup.getSupplierName() : "Unknown Supplier";

        System.out.println("\n-------------------------------------------------------------");
        System.out.println("                   PRODUCT SPECIFICATION SHEET               ");
        System.out.println("-------------------------------------------------------------");
        System.out.println("Product ID        : " + p.getProductId());
        System.out.println("Product Name      : " + p.getProductName());
        System.out.println("Supplier          : " + p.getSupplierId() + " (" + supplierName + ")");
        System.out.printf("Current Stock     : %.1f kg%n", p.getQuantityKg());
        System.out.printf("Unit Price        : Rs. %.2f / kg%n", p.getPricePerKg());
        System.out.println("Storage Units     : " + p.getStorageResourceUnits() + " units (Warehouse Bay allocation)");
        System.out.println("Benefit Score     : " + p.getBenefitScore() + " / 100 (Strategic priority)");
        System.out.println("Search Demand     : " + p.getDemandScore() + " (OBST access frequency)");
        System.out.println("Description       : " + p.getDescription());
        System.out.println("Quality Report    : " + p.getQualityReport());
        System.out.println("-------------------------------------------------------------");
    }

    // =========================================================================
    // FEATURE 2: SEARCH FOOD PRODUCTS
    // =========================================================================

    /**
     * Search workflow integrating KMP, Rabin-Karp, Damerau-Levenshtein, and Optimal BST.
     */
    public void executeInteractiveSearch(Scanner scanner) {
        boolean searching = true;
        while (searching) {
            System.out.println("\n--- Search Food Products ---");
            System.out.println("1. Search by Product Name (KMP with Spell Correction)");
            System.out.println("2. Lookup by Code / ID (Rabin-Karp Rolling Hash)");
            System.out.println("3. Frequently Searched Quick Lookup (Optimal BST)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select search mode: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    searchByNameWorkflow(scanner);
                    break;
                case "2":
                    searchByCodeWorkflow(scanner);
                    break;
                case "3":
                    searchByOptimalBSTWorkflow(scanner);
                    break;
                case "0":
                    searching = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 0.");
            }
        }
    }

    /**
     * 1. KMP Search on Product Name with Levenshtein/Damerau-Levenshtein auto-suggestion.
     */
    public void searchByNameWorkflow(Scanner scanner) {
        System.out.print("\nEnter product name to search: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        // Search products using KMP
        List<Product> matches = searchProductsByKMP(query);

        if (!matches.isEmpty()) {
            displaySearchResults("Matching products found for '" + query + "':", matches);
            for (Product p : matches) {
                p.incrementDemand(); // Update search frequency
            }
            obstNeedsRebuild = true;
            return;
        }

        // If no match found, use Damerau-Levenshtein to suggest closest valid product name
        List<String> productNames = new ArrayList<>();
        for (Product p : products) {
            productNames.add(p.getProductName());
        }

        String suggestion = DynamicProgramming.suggestClosest(query, productNames, 3);
        if (suggestion != null) {
            System.out.print("\nDid you mean " + suggestion + "? (Y/N): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("Y")) {
                List<Product> suggestedMatches = searchProductsByKMP(suggestion);
                displaySearchResults("Matching products for '" + suggestion + "':", suggestedMatches);
                for (Product p : suggestedMatches) {
                    p.incrementDemand();
                }
                obstNeedsRebuild = true;
            } else {
                System.out.println("No products matched your exact query.");
            }
        } else {
            System.out.println("No matching products found, and no close spelling matches identified.");
        }
    }

    /**
     * 2. Rabin-Karp Code-Based Search for Product IDs and Shipment Codes.
     */
    public void searchByCodeWorkflow(Scanner scanner) {
        System.out.print("\nEnter Code or ID (e.g. P101, SUP01, SHP-2026-001): ");
        String code = scanner.nextLine().trim();
        if (code.isEmpty()) {
            System.out.println("Search code cannot be empty.");
            return;
        }

        System.out.println("\n[Rabin-Karp Rolling Hash Search Execution]");
        boolean foundAny = false;

        // Search products by ID
        for (Product p : products) {
            List<Integer> rkMatches = StringAlgorithms.searchRabinKarp(p.getProductId(), code, false);
            if (!rkMatches.isEmpty()) {
                System.out.println("Product Matched: " + p.getProductId() + " | " + p.getProductName() +
                        " | Stock: " + p.getQuantityKg() + " kg | Price: Rs." + p.getPricePerKg() + "/kg");
                foundAny = true;
                p.incrementDemand();
                obstNeedsRebuild = true;
            }
        }

        // Search shipments by code
        for (Shipment s : shipments) {
            List<Integer> rkShipmentMatches = StringAlgorithms.searchRabinKarp(s.getShipmentId(), code, false);
            if (!rkShipmentMatches.isEmpty()) {
                System.out.println("Shipment Record Matched: " + s);
                foundAny = true;
            }
        }

        // Search suppliers by ID
        for (Supplier sup : suppliers) {
            List<Integer> rkSupMatches = StringAlgorithms.searchRabinKarp(sup.getSupplierId(), code, false);
            if (!rkSupMatches.isEmpty()) {
                System.out.println("Supplier Matched: " + sup);
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No system records matched the code '" + code + "'.");
        }
    }

    /**
     * 3. Optimal BST Search Workflow.
     * Uses the pre-calculated Optimal Binary Search Tree to look up frequently searched products.
     */
    public void searchByOptimalBSTWorkflow(Scanner scanner) {
        ensureOptimalBST();

        System.out.print("\nEnter exact product name for fast catalog lookup: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) return;

        OBSTSearchResult result = cachedOBST.search(query);
        if (result.found && result.data instanceof Product) {
            Product p = (Product) result.data;
            System.out.println("\n[Optimal BST Query Resolved in " + result.comparisons + " comparison step(s)]");
            System.out.println("Product ID        : " + p.getProductId());
            System.out.println("Product Name      : " + p.getProductName());
            System.out.printf("Current Stock     : %.1f kg%n", p.getQuantityKg());
            System.out.printf("Price             : Rs. %.2f/kg%n", p.getPricePerKg());
            System.out.println("Supplier ID       : " + p.getSupplierId());
            System.out.println("Search Demand     : " + p.getDemandScore() + " hits");

            p.incrementDemand();
            obstNeedsRebuild = true;
        } else {
            System.out.println("Product '" + query + "' was not found in the catalog index (Steps evaluated: "
                    + result.comparisons + ").");
        }
    }

    // =========================================================================
    // HELPER & ALGORITHM INTEGRATION METHODS
    // =========================================================================

    public List<Product> searchProductsByKMP(String query) {
        List<Product> results = new ArrayList<>();
        for (Product p : products) {
            // Search product name using KMP
            boolean nameMatch = StringAlgorithms.containsKMP(p.getProductName(), query, false);
            boolean descMatch = StringAlgorithms.containsKMP(p.getDescription(), query, false);
            if (nameMatch || descMatch) {
                results.add(p);
            }
        }
        return results;
    }

    public void ensureOptimalBST() {
        if (cachedOBST == null || obstNeedsRebuild) {
            int n = products.size();
            String[] keys = new String[n];
            Object[] data = new Object[n];
            double[] frequencies = new double[n];

            double totalDemand = 0;
            for (Product p : products) {
                totalDemand += p.getDemandScore();
            }

            for (int i = 0; i < n; i++) {
                Product p = products.get(i);
                keys[i] = p.getProductName();
                data[i] = p;
                frequencies[i] = (totalDemand > 0) ? (p.getDemandScore() / totalDemand) : (1.0 / n);
            }

            cachedOBST = DynamicProgramming.buildOptimalBST(keys, data, frequencies);
            obstNeedsRebuild = false;
        }
    }

    public OBSTResult getCachedOBST() {
        ensureOptimalBST();
        return cachedOBST;
    }

    private void displaySearchResults(String header, List<Product> list) {
        System.out.println("\n" + header);
        System.out.println("---------------------------------------------------------------------------------");
        for (Product p : list) {
            System.out.printf("%-6s | %-18s | %6.1f kg | Rs.%-6.2f/kg | %s%n",
                    p.getProductId(), p.getProductName(), p.getQuantityKg(),
                    p.getPricePerKg(), p.getQualityReport());
        }
        System.out.println("---------------------------------------------------------------------------------");
    }

    public Product findProductById(String id) {
        if (id == null) return null;
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(id.trim())) {
                return p;
            }
        }
        return null;
    }

    public Product findProductByNameExact(String name) {
        if (name == null) return null;
        for (Product p : products) {
            if (p.getProductName().equalsIgnoreCase(name.trim())) {
                return p;
            }
        }
        return null;
    }

    public Supplier findSupplierById(String id) {
        if (id == null) return null;
        for (Supplier s : suppliers) {
            if (s.getSupplierId().equalsIgnoreCase(id.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Supplier> getAllSuppliers() {
        return suppliers;
    }

    public List<Shipment> getAllShipments() {
        return shipments;
    }
}
