package gamelogic.player;

public class Item {
    private String name;
    private String type; //types include: food, accesory, weapon, and null
    private int value;   // rarity of the item (1~4)

    public Item(String name, String type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getType() { return type; }
    public int getValue() { return value; }

    @Override
    public String toString() {
        return name + " (" + type + "): " + value;
    }
}
