package gamelogic.player;

import gamelogic.Main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Item implements Serializable {
    private String name;
    public enum ItemType {
        FOOD, ACCESORY, MATERIAL, GENERIC, WEAPON // Add more types as needed
    }
    //types include: food, accesory, weapon, and null
    private int rarity;   // rarity of the item (1~4)
    private String desc;
    private String desc2;
    private BufferedImage image;
    private double cost;
    private int width;
    private int height;
    private double baseCost;
    private int x;
    private int y;
    private ItemType type;
    private int healthRestore;       // For FOOD type
    private int satisfactionBoost;   // For FOOD type
    private int luckBoost;
    private int intelliBoost;      // For FOOD type


    public Item(String name, int rarity, String desc, String desc2, double cost, BufferedImage image, int x, int y, int width, int height, int intelliBoost, int luckBoost) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.desc = desc;
        this.desc2 = desc2;
        this.cost = cost;
        this.baseCost = cost;
        this.width = width;
        this.height = height;
        this.image = image;
        this.x = x;
        this.y = y;
        this.type = ItemType.GENERIC; // Default type
        this.healthRestore = 0;
        this.satisfactionBoost = 0;
        this.intelliBoost = intelliBoost;
        this.luckBoost = luckBoost;
    }

    public Item(String name, String desc, String desc2, int rarity, double cost, BufferedImage image,
                int x, int y, int width, int height,
                ItemType type, int healthRestore, int satisfactionBoost, int intelliBoost, int luckBoost) {
        this(name, rarity, desc, desc2, cost, image, x, y, width, height, intelliBoost, luckBoost); // Call existing constructor
        this.type = type;
        this.healthRestore = healthRestore;
        this.satisfactionBoost = satisfactionBoost;
    }

    // Getters and setters
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public int getHealthRestore() {
        return healthRestore;
    }

    public int getSatisfactionBoost() {
        return satisfactionBoost;
    }

    public int getLuckBoost() {
        return luckBoost;
    }
    public int getIntelliBoost() {
        return intelliBoost;
    }
    public String getRarity() {
        if (rarity == 1) {
            return ("COMMON");
        } else if (rarity == 2) {
            return ("RARE");
        } else if (rarity == 3) {
            return ("EPIC");
        } else if (rarity == 4) {
            return ("LEGENDARY");
        } else {
            return "UNKNOWN";
        }
    }
    public Color getRarityColor() {
        if (rarity == 1) {
            return Color.WHITE;
        } else if (rarity == 2) {
            return Color.GREEN;
        } else if (rarity == 3) {
            return Color.MAGENTA;
        } else if (rarity == 4) {
            return Color.ORANGE;
        } else {
            return Color.WHITE;
        }
    }
    public String getDesc() { return desc; }

    @Override
    public String toString() {
        return name + " (" + type + "): " + rarity;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public double getCost() {
        if (cost == baseCost) {
            cost += (cost * Main.getTaxRate()) / 100;
        }
        return cost;
    }

    public Item copy() {
        Item newItem = new Item(this.name, this.desc, this.desc2, this.rarity, this.cost, this.image,
                this.x, this.y, this.width, this.height,
                this.type, this.healthRestore, this.satisfactionBoost, this.intelliBoost, this.luckBoost);
        if (this.image instanceof BufferedImage) {
        	BufferedImage bufImg = this.image;
            BufferedImage clone = new BufferedImage(bufImg.getWidth(), bufImg.getHeight(), bufImg.getType());
            Graphics2D g2d = clone.createGraphics();
            g2d.drawImage(bufImg, 0, 0, null);
            g2d.dispose();
            newItem.setImage(clone);
        } else {
            newItem.setImage(this.image); // fallback
        }
        return newItem;
    }

    public String getDesc2() {
        return desc2;
    }
}
