package gamelogic.player;

import gamelogic.level.Level;

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

    public boolean isFull() {
        return getItems().size() >= getMaxSlots();
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public int getUsedSlots() {
        return items.size();
    }

    public int getSlotOf(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                return i;
            }
        }
        return -1;
    }

    public int getRemainingSlots() {
        return maxSlots - items.size();
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    public void dropSelectedItem(Level level, int hoveredSlot, int x, int y) {
        if (hoveredSlot < 0 || hoveredSlot >= items.size()) return;
        Item item = items.get(hoveredSlot);
        if (item != null) {
            level.addDroppedItem(item, x, y);
            items.remove(hoveredSlot);
        }
    }

    public boolean hasItem(Item item) {
        return getItem(item.getName()) != null;
    }

    public void setMaxSlots(int i) {
        maxSlots = i;
    }
}