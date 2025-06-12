package gamelogic.world;

import java.util.ArrayList;

public class Firm {

    private String name;
    private String type;
    private int worker;
    private ArrayList<Product> products = new ArrayList<>();
    private double moneyMade;

    public Firm (String name, String type, ArrayList<Product> products) {
        this.name = name;
        this.type = type;
        this.products = products;
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void removeProduct(Product p) {
        products.remove(p);
    }

    public void timeLapse() {

    }
}
