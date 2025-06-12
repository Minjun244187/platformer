package gamelogic;

import java.awt.image.BufferedImage; // Required for images
// You might need an ImageLoader class or similar to load BufferedImages from path

public class Event {
    private String headline;
    private String body; // Optional: for a short article snippet
    private BufferedImage image;
    private String imagePath;// The actual loaded image
    private String date;
    private String id;// Date of the news event, e.g., "June, 2025"

    public Event(String headline, String body, String imagePath, String date) {
        this.headline = headline;
        this.body = body;
        this.imagePath = imagePath;
        this.date = date;
        this.image = null; // Image will be loaded later, or you can load it here if you have an ImageLoader
    }

    public Event(String id) {
        this.id = id;
        if (id == "welcome") {
            this.headline = "A New Year Starts";
            this.body = "The President Gives speech of the new year 2002, going to start strong...";
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "aboutWar") {
            this.headline = "Intense Situation";
            this.body = "The Country of Rodmania is at risk of engaging in war...";
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "increaseSpending") {
            this.headline = "Increased Spending";
            this.body = "The Council passed a new law increasing government spending! The new amount of spending is $" + Main.getInstance().getCountryList().get(0).getSpending();
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "decreaseSpending") {
            this.headline = "Decreased Spending";
            this.body = "The Council passed a decrease in government spending! The new amount of spending is $" + Main.getInstance().getCountryList().get(0).getSpending();
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "increaseTax") {
            this.headline = "Increased Tax";
            this.body = "Breaking News: The new amount of tax rate is " + String.format("%.2f", Main.getInstance().getTaxRate()) + "%";
            this.imagePath = "gfx/news/news_rise.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "decreaseTax") {
            this.headline = "Tax Cuts";
            this.body = "Breaking News: Tax Rate has decreased! The new amount of tax rate is " + String.format("%.2f", Main.getInstance().getTaxRate()) + "%";
            this.imagePath = "gfx/news/news_fall.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "increaseIR") {
            this.headline = "Increased Interest Rates";
            this.body = "The Bank decided to increase interest rates. The new amount of interest rate is " + String.format("%.2f", Main.getInstance().getCountryList().get(0).getIR()) + "%";
            this.imagePath = "gfx/news/news_rise.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "decreaseIR") {
            this.headline = "Decreased Interest Rates";
            this.body = "The Bank decided to decrease interest rates. The new amount of interest rate is " + String.format("%.2f", Main.getInstance().getCountryList().get(0).getIR()) + "%";
            this.imagePath = "gfx/news/news_fall.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "healthyBurger") {
            this.headline = "Burgers are healthy";
            this.body = "A recent study shows that burgers are healthy for people. Demand for burgers are expected to increase.";
            this.imagePath = "gfx/items/item_burger.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "boycott") {
            this.headline = "People start boycotting";
            this.body = "People started boycotting against other countries. Imports decrease, our economy is slowing down.";
            this.imagePath = "gfx/news/news_boycott.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "forestBurn") {
            this.headline = "Forest Fire Strikes Rodmania";
            this.body = "A fire in forest swipes the environment! Our nature is no longer with us.";
            this.imagePath = "gfx/news/news_fire.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "iceMelt") {
            this.headline = "Climate Change leads to melting of ice";
            this.body = "More ice is melting as the pollution increases. We must protect the planet.";
            this.imagePath = "gfx/news/news_ice.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "goldRush") {
            this.headline = "Gold Price Strikes High";
            this.body = "The unstable economy lead to a higher demand of Gold! Get your gold before others steal it!";
            this.imagePath = "gfx/items/item_gold.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "construction") {
            this.headline = "New Towns enter under government subsidies";
            this.body = "Newly built towns are bringing more people into the country! Rodmania is rapidly growing!";
            this.imagePath = "gfx/news/news_town.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "warDeclare") {
            this.headline = "RODMANIA IN WAR WITH COLLEGE BOARD";
            this.body = "Rodmania will expand its territories further towards the world. Glory to Rodmania!";
            this.imagePath = "gfx/news/news_soldiers.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "invasion") {
            this.headline = "COLLEGE BOARD INVADES RODMANIA";
            this.body = "College Board Empire crossed the borders and invaded Rodmania. However, we will fight until the end. There is no end of Rodmania.";
            this.imagePath = "gfx/news/news_battle.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "bankrupt") {
            this.headline = "Bank of Rodmania gone BANKRUPT";
            this.body = "Too many bankruns caused the bank to go bankrupt. There will be further clarifications soon.";
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "bombing") {
            this.headline = "COLLEGE BOARD BOMBS RODMANIA";
            this.body = "The Bomber Planes of College Board has entered Rodmania. Please seek shelter for safety.";
            this.imagePath = "gfx/news/news_battle.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "surrender") {
            this.headline = "RODMANIA SURRENDERS";
            this.body = "'To prevent further casualties, country of Rodmania surrenders. We will undertake all the responsibilities of war.'";
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "weaponFirm") {
            this.headline = "ORDER: WEAPONIZE ALL FIRMS";
            this.body = "'Our Country has turned to the state of total war. All firms should only produce weapons. Glory to Rodmania.'";
            this.imagePath = "gfx/news/news_president.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "forward") {
            this.headline = "RODMANIA DEFEATS COLLEGE BOARD ARMY";
            this.body = "The powerful Rodmanian army has suppressed the enemy, leading to our victory. Glory to Rodmania.";
            this.imagePath = "gfx/news/news_soldiers.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "backward") {
            this.headline = "COLLEGE BOARD ARMY MASSACRES RODMANIA";
            this.body = "The cruel College Board had massacred our men, leading to casualties. Those monsters are pushing " +
                    "towards the frontlines. However, we must not step back. Glory to Rodmania.";
            this.imagePath = "gfx/news/news_battle.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "victory") {
            this.headline = "COLLEGE BOARD SURRENDERS";
            this.body = "The cruel College Board had surrendered, and will take all war responsibilities. We have successfully defended our land. Victory is ours. Long live Rodmania!";
            this.imagePath = "gfx/news/news_soldiers.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        } else if (id == "final_battle") {
            this.headline = "COLLEGE BOARD ARMY STRIKES FORWARD";
            this.body = "The cruel College Board had just arrived at our town, trying to claim the territory. However, we are not dead yet. Rodmania will not fall on the hands of College Board. For your future, For justice, fight until the end.  Long live Rodmania!";
            this.imagePath = "gfx/news/news_soldiers.png";
            this.date = "2022";
            this.image = GameResources.newsPresident;
        }
    }

    public String getID() {
        return id;
    }

    public String getHeadline() {
        return headline;
    }

    public String getBody() {
        return body;
    }

    public String getImagePath() {
        return imagePath;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }
}
