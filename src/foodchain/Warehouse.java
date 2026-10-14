package foodchain;

/**
 * Represents a regional storage facility or warehouse in FoodChain.
 */
public class Warehouse {
    private String warehouseId;
    private String warehouseName;
    private String location;
    private double totalCapacityKg;
    private double currentOccupancyKg;

    public Warehouse(String warehouseId, String warehouseName, String location,
                     double totalCapacityKg, double currentOccupancyKg) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.location = location;
        this.totalCapacityKg = totalCapacityKg;
        this.currentOccupancyKg = currentOccupancyKg;
    }

    public String getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public String getLocation() { return location; }
    public double getTotalCapacityKg() { return totalCapacityKg; }
    public double getCurrentOccupancyKg() { return currentOccupancyKg; }

    public double getAvailableCapacityKg() {
        return Math.max(0, totalCapacityKg - currentOccupancyKg);
    }

    public void updateOccupancy(double deltaKg) {
        this.currentOccupancyKg += deltaKg;
    }

    @Override
    public String toString() {
        return String.format("%s | %-28s | %-12s | Occupancy: %.1f / %.1f kg (%.1f%%)",
                warehouseId, warehouseName, location, currentOccupancyKg, totalCapacityKg,
                (currentOccupancyKg / totalCapacityKg) * 100);
    }
}
