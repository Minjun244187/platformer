package gamelogic.world; // Or wherever you keep your player-related data classes

import gamelogic.Main;
import gamelogic.world.business.ProductType; // Import the canonical ProductType

import java.awt.*; // Keep if still used for any graphics-related properties in Product, otherwise can remove

public class Product {

    private String name;
    private ProductType type;
    private double price;
    private double productionCostPerUnit;
    private int currentSupply;
    private boolean isLaunched;

    // --- NEW: Fields to track per-turn production and sales for this specific product ---
    private int unitsProducedThisTurn;
    private int unitsSoldThisTurn;
    // --- END NEW ---

    public Product(String name, ProductType type, double price, double productionCostPerUnit, int initialSupply) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.productionCostPerUnit = productionCostPerUnit;
        this.currentSupply = initialSupply;
        this.isLaunched = false;
        this.unitsProducedThisTurn = 0; // Initialize
        this.unitsSoldThisTurn = 0;     // Initialize
    }

    // Getters
    public String getName() {
        return name;
    }

    public ProductType getType() {
        return type;
    }

    public double getCurrentPrice() { // Renamed for consistency with Business.java's currentPrice
        return price;
    }

    public double getProductionCostPerUnit() {
        return productionCostPerUnit;
    }

    public int getCurrentSupply() {
        return currentSupply;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public int getUnitsProducedThisTurn() {
        return unitsProducedThisTurn;
    }

    public int getUnitsSoldThisTurn() {
        return unitsSoldThisTurn;
    }


    // Setters (for editing)
    public void setName(String name) {
        this.name = name;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public void setCurrentPrice(double price) {
        this.price = price;
    }

    public void setProductionCostPerUnit(double productionCostPerUnit) {
        this.productionCostPerUnit = productionCostPerUnit;
        this.productionCostPerUnit += Main.extraProductionCost;
    }

    public void setCurrentSupply(int currentSupply) {
        this.currentSupply = currentSupply;
    }

    public void setUnitsProducedThisTurn(int unitsProducedThisTurn) {
        this.unitsProducedThisTurn = unitsProducedThisTurn;
    }

    public void setUnitsSoldThisTurn(int unitsSoldThisTurn) {
        this.unitsSoldThisTurn = unitsSoldThisTurn;
    }


    // Lifecycle methods
    public void launch() {
        this.isLaunched = true;
        System.out.println("Product '" + name + "' launched!");
        // Additional logic like starting sales, adding to market simulation etc.
    }

    public void terminate() {
        this.isLaunched = false;
        System.out.println("Product '" + name + "' terminated.");
        // Logic to remove from sales, stop market simulation etc.
    }

    // Example: Method to simulate selling units
    public double sellUnits(int unitsToSell) {
        if (currentSupply >= unitsToSell) {
            currentSupply -= unitsToSell;
            this.unitsSoldThisTurn += unitsToSell; // Track units sold for this turn
            double revenue = unitsToSell * price;
            System.out.println("Sold " + unitsToSell + " units of " + name + " for $" + String.format("%.2f", revenue));
            return revenue;
        } else {
            // Only sell what's available
            double revenue = currentSupply * price;
            this.unitsSoldThisTurn += currentSupply; // Track units sold for this turn
            System.out.println("Sold " + currentSupply + " units of " + name + " for $" + String.format("%.2f", revenue) + " (partial sale due to low supply).");
            currentSupply = 0; // All supply sold
            return revenue;
        }
    }

    // --- NEW: Method to reset per-turn counters ---
    public void resetTurnCounters() {
        this.unitsProducedThisTurn = 0;
        this.unitsSoldThisTurn = 0;
    }
    // --- END NEW ---
}