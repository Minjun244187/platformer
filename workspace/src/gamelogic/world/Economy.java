package gamelogic.world;

import gamelogic.Main; // To potentially access other game state like player decisions, government policies
import gamelogic.world.business.ProductType; // Import ProductType for type-specific demand factors

public class Economy {
    private double currentGDP;
    private int currentPopulation;
    private double currentInterestRate;
    private double currentInflationRate;

    // Base values and growth rates
    private static final double BASE_GDP = 100000.0;
    private static final int BASE_POPULATION = 10000;
    private static final double BASE_INTEREST_RATE = 0.03; // 3%
    private static final double BASE_INFLATION_RATE = 0.02; // 2%

    public Economy() {
        this.currentGDP = BASE_GDP;
        this.currentPopulation = BASE_POPULATION;
        this.currentInterestRate = BASE_INTEREST_RATE;
        this.currentInflationRate = BASE_INFLATION_RATE;
    }

    public void updateEconomy() {
        // Simple linear growth for now, can be expanded later based on game events/policies
        double gdpGrowthFactor = 1.01; // 1% growth
        double populationGrowthFactor = 1.005; // 0.5% growth

        currentGDP *= gdpGrowthFactor;
        currentPopulation = (int) (currentPopulation * populationGrowthFactor);

        // Interest rate and inflation can fluctuate or be influenced by player/government actions later
        // For now, let's keep them simple or slightly fluctuate
        currentInterestRate = BASE_INTEREST_RATE + (Math.random() - 0.5) * 0.01; // Fluctuate by +/- 0.5%
        currentInflationRate = BASE_INFLATION_RATE + (Math.random() - 0.5) * 0.005; // Fluctuate by +/- 0.25%

        // Ensure values don't go too low
        currentGDP = Math.max(10000.0, currentGDP);
        currentPopulation = Math.max(100, currentPopulation);
        currentInterestRate = Math.max(0.001, currentInterestRate); // Min 0.1%
        currentInflationRate = Math.max(0.0, currentInflationRate); // Min 0%

        // Update historical data in Main (if needed for graphs)
        // This is now done in Main.nextTurn() to ensure consistency
    }

    // Provide an overall economy factor for businesses
    public double getOverallEconomyFactor() {
        // Example: Economy factor influenced by GDP and population
        return (currentGDP / BASE_GDP) * (currentPopulation / (double)BASE_POPULATION);
    }

    // Provide a more specific demand factor for ProductionMarketBusiness products
    public double getDemandFactor(ProductType productType) {
        // Example: Population is a general factor for all goods.
        // Specific goods might be more or less sensitive to GDP.
        double factor = (currentPopulation / (double)BASE_POPULATION);

        // For more advanced logic, different product types could be sensitive to different economic aspects.
        // e.g., Electronics might be more sensitive to GDP, food less so.
        if (productType == ProductType.ELECTRONICS) {
            factor *= (currentGDP / BASE_GDP * 0.8 + 0.2); // Electronics scale more with GDP
        }
        return factor;
    }


    // Getters for economic indicators
    public double getCurrentGDP() {
        return currentGDP;
    }

    public int getCurrentPopulation() {
        return currentPopulation;
    }

    public double getInterestRate() {
        return currentInterestRate;
    }

    public double getInflationRate() {
        return currentInflationRate;
    }
}