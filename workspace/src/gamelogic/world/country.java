package gamelogic.world;

public class country {
    private String name;
    private double govtMoney;
    private int population;
    private double spending;
    private double defenseSpending;
    private double RGDP; //in dollars
    private double growthRate; //in percentage
    private double taxRate = 10; //in percentage
    private double extraSpending = 0; //in dollars

    public country(String name, int population, double GDP, double baseGrowthRate) {
        this.name = name;
        RGDP = GDP;
        this.population = population;
        growthRate = baseGrowthRate;


    }

    public double getGDP() {
        return RGDP;
    }

    public void growEconomy() {
        RGDP += RGDP * growthRate;
    }

    public double getPop() {
        return population;
    }


    public void collectTaxes() {
        govtMoney += RGDP * (taxRate / 100);
    }

    public void splitSpending() {
        if (govtMoney > 0) {
            spending = govtMoney * 0.7;
            govtMoney -= govtMoney * 0.7;
            defenseSpending = govtMoney * 0.2;
            govtMoney -= govtMoney * 0.2;
            govtMoney -= extraSpending;

        } else if (govtMoney > -1000000000000.0) {

        } else {
            defenseSpending = 0;
            extraSpending = 0;
        }

    }

    public void changeSpending(double amount) {
        extraSpending += amount;
    }

    public void changeTaxes(double amount) {
        taxRate += amount;
    }
    

    
}
