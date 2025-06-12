package gamelogic.world.business;

import gamelogic.Main;
import gamelogic.player.Player;

public abstract class Business {
    protected String name;
    protected String type; // e.g., "Production Market", "Airport"
    protected double initialCost;
    protected Player owner; // The player who owns this business

    protected double currentPrice; // Price player sets for their product/service
    protected double currentDemand; // Calculated demand for the product/service

    public Business(String name, String type, double initialCost, Player owner) {
        this.name = name;
        this.type = type;
        this.initialCost = initialCost;
        this.owner = owner;
        this.currentPrice = 0.0; // Default or initial price
        this.currentDemand = 0.0; // Initial demand
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getInitialCost() {
        return initialCost;
    }

    public Player getOwner() {
        return owner;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentDemand() {
        return currentDemand;
    }

    // Abstract methods to be implemented by subclasses
    public abstract void updateDemand(double economyFactor); // economyFactor can be influenced by GDP, population, etc.
    public abstract double calculateRevenue();
    public abstract double calculateExpenses();
    public abstract void performTurnUpdate(); // Method to encapsulate all turn-based logic

    // Utility method to get business profits (Revenue - Expenses)
    public double getProfit() {
        return calculateRevenue() - calculateExpenses();
    }
}