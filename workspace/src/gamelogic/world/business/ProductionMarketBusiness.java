package gamelogic.world.business;

import gamelogic.Main;
import gamelogic.player.Player;
import gamelogic.world.Product; // Import the Product class
import java.util.ArrayList;
import java.util.List;

public class ProductionMarketBusiness extends Business {
    // This business now manages a list of individual Product instances,
    // rather than a single ProductType and global capacity.
    private List<Product> products;

    // These fields will now be aggregates calculated from the 'products' list each turn.
    private int totalUnitsProducedThisTurn;
    private int totalUnitsSoldThisTurn;
    private double totalRevenueThisTurn;
    private double totalExpensesThisTurn;

    public ProductionMarketBusiness(String name, double initialCost, Player owner) {
        super(name, "Production Market", initialCost, owner);
        this.products = new ArrayList<>();
        // currentPrice for the business itself is no longer directly set or used
        // as actual pricing is per Product. We'll set it to 0.0 or average for reports.
        this.currentPrice = 0.0;
        this.totalUnitsProducedThisTurn = 0;
        this.totalUnitsSoldThisTurn = 0;
        this.totalRevenueThisTurn = 0;
        this.totalExpensesThisTurn = 0;
    }

    // --- Product Management Methods ---
    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        if (!this.products.contains(product)) {
            this.products.add(product);
            Main.getInstance().turnReportGUI.addEvent("Added new product: " + product.getName() + " to " + getName());
            // Optionally, set the owning business for the product here
            // product.setOwningBusiness(this);
        }
    }

    public void removeProduct(Product product) {
        this.products.remove(product);
        Main.getInstance().turnReportGUI.addEvent("Removed product: " + product.getName() + " from " + getName());
        // Optionally, clear the owning business for the product here
        // product.setOwningBusiness(null);
    }

    // --- Economic Simulation Methods (Refactored) ---

    @Override
    public void updateDemand(double economyFactor) {
        // Demand calculation now happens per individual Product.
        // This method for the business will iterate and (re)calculate demand for each product,
        // but the 'currentDemand' field of the Business itself might not be directly relevant
        // or would represent an aggregate/average. For simplicity, we'll let `performTurnUpdate`
        // handle the actual demand calculation per product.
        this.currentDemand = 0; // Reset for potential aggregation if needed elsewhere
    }

    @Override
    public double calculateRevenue() {
        // Returns the total revenue aggregated from all products sold this turn.
        // This relies on `performTurnUpdate` having run and set `totalRevenueThisTurn`.
        return this.totalRevenueThisTurn;
    }

    @Override
    public double calculateExpenses() {
        // Returns the total expenses aggregated from all products produced this turn
        // plus fixed costs. This relies on `performTurnUpdate` having run.
        return this.totalExpensesThisTurn;
    }

    @Override
    public void performTurnUpdate() {
        // Reset aggregates for this business for the current turn
        this.totalUnitsProducedThisTurn = 0;
        this.totalUnitsSoldThisTurn = 0;
        this.totalRevenueThisTurn = 0;
        this.totalExpensesThisTurn = 0;
        double fixedCost = 500; // Example fixed cost for a production market
        this.totalExpensesThisTurn += fixedCost; // Add fixed costs upfront

        Main.getInstance().turnReportGUI.addEvent(getName() + " (Overall Business Update):");

        for (Product p : products) {
            p.resetTurnCounters(); // Clear previous turn's production/sales for this product

            // 1. Calculate demand for this specific product
            double economyFactor = 1.0; // Placeholder, fetch from Main.getInstance().economy if available
            if (Main.getInstance() != null && Main.getInstance().economy != null) {
                economyFactor = Main.getInstance().economy.getDemandFactor(p.getType());
            }

            double effectiveBaseDemand = p.getType().getBaseDemand() * economyFactor;
            double productDemand = Math.max(0, effectiveBaseDemand - (p.getCurrentPrice() - p.getType().getBaseProductionCost()) * p.getType().getPriceSensitivity());

            // 2. Determine units to produce for this product
            // Simplified production: each product can produce a certain amount each turn.
            // This could be based on its type or a dynamic factor.
            int productMaxProductionCapacityPerTurn = 100; // Example: Max 100 units per product per turn

            int unitsToProduceForProduct = (int) Math.min(productMaxProductionCapacityPerTurn, productDemand);
            p.setCurrentSupply(p.getCurrentSupply() + unitsToProduceForProduct); // Add newly produced units to supply
            p.setUnitsProducedThisTurn(unitsToProduceForProduct); // Record for product

            // 3. Simulate sales for this product
            double revenueForProduct = p.sellUnits((int) productDemand); // Product sells units up to demand/supply
            // Note: sellUnits already updates p.getUnitsSoldThisTurn()

            // 4. Calculate expenses for this product's production
            double expensesForProduct = unitsToProduceForProduct * p.getProductionCostPerUnit();

            // 5. Aggregate for the business
            this.totalUnitsProducedThisTurn += unitsToProduceForProduct;
            this.totalUnitsSoldThisTurn += p.getUnitsSoldThisTurn(); // Get actual sold from product
            this.totalRevenueThisTurn += revenueForProduct;
            this.totalExpensesThisTurn += expensesForProduct; // Add production costs to business total

            Main.getInstance().turnReportGUI.addEvent(
                    " - " + p.getName() + " (" + p.getType().getName() + "): Produced " + unitsToProduceForProduct +
                            " units, sold " + p.getUnitsSoldThisTurn() + " units at $" + String.format("%.2f", p.getCurrentPrice()) + " each. Remaining Supply: " + p.getCurrentSupply()
            );
        }

        // Calculate total profit for the business after all products are processed and fixed costs are added
        double netProfit = getProfit(); // Calls calculateRevenue() and calculateExpenses()

        // 6. Update player's purse
        owner.changePurse(netProfit);

        // 7. Report overall business results
        Main.getInstance().turnReportGUI.addEvent(
                getName() + " (Summary): Total Produced " + totalUnitsProducedThisTurn +
                        " units, Total Sold " + totalUnitsSoldThisTurn + " units. Net Profit: $" + String.format("%.2f", netProfit)
        );
    }
}