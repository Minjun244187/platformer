package gamelogic.world;

import gameengine.input.KeyboardInputManager;
import gamelogic.Main;
import gamelogic.world.business.Business;
import gamelogic.world.business.ProductionMarketBusiness;
import gamelogic.world.business.ProductType;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductManagementPanel {

    private int panelX, panelY, panelWidth, panelHeight;
    private static final int PADDING = 20;
    private static final int CORNER_RADIUS = 15;

    private static final int INPUT_FIELD_WIDTH = 200;
    private static final int INPUT_FIELD_HEIGHT = 30;
    private static final int VERTICAL_SPACING_FORM = 60;
    private static final int BUTTON_SPACING = 10;

    private Rectangle newProductButtonRect;
    private Rectangle actionButtonRect;
    private Rectangle typeDropdownRect;

    private Rectangle priceIncrementButtonRect;
    private Rectangle priceDecrementButtonRect;
    private Rectangle supplyIncrementButtonRect;
    private Rectangle supplyDecrementButtonRect;

    private ArrayList<Product> launchedProducts = new ArrayList<>();
    private Product editingProduct;

    private ArrayList<ProductType> productTypes = new ArrayList<>(Arrays.asList(ProductType.values()));
    private ProductType selectedProductType = ProductType.FOOD;
    private boolean typeDropdownOpen = false;

    private static final int TRIANGLE_BUTTON_SIZE = 15;
    private static final double VALUE_STEP_PRICE = 1.0;
    private static final int VALUE_STEP_SUPPLY = 10;
    private static final int DEFAULT_INITIAL_SUPPLY_FOR_NEW_PRODUCT = 100;

    public ProductManagementPanel(int x, int y, int width, int height) {
        this.panelX = x;
        this.panelY = y;
        this.panelWidth = width;
        this.panelHeight = height;
        calculateLayout();

        clearFormForNewProduct();
    }

    private void calculateLayout() {
        newProductButtonRect = new Rectangle(panelX + PADDING, panelY + PADDING,
                INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT);

        int currentFormY = panelY + PADDING + INPUT_FIELD_HEIGHT + VERTICAL_SPACING_FORM / 2;

        typeDropdownRect = new Rectangle(panelX + PADDING + 150, currentFormY + 3 * VERTICAL_SPACING_FORM - (INPUT_FIELD_HEIGHT / 2) , INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT);

        actionButtonRect = new Rectangle(panelX + PADDING, panelY + panelHeight - PADDING - BUTTON_SPACING * 4,
                INPUT_FIELD_WIDTH + BUTTON_SPACING * 2 + TRIANGLE_BUTTON_SIZE * 2, BUTTON_SPACING * 3);

        priceIncrementButtonRect = new Rectangle(
                panelX + PADDING + 150 + INPUT_FIELD_WIDTH + 5,
                currentFormY + VERTICAL_SPACING_FORM,
                TRIANGLE_BUTTON_SIZE, TRIANGLE_BUTTON_SIZE
        );
        priceDecrementButtonRect = new Rectangle(
                priceIncrementButtonRect.x + TRIANGLE_BUTTON_SIZE + 5,
                currentFormY + VERTICAL_SPACING_FORM,
                TRIANGLE_BUTTON_SIZE, TRIANGLE_BUTTON_SIZE
        );

        supplyIncrementButtonRect = new Rectangle(
                panelX + PADDING + 150 + INPUT_FIELD_WIDTH + 5,
                currentFormY + 2 * VERTICAL_SPACING_FORM,
                TRIANGLE_BUTTON_SIZE, TRIANGLE_BUTTON_SIZE
        );
        supplyDecrementButtonRect = new Rectangle(
                supplyIncrementButtonRect.x + TRIANGLE_BUTTON_SIZE + 5,
                currentFormY + 2 * VERTICAL_SPACING_FORM,
                TRIANGLE_BUTTON_SIZE, TRIANGLE_BUTTON_SIZE
        );
    }

    public void setLaunchedProducts(List<Product> products) {
        this.launchedProducts.clear();
        if (products != null) {
            this.launchedProducts.addAll(products);
        }
        if (editingProduct == null || !this.launchedProducts.contains(editingProduct)) {
            if (!this.launchedProducts.isEmpty()) {
                editingProduct = this.launchedProducts.get(0);
                selectedProductType = editingProduct.getType();
            } else {
                clearFormForNewProduct();
            }
        }
    }

    public void render(Graphics2D g2d, int mouseX, int mouseY) {
        g2d.setColor(new Color(60, 60, 80, 240));
        g2d.fill(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, CORNER_RADIUS, CORNER_RADIUS));

        drawButton(g2d, newProductButtonRect, "New Product", new Color(50, 150, 50));

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));

        int currentLabelY = panelY + PADDING + INPUT_FIELD_HEIGHT + VERTICAL_SPACING_FORM / 2 + 20;
        int currentFieldY = panelY + PADDING + INPUT_FIELD_HEIGHT + VERTICAL_SPACING_FORM / 2 - (INPUT_FIELD_HEIGHT / 2);

        g2d.drawString("Name:", panelX + PADDING, currentLabelY);
        g2d.drawString("Price:", panelX + PADDING, currentLabelY + VERTICAL_SPACING_FORM);
        g2d.drawString("Supply:", panelX + PADDING, currentLabelY + 2 * VERTICAL_SPACING_FORM);
        g2d.drawString("Type:", panelX + PADDING, currentLabelY + 3 * VERTICAL_SPACING_FORM);

        Rectangle nameDisplayRect = new Rectangle(panelX + PADDING + 150, currentFieldY, INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(nameDisplayRect);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(nameDisplayRect);
        g2d.setColor(Color.WHITE);
        if (editingProduct != null) {
            g2d.drawString(editingProduct.getName(), nameDisplayRect.x + 5, nameDisplayRect.y + 20);
        } else {
            g2d.drawString("N/A", nameDisplayRect.x + 5, nameDisplayRect.y + 20);
        }

        Rectangle priceDisplayRect = new Rectangle(panelX + PADDING + 150, currentFieldY + VERTICAL_SPACING_FORM, INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(priceDisplayRect);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(priceDisplayRect);
        g2d.setColor(Color.WHITE);
        if (editingProduct != null) {
            g2d.drawString(String.format("%.2f", editingProduct.getCurrentPrice()), priceDisplayRect.x + 5, priceDisplayRect.y + 20);
        } else {
            g2d.drawString("0.00", priceDisplayRect.x + 5, priceDisplayRect.y + 20);
        }

        drawTriangleButton(g2d, priceIncrementButtonRect, true);
        drawTriangleButton(g2d, priceDecrementButtonRect, false);

        Rectangle supplyDisplayRect = new Rectangle(panelX + PADDING + 150, currentFieldY + 2 * VERTICAL_SPACING_FORM, INPUT_FIELD_WIDTH, INPUT_FIELD_HEIGHT);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(supplyDisplayRect);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(supplyDisplayRect);
        g2d.setColor(Color.WHITE);
        if (editingProduct != null) {
            g2d.drawString(String.valueOf(editingProduct.getCurrentSupply()), supplyDisplayRect.x + 5, supplyDisplayRect.y + 20);
        } else {
            g2d.drawString("0", supplyDisplayRect.x + 5, supplyDisplayRect.y + 20);
        }

        drawTriangleButton(g2d, supplyIncrementButtonRect, true);
        drawTriangleButton(g2d, supplyDecrementButtonRect, false);

        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(typeDropdownRect);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(typeDropdownRect);
        g2d.setColor(Color.WHITE);
        g2d.drawString(selectedProductType.getName(), typeDropdownRect.x + 5, typeDropdownRect.y + 20);
        drawTriangle(g2d, typeDropdownRect.x + typeDropdownRect.width - 15, typeDropdownRect.y + typeDropdownRect.height / 2 - 5, 10, 10, false);

        if (typeDropdownOpen) {
            g2d.setColor(new Color(50, 50, 70));
            g2d.fillRect(typeDropdownRect.x, typeDropdownRect.y + typeDropdownRect.height, typeDropdownRect.width, productTypes.size() * INPUT_FIELD_HEIGHT);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(typeDropdownRect.x, typeDropdownRect.y + typeDropdownRect.height, typeDropdownRect.width, productTypes.size() * INPUT_FIELD_HEIGHT);

            for (int i = 0; i < productTypes.size(); i++) {
                Rectangle optionRect = new Rectangle(typeDropdownRect.x, typeDropdownRect.y + typeDropdownRect.height + i * INPUT_FIELD_HEIGHT, typeDropdownRect.width, INPUT_FIELD_HEIGHT);
                g2d.setColor(Color.WHITE);
                g2d.drawString(productTypes.get(i).getName(), optionRect.x + 5, optionRect.y + 20);
            }
        }

        String actionText = (editingProduct != null && !editingProduct.getName().startsWith("New Product (")) ? "Update" : "Launch";
        drawButton(g2d, actionButtonRect, actionText, Color.BLUE);

        if (editingProduct != null) {
            double cost = editingProduct.getCurrentSupply() * editingProduct.getProductionCostPerUnit();
            String costString = "Cost: $" + String.format("%.2f", cost);
            g2d.setColor(Color.YELLOW); // Highlight cost
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            // Position it to the right of the action button
            g2d.drawString(costString, actionButtonRect.x + actionButtonRect.width + 10, actionButtonRect.y + actionButtonRect.height / 2 + 5);
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Launched Products", panelX + panelWidth / 2 + PADDING, panelY + PADDING + 30);

        int listStartX = panelX + panelWidth / 2 + PADDING;
        int productDisplayY = panelY + PADDING + 20 + PADDING + 10;
        int productHeight = 55;

        g2d.setColor(new Color(40, 40, 50, 180));
        g2d.fill(new RoundRectangle2D.Double(listStartX, panelY + PADDING + 50, panelWidth / 2 - 2 * PADDING, panelHeight - 2 * PADDING - 50, CORNER_RADIUS, CORNER_RADIUS));

        if (launchedProducts.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            String msg = "No products launched yet.";
            int msgWidth = g2d.getFontMetrics().stringWidth(msg);
            g2d.drawString(msg, listStartX + (panelWidth / 2 - 2 * PADDING - msgWidth) / 2, productDisplayY + 50);
        } else {
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            for (int i = 0; i < launchedProducts.size(); i++) {
                Product p = launchedProducts.get(i);
                int yPos = productDisplayY + i * productHeight;

                if (p == editingProduct) {
                    g2d.setColor(new Color(100, 100, 150, 150));
                    g2d.fillRect(listStartX + 5, yPos, panelWidth / 2 - 2 * PADDING - 10, productHeight - 5);
                }

                g2d.setColor(Color.WHITE);
                g2d.drawString("Name: " + p.getName(), listStartX + PADDING, yPos + 20);
                g2d.drawString("Price: $" + String.format("%.2f", p.getCurrentPrice()), listStartX + PADDING, yPos + 40);

                Rectangle editButtonRect = new Rectangle(listStartX + (panelWidth / 2 - 2 * PADDING) - 100, yPos + 5, 45, 18);
                Rectangle terminateButtonRect = new Rectangle(listStartX + (panelWidth / 2 - 2 * PADDING) - 50, yPos + 5, 45, 18);

                drawButton(g2d, editButtonRect, "Edit", new Color(50, 150, 200));
                drawButton(g2d, terminateButtonRect, "X", Color.RED);
            }
        }
    }

    private void drawButton(Graphics2D g2d, Rectangle rect, String text, Color color) {
        g2d.setColor(color);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 5, 5));
        g2d.setColor(Color.BLACK);
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 5, 5));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();
        g2d.drawString(text, rect.x + (rect.width - textWidth) / 2, rect.y + (rect.height - textHeight) / 2 + textHeight - 4);
    }

    private void drawTriangleButton(Graphics2D g2d, Rectangle rect, boolean isUpArrow) {
        g2d.setColor(new Color(70, 70, 90));
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 5, 5));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 5, 5));

        g2d.setColor(Color.WHITE);
        drawTriangle(g2d, rect.x, rect.y, rect.width, rect.height, isUpArrow);
    }

    private void drawTriangle(Graphics2D g2d, int x, int y, int width, int height, boolean isUpArrow) {
        int[] xPoints;
        int[] yPoints;

        if (isUpArrow) {
            xPoints = new int[]{x, x + width, x + width / 2};
            yPoints = new int[]{y + height, y + height, y};
        } else {
            xPoints = new int[]{x, x + width, x + width / 2};
            yPoints = new int[]{y, y, y + height};
        }
        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (newProductButtonRect.contains(mouseX, mouseY)) {
            clearFormForNewProduct();
            return;
        }

        if (priceIncrementButtonRect.contains(mouseX, mouseY)) {
            if (editingProduct != null) {
                editingProduct.setCurrentPrice(editingProduct.getCurrentPrice() + VALUE_STEP_PRICE);
            }
            return;
        }
        if (priceDecrementButtonRect.contains(mouseX, mouseY)) {
            if (editingProduct != null) {
                editingProduct.setCurrentPrice(Math.max(0, editingProduct.getCurrentPrice() - VALUE_STEP_PRICE));
            }
            return;
        }

        if (supplyIncrementButtonRect.contains(mouseX, mouseY)) {
            if (editingProduct != null) {
                editingProduct.setCurrentSupply(editingProduct.getCurrentSupply() + VALUE_STEP_SUPPLY);
            }
            return;
        }
        if (supplyDecrementButtonRect.contains(mouseX, mouseY)) {
            if (editingProduct != null) {
                editingProduct.setCurrentSupply(Math.max(0, editingProduct.getCurrentSupply() - VALUE_STEP_SUPPLY));
            }
            return;
        }

        if (typeDropdownRect.contains(mouseX, mouseY)) {
            typeDropdownOpen = !typeDropdownOpen;
        } else {
            typeDropdownOpen = false;
        }

        if (typeDropdownOpen) {
            for (int i = 0; i < productTypes.size(); i++) {
                Rectangle optionRect = new Rectangle(typeDropdownRect.x, typeDropdownRect.y + typeDropdownRect.height + i * INPUT_FIELD_HEIGHT, typeDropdownRect.width, INPUT_FIELD_HEIGHT);
                if (optionRect.contains(mouseX, mouseY)) {
                    selectedProductType = productTypes.get(i);
                    if (editingProduct != null) {
                        editingProduct.setType(selectedProductType);
                        editingProduct.setProductionCostPerUnit(selectedProductType.getBaseProductionCost());
                        if (editingProduct.getName().startsWith("New Product")) {
                            editingProduct.setName(selectedProductType.getName() + " (Custom)");
                        }
                        editingProduct.setCurrentPrice(selectedProductType.getBaseProductionCost() * 1.5);
                    }
                    typeDropdownOpen = false;
                    return;
                }
            }
        }

        if (actionButtonRect.contains(mouseX, mouseY)) {
            handleProductAction();
            return;
        }

        int listStartX = panelX + panelWidth / 2 + PADDING;
        int productDisplayY = panelY + PADDING + 20 + PADDING + 10;
        int productHeight = 55;

        for (int i = 0; i < launchedProducts.size(); i++) {
            Product p = launchedProducts.get(i);
            int yPos = productDisplayY + i * productHeight;

            Rectangle editButtonRect = new Rectangle(listStartX + (panelWidth / 2 - 2 * PADDING) - 100, yPos + 5, 45, 18);
            Rectangle terminateButtonRect = new Rectangle(listStartX + (panelWidth / 2 - 2 * PADDING) - 50, yPos + 5, 45, 18);

            if (editButtonRect.contains(mouseX, mouseY)) {
                loadProductForEditing(p);
                return;
            } else if (terminateButtonRect.contains(mouseX, mouseY)) {
                Business playerBusiness = Main.getInstance().getPlayerBusiness();
                if (playerBusiness instanceof ProductionMarketBusiness) {
                    ((ProductionMarketBusiness) playerBusiness).removeProduct(p);
                }
                setLaunchedProducts(launchedProducts);
                return;
            }
        }
    }

    public void handleKeyPress(int keyCode) {
        if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
            clearFormForNewProduct();
        }
    }

    public void open() {
        calculateLayout();
    }

    public void clearFormForNewProduct() {
        selectedProductType = ProductType.FOOD;
        editingProduct = new Product(
                "New Product (" + selectedProductType.getName() + ")",
                selectedProductType,
                selectedProductType.getBaseProductionCost() * 1.5,
                selectedProductType.getBaseProductionCost(),
                DEFAULT_INITIAL_SUPPLY_FOR_NEW_PRODUCT // Set a default initial supply
        );
        typeDropdownOpen = false;
    }

    private void handleProductAction() {
        if (editingProduct == null) {
            Main.getInstance().turnReportGUI.addEvent("Error: No product selected/created.");
            Main.getInstance().turnReportGUI.show();
            return;
        }

        Business playerBusiness = Main.getInstance().getPlayerBusiness();
        if (!(playerBusiness instanceof ProductionMarketBusiness)) {
            Main.getInstance().turnReportGUI.addEvent("Cannot manage products: You don't own a Production Market business!");
            Main.getInstance().turnReportGUI.show();
            return;
        }
        ProductionMarketBusiness pmb = (ProductionMarketBusiness) playerBusiness;

        double costToLaunchOrUpdate = editingProduct.getCurrentSupply() * editingProduct.getProductionCostPerUnit();
        if (Main.purse < costToLaunchOrUpdate) {
            Main.getInstance().startDialogue("no_money");
            return; // Stop the action if not enough money
        }

        boolean isNewProduct = !launchedProducts.contains(editingProduct);

        if (isNewProduct) {
            pmb.addProduct(editingProduct);
            Main.getInstance().turnReportGUI.addEvent("Launched new product: " + editingProduct.getName());
        } else {
            Main.getInstance().turnReportGUI.addEvent("Updated product: " + editingProduct.getName());
        }

        // Deduct cost after successful launch/update logic
        Main.purse -= costToLaunchOrUpdate;
        Main.getInstance().turnReportGUI.addEvent("Deducted $" + String.format("%.2f", costToLaunchOrUpdate) + " for product " + (isNewProduct ? "launch" : "update") + ".");

        Main.getInstance().turnReportGUI.show();

        setLaunchedProducts(pmb.getProducts());
        clearFormForNewProduct();
    }

    private void loadProductForEditing(Product p) {
        editingProduct = p;
        selectedProductType = p.getType();
        typeDropdownOpen = false;
    }
}