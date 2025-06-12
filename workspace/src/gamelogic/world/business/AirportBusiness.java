package gamelogic.world.business;

import gamelogic.Main;
import gamelogic.player.Player;

public class AirportBusiness extends Business {
    private int passengerCapacity; // Max passengers per turn
    private double maintenanceCostPerTurn;
    private int passengersThisTurn;


    public AirportBusiness(String name, double initialCost, Player owner, int capacity, double maintenanceCost) {
        super(name, "Airport", initialCost, owner);
        this.passengerCapacity = capacity;
        this.maintenanceCostPerTurn = maintenanceCost;
        this.currentPrice = 100.0; // Initial suggested ticket price
        this.passengersThisTurn = 0;
    }

    @Override
    public void updateDemand(double economyFactor) {
        // Monopoly demand curve: Demand is high but decreases significantly with price
        // economyFactor could be influenced by population, GDP, etc.
        double baseDemand = 1000 * economyFactor; // High base demand for a monopoly airport
        double demandBasedOnPrice = baseDemand - (currentPrice * 5); // Example: demand drops significantly with price

        // Ensure demand is not negative and capped by capacity
        this.currentDemand = Math.max(0, demandBasedOnPrice);
        // Actual passengers will be capped by passengerCapacity and demand
    }

    @Override
    public double calculateRevenue() {
        // Revenue based on passengers served and ticket price
        return passengersThisTurn * currentPrice;
    }

    @Override
    public double calculateExpenses() {
        // Expenses are fixed maintenance cost + variable costs (e.g., per passenger)
        double variableCostPerPassenger = 5; // Example
        return maintenanceCostPerTurn + (passengersThisTurn * variableCostPerPassenger);
    }

    @Override
    public void performTurnUpdate() {
        // 1. Update demand based on current economy (Main.economy.getDemandFactor())
        double economyFactor = 1.0; // Placeholder
        if (Main.getInstance() != null && Main.getInstance().economy != null) {
            // Airport demand might be less sensitive to specific product type factors
            economyFactor = Main.getInstance().economy.getOverallEconomyFactor(); // Assuming an overall factor
        }
        updateDemand(economyFactor);

        // 2. Determine passengers served: Capped by demand and capacity
        this.passengersThisTurn = (int) Math.min(passengerCapacity, currentDemand);

        // 3. Update player's purse based on profit
        double profit = getProfit();
        owner.changePurse(profit);

        // 4. Report to TurnReportGUI
        Main.getInstance().turnReportGUI.addEvent(
                getName() + " (Airport): Served " + passengersThisTurn +
                        " passengers at $" + String.format("%.2f", currentPrice) + " per ticket. Profit: $" + String.format("%.2f", profit)
        );
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }
}