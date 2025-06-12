package gamelogic.world;

import gamelogic.Event;
import gamelogic.Main;
import gamelogic.audio.SoundManager;

import java.util.ArrayList;
import java.util.List;

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
    private double interestRate; //in percentage
    public List<Double> historicalInterestRates = new ArrayList<>();
    public List<Double> historicalTaxRates = new ArrayList<>();
    public List<Double> historicalSpending = new ArrayList<>();
    public List<Double> historicalGrowthRate = new ArrayList<>();
    public List<Integer> historicalPopRate = new ArrayList<>();
    public List<Double> historicalRGDP = new ArrayList<>();
    private events event;
    private boolean inWar = false;
    public List<Event> countryNews;
    public int difficulty = 0;
    private int time = 0;
    public boolean bankrupted;
    private SoundManager sm = new SoundManager();
    private int frontLine = 0;

    public country(String name, int population, double GDP, double baseGrowthRate, double interestRate) {
        this.name = name;
        RGDP = GDP;
        this.population = population;
        growthRate = baseGrowthRate;
        this.interestRate = interestRate;
        sm.loadClip("war", "music/warmarch.wav");
        sm.loadClip("notonestepback", "music/dietotenerwachen.wav");
        sm.loadClip("badomen", "music/badomen.wav");
        event = new events();
        event.resetEvents();
        countryNews = new ArrayList<>();
        historicalGrowthRate.add(growthRate);
        historicalSpending.add(extraSpending);
        historicalTaxRates.add(taxRate);
        historicalInterestRates.add(interestRate);
        historicalPopRate.add(population);
        historicalRGDP.add(RGDP);


    }

    public List<Event> getCountryNews() { return countryNews; }

    public void setTime(int time) {
        this.time = time;
    }

    public double getIR(){ return interestRate; }

    public int getTime(){ return time; }

    public void changeGrowthRate(double amount) {
        growthRate += amount;
        //historicalGrowthRate.add(growthRate);
    }

    public boolean isInWar() { return inWar; }

    public double getGDP() {
        return RGDP;
    }

    public void growEconomy() {
        RGDP += RGDP * (growthRate / 100);
        if (taxRate > growthRate * 3) {
            decreaseTax();
        } else if (taxRate < growthRate * 2) {
            increaseTax();
        }
    }

    public int getPop() {
        return population;
    }

    public double getInterestRate() { return interestRate; }


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
            increaseTax();
        } else {
            defenseSpending = 0;
            extraSpending = 0;
        }

    }

    public int getFrontLine() { return frontLine; }

    public void changeSpending(double amount) {

        extraSpending += amount;
        //historicalSpending.add(extraSpending);
    }

    public void changeTaxes(double amount) {

        taxRate += amount;
        //historicalTaxRates.add(taxRate);
    }

    public void changeIR(double amount) {


        interestRate += amount;
        if (interestRate < 0) {
            interestRate = 0;
        }
        //historicalInterestRates.add(interestRate);

    }

    public void changePop(int amount) {

        population += amount;
        //historicalPopRate.add(population);
    }
    public void initialNews() {
        countryNews.add(new Event("welcome"));
    }
    public void hardNews() {
        countryNews.add(new Event("aboutWar"));
        countryNews.add(new Event("welcome"));
    }

    //functions when the player sleeps
    public void timeLapse() {
        time += 1;
        countryNews.clear();
        if (difficulty == 1 && time == 3 && !isInWar()) {
            startWar();
        } else if (difficulty == 1 && !isInWar()) {
            switch (((int) (Math.random() * 5))) {
                case 2 -> startWar();

                case 3 -> increaseTax();


                case 4 -> increaseTax();
            }
        }
        growEconomy();
        collectTaxes();
        splitSpending();
        while(countryNews.size() < 3) {
            countryNews.add(new Event(eventGetter()));
        }
        historicalGrowthRate.add(growthRate);
        historicalSpending.add(extraSpending);
        historicalTaxRates.add(taxRate);
        historicalInterestRates.add(interestRate);
        historicalPopRate.add(population);



    }

    public void increaseTax() {
        double randomChangeRate = (double) (Math.random() * 4);
        changeTaxes(randomChangeRate);
        countryNews.add(new Event("increaseTax"));
    }

    public void decreaseTax() {
        double randomChangeRate = (double) (Math.random() * 4);
        changeTaxes(-randomChangeRate);
        countryNews.add(new Event("decreaseTax"));
    }

    public void startWar() {
        countryNews.add(new Event("warDeclare"));
        frontLine = 0;
        inWar = true;
    }

    //will return a random event
    public String eventGetter() {

        String currentEvent = event.getRandomEvent();
        if (growthRate > 5) {
            switch((int)(Math.random() * 3)) {
                case 0 -> currentEvent = "decreaseSpending";
                case 1 -> currentEvent = "increaseTax";
                case 2 -> currentEvent = "decreaseIR";
            }

        } else if (growthRate < 0) {
            switch((int)(Math.random() * 3)) {
                case 0 -> currentEvent = "increaseSpending";
                case 1 -> currentEvent = "decreaseTax";
                case 2 -> currentEvent = "increaseIR";
            }
        }

        if (inWar && countryNews.size() <= 2) {
            currentEvent = event.getRandomWarEvent();
        }

        double randomChangeRate = (double) (Math.random() * 3);
        if (currentEvent.equals("increaseSpending")) {
            changeSpending((randomChangeRate * spending) / 100);

        } else if (currentEvent.equals("decreaseSpending")) {
            changeSpending(-(randomChangeRate * spending) / 100);

        } else if (currentEvent.equals("increaseTax")) {
            changeTaxes(randomChangeRate);

        } else if (currentEvent.equals("decreaseTax")) {
            changeTaxes(-randomChangeRate);

        } else if (currentEvent.equals("increaseIR")) {
            changeIR(randomChangeRate);

        } else if (currentEvent.equals("decreaseIR")) {
            changeIR(-randomChangeRate);

        } else if (currentEvent.equals("bankrupt")) {
            bankrupted = true;
            Main.bankrupted = true;
            changeGrowthRate(-randomChangeRate * 2);

        } else if (currentEvent.equals("warDeclare")) {
            inWar = true;
            frontLine = 0;

        } else if (currentEvent.equals("invasion")) {
            inWar = true;
            frontLine = -1;

        } else if (currentEvent.equals("bombing")) {
            changePop(-(int) (population * randomChangeRate / 180));

        } else if (currentEvent.equals("battle")) { //trash

        } else if (currentEvent.equals("weaponFirm")) {
            changeGrowthRate(-randomChangeRate * 2);

        } else if (currentEvent.equals("forward")) {
            frontLine++;
            changePop(-(int) (population * randomChangeRate / 200));
            if (frontLine >= 4) {
                currentEvent = "victory";
                inWar = false;
            }

        } else if (currentEvent.equals("backward")) {
            frontLine--;
            changeGrowthRate(-randomChangeRate);
            changePop(-(int) (population * randomChangeRate / 220));
            if (frontLine < -4) {
                Main.startFinalBattle();
                currentEvent = "final_battle";
            }

        } else if (currentEvent.equals("surrender")) {
            inWar = false;
            changeGrowthRate(-randomChangeRate * 2.5);
            govtMoney -= govtMoney * randomChangeRate / 75;

        } else if (currentEvent.equals("victory")) {
            inWar = false;
            changeGrowthRate(randomChangeRate * 2.5);

        } else if (currentEvent.equals("healthyBurger")) {
            changeGrowthRate(randomChangeRate);

        } else if (currentEvent.equals("boycott")) {
            changeGrowthRate(-randomChangeRate);

        } else if (currentEvent.equals("forestBurn")) {
            Main.extraProductionCost += 20;
            changeGrowthRate(-randomChangeRate);

        } else if (currentEvent.equals("iceMelt")) {
            Main.extraProductionCost += 10;
            changeGrowthRate(-randomChangeRate);

        } else if (currentEvent.equals("goldRush")) {
            Main.goldCost += Main.goldCost * 2 * (randomChangeRate/100);
            changeGrowthRate(+randomChangeRate);

        } else if (currentEvent.equals("construction")) {
            changePop((int) (population * randomChangeRate / 220));
            changeGrowthRate(randomChangeRate * 2.2);

        }

        return currentEvent;

    }


    public String getName() {
        return name;
    }

    public double getTax() {
        return taxRate;
    }

    public void setDifficulty(int selectedDifficulty) {
        difficulty = selectedDifficulty;
    }

    public double getSpending() {
        return spending;
    }

    public void setPop(int population) {
        this.population = population;
    }

    public void setFL(int frontLine) {
        this.frontLine = frontLine;
    }

    public void setInWar(boolean isInWar) {
        this.inWar = isInWar;
    }

    public void setCountryNews(List<Event> countrynews) {
        this.countryNews = countrynews;
    }

    public void setTax(double taxRate) {
        this.taxRate = taxRate;
    }

    public void setSpending(double spending) {
        this.spending = spending;
    }
}
