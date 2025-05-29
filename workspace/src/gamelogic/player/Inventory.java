package gamelogic.player;

import java.util.List;
import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> items;
    private int maxSlots;

    public Inventory(int maxSlots) {
        this.items = new ArrayList<>();
        this.maxSlots = maxSlots;
    }

    public boolean addItem(Item item) {
        if (items.size() >= maxSlots) {
            System.out.println("Inventory is full! Cannot add " + item.getName());
            return false;
        }
        items.add(item);
        return true;
    }

    public boolean removeItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public void showInventory() {
        if (items.isEmpty()) {
            System.out.println("Empty.");
        } else {
            for (Item item : items) {
                System.out.println(item);
            }
        }
    }

    public Item getItem(String name) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getUsedSlots() {
        return items.size();
    }

    public int getRemainingSlots() {
        return maxSlots - items.size();
    }

    public int getMaxSlots() {
        return maxSlots;
    }
}