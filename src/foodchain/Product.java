package foodchain;

/**
 * Represents a food product in the FoodChain supply system.
 */
public class Product {
    private String productId;
    private String productName;
    private String supplierId;
    private double quantityKg;
    private double pricePerKg;
    private String description;
    private String qualityReport;
    private int demandScore;            // Used as search frequency weight for Optimal BST
    private int storageResourceUnits;   // Storage units needed for warehouse budget allocation
    private int benefitScore;           // Strategic benefit/priority score for Bitmask DP

    public Product(String productId, String productName, String supplierId,
                   double quantityKg, double pricePerKg, String description,
                   String qualityReport, int demandScore,
                   int storageResourceUnits, int benefitScore) {
        this.productId = productId;
        this.productName = productName;
        this.supplierId = supplierId;
        this.quantityKg = quantityKg;
        this.pricePerKg = pricePerKg;
        this.description = description;
        this.qualityReport = qualityReport;
        this.demandScore = demandScore;
        this.storageResourceUnits = storageResourceUnits;
        this.benefitScore = benefitScore;
    }

    // Copy constructor for safe demo isolation
    public Product(Product other) {
        this.productId = other.productId;
        this.productName = other.productName;
        this.supplierId = other.supplierId;
        this.quantityKg = other.quantityKg;
        this.pricePerKg = other.pricePerKg;
        this.description = other.description;
        this.qualityReport = other.qualityReport;
        this.demandScore = other.demandScore;
        this.storageResourceUnits = other.storageResourceUnits;
        this.benefitScore = other.benefitScore;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public double getQuantityKg() { return quantityKg; }
    public void setQuantityKg(double quantityKg) { this.quantityKg = quantityKg; }

    public double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(double pricePerKg) { this.pricePerKg = pricePerKg; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getQualityReport() { return qualityReport; }
    public void setQualityReport(String qualityReport) { this.qualityReport = qualityReport; }

    public int getDemandScore() { return demandScore; }
    public void setDemandScore(int demandScore) { this.demandScore = demandScore; }

    public int getStorageResourceUnits() { return storageResourceUnits; }
    public void setStorageResourceUnits(int storageResourceUnits) { this.storageResourceUnits = storageResourceUnits; }

    public int getBenefitScore() { return benefitScore; }
    public void setBenefitScore(int benefitScore) { this.benefitScore = benefitScore; }

    public void reduceQuantity(double amountKg) {
        this.quantityKg -= amountKg;
    }

    public void addQuantity(double amountKg) {
        this.quantityKg += amountKg;
    }

    public void incrementDemand() {
        this.demandScore++;
    }

    @Override
    public String toString() {
        return String.format("%s | %-16s | %6.1f kg | Rs.%-5.2f/kg | Supplier: %s",
                productId, productName, quantityKg, pricePerKg, supplierId);
    }
}
