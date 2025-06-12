package gamelogic.world.business;

public enum ProductType {
    FOOD("Food", 100, 500, 10), // Base cost, base demand, sensitivity to price
    CLOTHING("Clothing", 200, 300, 15),
    ELECTRONICS("Electronics", 500, 100, 20);

    private final String name;
    private final double baseProductionCost;
    private final double baseDemand;
    private final double priceSensitivity; // How much demand changes with price

    ProductType(String name, double baseProductionCost, double baseDemand, double priceSensitivity) {
        this.name = name;
        this.baseProductionCost = baseProductionCost;
        this.baseDemand = baseDemand;
        this.priceSensitivity = priceSensitivity;
    }

    public String getName() {
        return name;
    }

    public double getBaseProductionCost() {
        return baseProductionCost;
    }

    public double getBaseDemand() {
        return baseDemand;
    }

    public double getPriceSensitivity() {
        return priceSensitivity;
    }
}