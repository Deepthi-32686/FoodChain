package foodchain;

/**
 * Represents an agricultural or dairy supplier in the food network.
 */
public class Supplier {
    private String supplierId;
    private String supplierName;
    private String location;
    private String contactNumber;
    private String category;

    public Supplier(String supplierId, String supplierName, String location, String contactNumber, String category) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.location = location;
        this.contactNumber = contactNumber;
        this.category = category;
    }

    public String getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getLocation() { return location; }
    public String getContactNumber() { return contactNumber; }
    public String getCategory() { return category; }

    @Override
    public String toString() {
        return String.format("%s | %-25s | %-15s | Contact: %s | Category: %s",
                supplierId, supplierName, location, contactNumber, category);
    }
}
