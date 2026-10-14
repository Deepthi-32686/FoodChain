package foodchain;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides comprehensive in-memory initial data for the FoodChain platform.
 * Features 20 food products, 5 suppliers, 3 regional warehouses, and active shipments.
 */
public class SampleData {

    public static List<Product> getSampleProducts() {
        List<Product> products = new ArrayList<>();

        products.add(new Product(
            "P101", "Rice", "SUP01", 120.0, 55.0,
            "Premium grade raw white rice grains harvested from sustainable fields.",
            "Fresh stock, packaging intact. Certified moisture grade.",
            95, 4, 85
        ));

        products.add(new Product(
            "P102", "Brown Rice", "SUP01", 60.0, 80.0,
            "Nutritious unpolished brown rice rich in dietary fiber and essential minerals.",
            "Moisture content within standard limits. Packaging intact.",
            60, 3, 70
        ));

        products.add(new Product(
            "P103", "Wheat", "SUP01", 200.0, 42.0,
            "Golden whole grain wheat harvested for industrial milling and bakeries.",
            "Moisture detected; unsafe storage conditions suspected. Requires re-drying.",
            80, 5, 65
        ));

        products.add(new Product(
            "P104", "Tomato", "SUP04", 45.0, 35.0,
            "Vine-ripened red tomatoes sorted for fresh consumer markets.",
            "Inspection note: 5% batch spoiled due to high humidity during transit.",
            70, 2, 60
        ));

        products.add(new Product(
            "P105", "Potato", "SUP04", 150.0, 28.0,
            "Farm fresh tuber potatoes suitable for long storage and cooking.",
            "Slight mold detected in corner burlap sacks. Quarantined for review.",
            75, 4, 55
        ));

        products.add(new Product(
            "P106", "Milk", "SUP03", 80.0, 62.0,
            "Pasteurized homogenized cow milk processed under strict temperature control.",
            "Package damaged during transport. Seal compromised on 4 crates.",
            90, 3, 90
        ));

        products.add(new Product(
            "P107", "Rice Flour", "SUP05", 90.0, 65.0,
            "Finely ground polished rice flour for gluten-free culinary preparations.",
            "Packaging intact, sealed double-layer bag, certified hygienic.",
            40, 2, 50
        ));

        products.add(new Product(
            "P108", "Apples", "SUP04", 50.0, 140.0,
            "Crisp Himalayan red apples handpicked for export grade retail.",
            "Certified crisp harvest. 2 crates bruised and damaged.",
            65, 3, 75
        ));

        products.add(new Product(
            "P109", "Onions", "SUP02", 110.0, 32.0,
            "Cured red onions with tight skins for extended pantry shelf life.",
            "Dry outer skins, packaging intact, no rot detected.",
            85, 3, 58
        ));

        products.add(new Product(
            "P110", "Green Gram", "SUP02", 75.0, 115.0,
            "Whole green moong beans packed with plant protein and fiber.",
            "Laboratory certified clean stock, no foreign matter.",
            45, 2, 52
        ));

        products.add(new Product(
            "P111", "Soybeans", "SUP02", 130.0, 72.0,
            "High protein soybeans intended for soy milk and tofu production.",
            "Storage moisture 11%, zero pest activity detected.",
            55, 4, 62
        ));

        products.add(new Product(
            "P112", "Sugar", "SUP05", 180.0, 48.0,
            "Refined crystalline cane sugar processed without harmful chemical bleaches.",
            "Clean crystallization, packaging intact, dry storage confirmed.",
            70, 5, 48
        ));

        products.add(new Product(
            "P113", "Cooking Oil", "SUP03", 95.0, 135.0,
            "Cold-pressed sunflower cooking oil packed in tamper-proof canisters.",
            "Minor container leak noted at warehouse bay. Needs wipe inspection.",
            82, 3, 80
        ));

        products.add(new Product(
            "P114", "Fresh Spinach", "SUP04", 30.0, 40.0,
            "Locally sourced green leafy spinach harvested within 24 hours.",
            "Fresh crisp leaves, washed and sanitized, refrigerated at 4C.",
            50, 1, 45
        ));

        products.add(new Product(
            "P115", "Carrots", "SUP04", 65.0, 45.0,
            "Crunchy farm-grown orange carrots washed and graded for retail.",
            "Standard quality, firmness tested, packaging intact.",
            48, 2, 42
        ));

        products.add(new Product(
            "P116", "Basmati Rice", "SUP01", 85.0, 110.0,
            "Aged long-grain aromatic royal basmati rice from the foothills.",
            "Aroma test passed, zero adulteration, packaging intact.",
            92, 4, 88
        ));

        products.add(new Product(
            "P117", "Yellow Lentils", "SUP02", 100.0, 98.0,
            "Polished toor dal lentils ideal for high nutrition daily meals.",
            "Quality certified, zero chemical residue, packaging intact.",
            68, 3, 64
        ));

        products.add(new Product(
            "P118", "Organic Honey", "SUP03", 40.0, 320.0,
            "Pure unprocessed wild forest honey tested for natural sweetness.",
            "Batch report: suspected contaminated lot isolated for testing.",
            35, 1, 95
        ));

        products.add(new Product(
            "P119", "Fresh Oranges", "SUP04", 70.0, 60.0,
            "Juicy Nagpur oranges hand-sorted for vitamin C richness.",
            "Fresh fruit batch, peel integrity verified, packaging intact.",
            58, 3, 50
        ));

        products.add(new Product(
            "P120", "Whole Wheat Flour", "SUP05", 140.0, 46.0,
            "Stone-ground whole wheat atta flour retaining natural bran and germ.",
            "Fine texture verified, zero weevils, packaging intact.",
            78, 4, 68
        ));

        return products;
    }

    public static List<Supplier> getSampleSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(new Supplier("SUP01", "Green Valley Farms", "Punjab", "+91-98112-34501", "Grains & Cereals"));
        suppliers.add(new Supplier("SUP02", "AgroFresh Organic Co.", "Maharashtra", "+91-98223-45602", "Pulses & Legumes"));
        suppliers.add(new Supplier("SUP03", "Highland Dairy Ltd.", "Gujarat", "+91-98334-56703", "Dairy & Oils"));
        suppliers.add(new Supplier("SUP04", "Sunrise Horticulture", "Karnataka", "+91-98445-67804", "Fresh Produce & Fruits"));
        suppliers.add(new Supplier("SUP05", "Golden Harvest Mills", "Haryana", "+91-98556-78905", "Flours & Refined Grains"));
        return suppliers;
    }

    public static List<Warehouse> getSampleWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        warehouses.add(new Warehouse("WH01", "Metro North Central Hub", "Delhi NCR", 50000.0, 34200.0));
        warehouses.add(new Warehouse("WH02", "Western Regional Depot", "Mumbai", 45000.0, 28100.0));
        warehouses.add(new Warehouse("WH03", "Southern Distribution Depot", "Bengaluru", 40000.0, 19800.0));
        return warehouses;
    }

    public static List<Shipment> getSampleShipments() {
        List<Shipment> shipments = new ArrayList<>();
        shipments.add(new Shipment("SHP-2026-001", "P101", "SUP01", "WH01", 120.0, "Delivered", "2026-10-01"));
        shipments.add(new Shipment("SHP-2026-002", "P106", "SUP03", "WH02", 80.0, "In Transit", "2026-10-07"));
        shipments.add(new Shipment("SHP-2026-003", "P104", "SUP04", "WH01", 45.0, "Delivered", "2026-10-05"));
        shipments.add(new Shipment("SHP-2026-004", "P108", "SUP04", "WH03", 50.0, "In Transit", "2026-10-08"));
        shipments.add(new Shipment("SHP-2026-005", "P116", "SUP01", "WH01", 85.0, "Delivered", "2026-10-03"));
        shipments.add(new Shipment("SHP-2026-006", "P113", "SUP03", "WH03", 95.0, "Dispatched", "2026-10-08"));
        return shipments;
    }

    /**
     * Creates an isolated deep copy of products for demo execution
     * to prevent modifying the user's active session state.
     */
    public static List<Product> cloneProducts(List<Product> original) {
        List<Product> copy = new ArrayList<>();
        for (Product p : original) {
            copy.add(new Product(p));
        }
        return copy;
    }
}
