package foodchain;

/**
 * Represents a shipment moving inventory between suppliers and warehouses.
 */
public class Shipment {
    private String shipmentId;
    private String productId;
    private String supplierId;
    private String warehouseId;
    private double quantityKg;
    private String status;
    private String dispatchDate;

    public Shipment(String shipmentId, String productId, String supplierId,
                    String warehouseId, double quantityKg, String status, String dispatchDate) {
        this.shipmentId = shipmentId;
        this.productId = productId;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
        this.quantityKg = quantityKg;
        this.status = status;
        this.dispatchDate = dispatchDate;
    }

    public String getShipmentId() { return shipmentId; }
    public String getProductId() { return productId; }
    public String getSupplierId() { return supplierId; }
    public String getWarehouseId() { return warehouseId; }
    public double getQuantityKg() { return quantityKg; }
    public String getStatus() { return status; }
    public String getDispatchDate() { return dispatchDate; }

    @Override
    public String toString() {
        return String.format("%-14s | Prod: %-5s | From: %-5s -> To: %-5s | %6.1f kg | Status: %-10s | %s",
                shipmentId, productId, supplierId, warehouseId, quantityKg, status, dispatchDate);
    }
}
