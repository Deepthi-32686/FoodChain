package foodchain;

import foodchain.algorithms.DynamicProgramming;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Handles customer order placement, order validation, stock updates, and order history.
 * Integrates M1 (KMP) and M3 (Damerau-Levenshtein edit-distance suggestion).
 */
public class OrderService {

    public static class CustomerOrder {
        private final String orderId;
        private final String productId;
        private final String productName;
        private final double quantityKg;
        private final double pricePerKg;
        private final double totalPrice;
        private final String orderTime;

        public CustomerOrder(String orderId, String productId, String productName,
                             double quantityKg, double pricePerKg, double totalPrice, String orderTime) {
            this.orderId = orderId;
            this.productId = productId;
            this.productName = productName;
            this.quantityKg = quantityKg;
            this.pricePerKg = pricePerKg;
            this.totalPrice = totalPrice;
            this.orderTime = orderTime;
        }

        public String getOrderId() { return orderId; }
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getQuantityKg() { return quantityKg; }
        public double getPricePerKg() { return pricePerKg; }
        public double getTotalPrice() { return totalPrice; }
        public String getOrderTime() { return orderTime; }

        @Override
        public String toString() {
            return String.format("%-8s | %-6s | %-16s | %6.1f kg | Rs.%-7.2f/kg | Total: Rs.%-9.2f | %s",
                    orderId, productId, productName, quantityKg, pricePerKg, totalPrice, orderTime);
        }
    }

    private final ProductService productService;
    private final List<CustomerOrder> orderHistory = new ArrayList<>();
    private int orderCounter = 1001;

    public OrderService(ProductService productService) {
        this.productService = productService;
    }

    public void executeOrderMenu(Scanner scanner) {
        boolean active = true;
        while (active) {
            System.out.println("\n--- Customer Order Management ---");
            System.out.println("1. Place New Customer Order");
            System.out.println("2. View Order History");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select order operation: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    placeOrderWorkflow(scanner);
                    break;
                case "2":
                    viewOrderHistory();
                    break;
                case "0":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, or 0.");
            }
        }
    }

    /**
     * Executes the customer order workflow step-by-step as specified.
     */
    public void placeOrderWorkflow(Scanner scanner) {
        System.out.println("\n=================================================================================");
        System.out.println("                            PLACE CUSTOMER ORDER                                 ");
        System.out.println("=================================================================================");

        System.out.print("Enter product name: ");
        String inputName = scanner.nextLine().trim();
        if (inputName.isEmpty()) {
            System.out.println("Product name cannot be empty.");
            return;
        }

        // Step 1 & 2: Search using KMP
        List<Product> matches = productService.searchProductsByKMP(inputName);

        // Step 3: If not found, use Damerau-Levenshtein to suggest correction
        if (matches.isEmpty()) {
            List<String> allNames = new ArrayList<>();
            for (Product p : productService.getAllProducts()) {
                allNames.add(p.getProductName());
            }

            String suggestion = DynamicProgramming.suggestClosest(inputName, allNames, 3);
            if (suggestion != null) {
                System.out.print("Did you mean " + suggestion + "? (Y/N): ");
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("Y")) {
                    matches = productService.searchProductsByKMP(suggestion);
                } else {
                    System.out.println("Order cancelled. Product not found.");
                    return;
                }
            } else {
                System.out.println("Product '" + inputName + "' not found in catalog, and no close spelling matches found.");
                return;
            }
        }

        if (matches.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }

        // Step 4: Display matching products
        System.out.println("\nMatching products in stock:");
        System.out.printf("%-6s | %-16s | %-12s | %-12s%n", "ID", "Product Name", "Available", "Price");
        System.out.println("--------------------------------------------------");
        for (Product p : matches) {
            System.out.printf("%-6s | %-16s | %8.1f kg | Rs.%-6.2f/kg%n",
                    p.getProductId(), p.getProductName(), p.getQuantityKg(), p.getPricePerKg());
        }

        // Step 5: Select product ID
        Product selectedProduct = null;
        if (matches.size() == 1) {
            selectedProduct = matches.get(0);
            System.out.println("Auto-selected matching product: " + selectedProduct.getProductId() +
                    " (" + selectedProduct.getProductName() + ")");
        } else {
            System.out.print("\nEnter Product ID to purchase: ");
            String idChoice = scanner.nextLine().trim();
            for (Product p : matches) {
                if (p.getProductId().equalsIgnoreCase(idChoice)) {
                    selectedProduct = p;
                    break;
                }
            }
            if (selectedProduct == null) {
                System.out.println("Invalid Product ID chosen.");
                return;
            }
        }

        // Step 6 & 7: Customer enters quantity & check inventory
        System.out.print("Enter quantity to order (kg): ");
        double quantity;
        try {
            quantity = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric quantity.");
            return;
        }

        if (quantity <= 0) {
            System.out.println("Order quantity must be greater than zero.");
            return;
        }

        if (quantity > selectedProduct.getQuantityKg()) {
            System.out.printf("Insufficient inventory! Requested: %.1f kg, Available: %.1f kg.%n",
                    quantity, selectedProduct.getQuantityKg());
            return;
        }

        // Step 8: Calculate total price
        double totalPrice = quantity * selectedProduct.getPricePerKg();

        System.out.println("\n--------------------------------------------------");
        System.out.println("                ORDER SUMMARY REVIEW              ");
        System.out.println("--------------------------------------------------");
        System.out.println("Product         : " + selectedProduct.getProductName() + " (" + selectedProduct.getProductId() + ")");
        System.out.printf("Unit Price      : Rs. %.2f / kg%n", selectedProduct.getPricePerKg());
        System.out.printf("Order Quantity  : %.1f kg%n", quantity);
        System.out.printf("Available Stock : %.1f kg%n", selectedProduct.getQuantityKg());
        System.out.printf("Total Payable   : Rs. %.2f%n", totalPrice);
        System.out.println("--------------------------------------------------");

        // Step 9: Confirm order
        System.out.print("Confirm order and deduct inventory? (Y/N): ");
        String orderConfirm = scanner.nextLine().trim();

        if (orderConfirm.equalsIgnoreCase("Y")) {
            // Step 10: Deduct stock only upon confirmation
            selectedProduct.reduceQuantity(quantity);
            selectedProduct.incrementDemand();

            // Step 11: Generate Order ID and record in memory
            String orderId = "ORD" + (orderCounter++);
            CustomerOrder order = new CustomerOrder(
                    orderId, selectedProduct.getProductId(), selectedProduct.getProductName(),
                    quantity, selectedProduct.getPricePerKg(), totalPrice, new Date().toString()
            );
            orderHistory.add(order);

            System.out.println("\n==================================================");
            System.out.println("                 ORDER CONFIRMED!                 ");
            System.out.println("==================================================");
            System.out.println("Order ID        : " + orderId);
            System.out.println("Product         : " + selectedProduct.getProductName());
            System.out.printf("Quantity        : %.1f kg%n", quantity);
            System.out.printf("Total           : Rs. %.2f%n", totalPrice);
            System.out.printf("Remaining Stock : %.1f kg%n", selectedProduct.getQuantityKg());
            System.out.println("Status          : Reserved for Fulfillment");
            System.out.println("==================================================");
            System.out.println("Note: Route and vehicle dispatch planning will be scheduled in Phase 2.");
        } else {
            System.out.println("Order has been cancelled. Inventory unchanged.");
        }
    }

    public void viewOrderHistory() {
        System.out.println("\n=================================================================================");
        System.out.println("                             CUSTOMER ORDER HISTORY                              ");
        System.out.println("=================================================================================");
        if (orderHistory.isEmpty()) {
            System.out.println("No customer orders recorded yet in current session.");
        } else {
            System.out.printf("%-8s | %-6s | %-16s | %-10s | %-12s | %-16s | %s%n",
                    "Order ID", "Prod", "Product Name", "Quantity", "Price", "Total Cost", "Date & Time");
            System.out.println("---------------------------------------------------------------------------------");
            double totalRevenue = 0;
            for (CustomerOrder o : orderHistory) {
                totalRevenue += o.getTotalPrice();
                System.out.printf("%-8s | %-6s | %-16s | %6.1f kg | Rs.%-7.2f/kg | Rs.%-11.2f | %s%n",
                        o.getOrderId(), o.getProductId(), o.getProductName(),
                        o.getQuantityKg(), o.getPricePerKg(), o.getTotalPrice(), o.getOrderTime());
            }
            System.out.println("---------------------------------------------------------------------------------");
            System.out.printf("Total Orders: %d | Total Sales Revenue: Rs. %.2f%n", orderHistory.size(), totalRevenue);
        }
        System.out.println("=================================================================================");
    }

    public List<CustomerOrder> getOrderHistory() {
        return orderHistory;
    }
}
