package gamelogic.world;

import gamelogic.Main;
import gamelogic.player.Item;
import gamelogic.player.Player;

import java.awt.*;
import java.awt.geom.RoundRectangle2D; // For rounded rectangles
import java.util.List;
import java.awt.event.KeyEvent; // For keyboard input

public class ShopCategoryGUI {
    public boolean invFull = false;
    public boolean noMoney = false;
    public String purchasedItem = "";
    public double purchasedCost = -1;
    private String title;
    private List<Item> shopItems;
    private boolean isOpen = false;

    // --- NEW: For keyboard selection ---
    private int selectedIndex = 0; // Index of the currently selected item

    // Constants for layout - matching inventory's style
    private static final int SLOT_SIZE = 64; // Size of the drawable item area within a slot (e.g., for image)
    private static final int CELL_SIZE = 70; // Total size of a slot, including padding (e.g., SLOT_SIZE + 6 pixels padding)
    private static int COLS = 5; // Number of columns in the shop display
    private static final int PANEL_PADDING = 10; // Padding around the item grid within the panel
    private static final int HEADER_HEIGHT = 50; // Space for title and money at the top of the panel
    private static final int CORNER_RADIUS = 20; // Rounded corner radius for the main panel


    private static final int SCREEN_WIDTH = 300;
    private static final int SCREEN_HEIGHT = 300;

    // Calculated panel and content start positions
    private int panelX, panelY, panelTotalWidth, panelTotalHeight;
    private int contentStartX, contentStartY;
    private int numRows;

    private long lastPurchaseTime = 0;
    private static final long PURCHASE_COOLDOWN = 500;


    public ShopCategoryGUI(String title, List<Item> items) {
        this.title = title;
        this.shopItems = items;
        COLS = items.size();
        // Ensure selectedIndex is valid if the list is empty
        if (shopItems.isEmpty()) {
            selectedIndex = -1; // No item is selected if the shop is empty
        } else {
            selectedIndex = 0; // Select the first item by default
        }
    }

    /**
     * Calculates the dimensions and positions for the GUI panel and its contents.
     * This method should be called when the GUI is initialized or when the number of items changes.
     */
    private void calculateLayout(int windowWidth, int windowHeight) { // MODIFIED SIGNATURE
        numRows = (shopItems.size() + COLS - 1) / COLS;
        if (numRows == 0) numRows = 1;

        int panelContentWidth = COLS * CELL_SIZE;
        int panelContentHeight = numRows * CELL_SIZE;

        panelTotalWidth = panelContentWidth + (PANEL_PADDING * 2);
        panelTotalHeight = panelContentHeight + (PANEL_PADDING * 2) + HEADER_HEIGHT;

        // Calculate top-left position to center the entire panel within the GIVEN window dimensions
        panelX = (windowWidth - panelTotalWidth) / 2; // MODIFIED: Use windowWidth
        panelY = (windowHeight - panelTotalHeight) / 2; // MODIFIED: Use windowHeight

        contentStartX = panelX + PANEL_PADDING;
        contentStartY = panelY + PANEL_PADDING + HEADER_HEIGHT;
    }


    public void open() {
        long currentTime = System.currentTimeMillis();
        lastPurchaseTime = currentTime;
        isOpen = true;
        // Reset selected index when opening the shop
        if (!shopItems.isEmpty()) {
            selectedIndex = 0;
        } else {
            selectedIndex = -1;
        }
    }

    public void close() { isOpen = false; }

    public String getTitle() { return title; }

    /**
     * Renders the shop GUI on the screen.
     * @param g The Graphics context to draw on.
     * @param player The Player object, used to display current money.
     * @param mouseX No longer used for item selection, but kept for method signature compatibility
     * @param mouseY No longer used for item selection, but kept for method signature compatibility
     */
    public void render(Graphics g, Player player, int mouseX, int mouseY, int windowWidth, int windowHeight) { // mouseX, mouseY are now ignored for item selection
        if (!isOpen) return;

        calculateLayout(windowWidth, windowHeight);

        Graphics2D g2d = (Graphics2D) g; // Cast to Graphics2D for advanced drawing capabilities



        // Enable Anti-aliasing for smoother shapes and text rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        // --- Panel Drawing (Matching Inventory Style) ---

        // 1. Draw a subtle drop shadow for the whole panel
        g2d.setColor(new Color(0, 0, 0, 120)); // Semi-transparent black shadow
        // Offset the shadow slightly to create the effect
        g2d.fill(new RoundRectangle2D.Float(panelX + 10, panelY + 10, panelTotalWidth, panelTotalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // 2. Draw the main panel background with a gradient and rounded corners
        GradientPaint panelGradient = new GradientPaint(
                panelX, panelY, new Color(50, 50, 70, 220), // Dark grey-blue at the top
                panelX, panelY + panelTotalHeight, new Color(20, 20, 30, 220)); // Even darker at the bottom
        g2d.setPaint(panelGradient); // Set the paint to the gradient
        g2d.fill(new RoundRectangle2D.Float(panelX, panelY, panelTotalWidth, panelTotalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // 3. Draw a border for the panel
        g2d.setColor(new Color(100, 100, 150)); // Lighter border color
        g2d.setStroke(new BasicStroke(2)); // Thicker border stroke
        g2d.draw(new RoundRectangle2D.Float(panelX, panelY, panelTotalWidth, panelTotalHeight, CORNER_RADIUS, CORNER_RADIUS));
        g2d.setStroke(new BasicStroke(1)); // Reset stroke to default (important for other drawings)


        // --- Draw Header Text (Title, ESC Text, Player Money) ---

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24)); // Larger, bold font for the title
        g2d.drawString(title, panelX + PANEL_PADDING + 10, panelY + PANEL_PADDING + 20); // Position title

        g2d.setFont(new Font("Arial", Font.PLAIN, 18)); // Normal font for other text
        String escText = "ESC to close";
        int escTextWidth = g2d.getFontMetrics().stringWidth(escText);
        // Position "ESC to close" text to the right
        g2d.drawString(escText, panelX + panelTotalWidth - escTextWidth - PANEL_PADDING - 10, panelY + PANEL_PADDING + 20);

        // Display Player's Money
        g2d.drawString("Money: $" + String.format("%.2f", player.getPurse()), panelX + PANEL_PADDING + 10, panelY + PANEL_PADDING + 45);


        // --- Render Items and Slots ---

        // Item hoveredItem = null; // No longer needed for mouse hover
        // int hoveredMouseX = 0; // No longer needed for mouse hover
        // int hoveredMouseY = 0; // No longer needed for mouse hover


        for (int i = 0; i < (numRows * COLS); i++) {
            int row = i / COLS;
            int col = i % COLS;

            // Calculate the top-left position for the current slot
            int slotX = contentStartX + col * CELL_SIZE;
            int slotY = contentStartY + row * CELL_SIZE;

            // 4. Draw individual slot rectangles with a sunken/beveled effect (matching inventory)
            // Inner fill (darker, recessed look)
            g2d.setColor(new Color(30, 30, 40)); // Darker interior color
            g2d.fillRect(slotX + 2, slotY + 2, SLOT_SIZE, SLOT_SIZE); // Fill the inner area (SLOT_SIZE is 64x64)

            // Highlights (top and left edges)
            g2d.setColor(new Color(80, 80, 100)); // Lighter color for highlights
            g2d.drawLine(slotX + 2, slotY + 2, slotX + SLOT_SIZE + 2, slotY + 2); // Top line
            g2d.drawLine(slotX + 2, slotY + 2, slotX + 2, slotY + SLOT_SIZE + 2); // Left line

            // Shadows (bottom and right edges)
            g2d.setColor(new Color(10, 10, 15)); // Darker color for shadows
            g2d.drawLine(slotX + 2, slotY + SLOT_SIZE + 2, slotX + SLOT_SIZE + 2, slotY + SLOT_SIZE + 2); // Bottom line
            g2d.drawLine(slotX + SLOT_SIZE + 2, slotY + 2, slotX + SLOT_SIZE + 2, slotY + SLOT_SIZE + 2); // Right line

            // Draw the overall border for the slot
            g2d.setColor(new Color(60, 60, 80)); // Medium gray for the border
            g2d.drawRect(slotX + 2, slotY + 2, SLOT_SIZE, SLOT_SIZE); // Draw border for the inner rectangle

            // --- NEW: Draw selection highlight if this is the selected item ---
            if (i == selectedIndex) {
                g2d.setColor(Color.CYAN); // Highlight color for the selected item
                g2d.setStroke(new BasicStroke(3)); // Thicker border for highlight
                g2d.drawRect(slotX + 1, slotY + 1, SLOT_SIZE + 2, SLOT_SIZE + 2); // Draw around the slot
                g2d.setStroke(new BasicStroke(1)); // Reset stroke
            }


            // Draw item if the current slot index has an item
            if (i < shopItems.size()) {
                Item item = shopItems.get(i);
                if (item.getImage() != null) {
                    // Draw item image, scaling it to fit the SLOT_SIZE (64x64) inner area
                    g2d.drawImage(item.getImage(), slotX + 2, slotY + 2, SLOT_SIZE, SLOT_SIZE, null);
                }
                // Draw item price below the slot
                g2d.setColor(Color.YELLOW); // Price in yellow
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14)); // Bold font for price
                g2d.drawString("$" + String.format("%.2f", item.getCost()), slotX + 5, slotY + CELL_SIZE + 15); // Adjust Y for price position
            }
        }

        // --- Render Tooltip for Selected Item ---
        if (selectedIndex != -1 && selectedIndex < shopItems.size()) {
            Item selectedItem = shopItems.get(selectedIndex);
            // Calculate position for tooltip relative to the selected item's slot
            int row = selectedIndex / COLS;
            int col = selectedIndex % COLS;
            int slotX = contentStartX + col * CELL_SIZE;
            int slotY = contentStartY + row * CELL_SIZE;

            // Position tooltip to the right or below the selected item
            int tooltipDisplayX = slotX + CELL_SIZE + 10; // 10 pixels to the right of the slot
            int tooltipDisplayY = slotY;

            // Adjust tooltip position to prevent it from going off the screen
            // Re-use logic from renderTooltip to adjust based on SCREEN_WIDTH/HEIGHT
            int tooltipWidth = 200; // Assuming tooltip width
            int tooltipHeight = 80; // Assuming tooltip height

            // Adjust tooltip position to prevent it from going off the screen
            if (tooltipDisplayX + tooltipWidth > windowWidth) { // MODIFIED: Use windowWidth
                tooltipDisplayX = slotX - tooltipWidth - 10;
                if (tooltipDisplayX < 0) tooltipDisplayX = 10;
            }
            if (tooltipDisplayY + tooltipHeight > windowHeight) { // MODIFIED: Use windowHeight
                tooltipDisplayY = windowHeight - tooltipHeight - 10;
            }
            if (tooltipDisplayY < 0) tooltipDisplayY = 10;

            renderTooltip(g2d, selectedItem, tooltipDisplayX, tooltipDisplayY);
        }
    }

    /**
     * Renders a tooltip for a given item at the specified position.
     * @param g2d The Graphics2D context to draw on.
     * @param item The Item for which to display the tooltip.
     * @param x X-coordinate for the tooltip's top-left corner.
     * @param y Y-coordinate for the tooltip's top-left corner.
     */
    private void renderTooltip(Graphics2D g2d, Item item, int x, int y) {
        int tooltipWidth = 280;
        int tooltipHeight = 100;

        g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black background for tooltip
        g2d.fillRect(x, y, tooltipWidth, tooltipHeight);
        g2d.setColor(Color.WHITE); // White text for tooltip content

        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14)); // Bold for item name
        g2d.drawString(item.getName(), x + 5, y + 15);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12)); // Plain for description
        g2d.drawString(item.getDesc(), x + 5, y + 30);
        g2d.setColor(Color.CYAN);
        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12)); // Plain for description
        g2d.drawString(item.getDesc2(), x + 5, y + 45);

        g2d.setColor(item.getRarityColor()); //for rarity

        g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 12)); // Bold for rarity
        g2d.drawString(item.getRarity() + "", x + 5, y + 75);

        g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN)); // Reset font to default plain for subsequent drawings
    }

    /**
     * Handles keyboard input for navigating the shop and purchasing items.
     * This method should be called from your game's key listener.
     * @param keyCode The key code of the pressed key.
     * @param player The Player object, used for transactions.
     */
    public void handleKeyPress(int keyCode, Player player) {
        if (!isOpen || shopItems.isEmpty()) {
            return; // Only process input if shop is open and has items
        }

        int numItems = shopItems.size();

        switch (keyCode) {
            case KeyEvent.VK_UP:
                if (selectedIndex - COLS >= 0) {
                    selectedIndex -= COLS;
                } else {
                    // Optional: wrap around to the bottom row
                    // selectedIndex = numItems - 1 - ((numItems - 1 - selectedIndex) % COLS);
                }
                break;
            case KeyEvent.VK_DOWN:
                if (selectedIndex + COLS < numItems) {
                    selectedIndex += COLS;
                } else {
                    // Optional: wrap around to the top row
                    // selectedIndex = (selectedIndex % COLS);
                }
                break;
            case KeyEvent.VK_LEFT:
                if (selectedIndex % COLS != 0) {
                    selectedIndex--;
                } else {
                    // Optional: wrap around to the end of the row
                    // if (selectedIndex + COLS - 1 < numItems) {
                    //     selectedIndex += COLS - 1;
                    // }
                }
                break;
            case KeyEvent.VK_RIGHT:
                if ((selectedIndex + 1) % COLS != 0 && selectedIndex + 1 < numItems) {
                    selectedIndex++;
                } else {
                    // Optional: wrap around to the beginning of the next row
                    // if (selectedIndex % COLS == COLS - 1 || selectedIndex == numItems -1) {
                    //      selectedIndex = (selectedIndex / COLS) * COLS;
                    // }
                }
                break;
            case KeyEvent.VK_ENTER: // Or KeyEvent.VK_SPACE for purchase
                purchaseSelectedItem(player);
                break;
        }
    }

    /**
     * Attempts to purchase the currently selected item.
     * @param player The Player object for the transaction.
     */
    private void purchaseSelectedItem(Player player) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPurchaseTime < PURCHASE_COOLDOWN) {
            return; // Exit if still on cooldown
        }
        if (selectedIndex != -1 && selectedIndex < shopItems.size()) {
            Item itemToPurchase = shopItems.get(selectedIndex);
            if (!Main.getInventory().isFull()) {
                if (player.getPurse() >= itemToPurchase.getCost()) {
                    player.changePurse(-itemToPurchase.getCost()); // Deduct cost from player's money
                    Main.getInventory().addItem(itemToPurchase.copy());
                    lastPurchaseTime = currentTime;// Add a copy of the item to inventory
                    System.out.println("Purchased " + itemToPurchase.getName());
                    Main.moneySpent += itemToPurchase.getCost();
                    purchasedItem = itemToPurchase.getName();
                    purchasedCost = itemToPurchase.getCost();// Confirmation message
                } else {
                    System.out.println("Not enough money to purchase " + itemToPurchase.getName());
                    noMoney = true; // Not enough money
                }
            }   else {
                System.out.println("Inventory is full!");
                invFull = true;
            }
        }
    }

    // This method is no longer needed for item selection, but might be used for closing the shop via mouse click on a close button etc.
    public void handleMouseClick(int mouseX, int mouseY, Player player) {
        if (!isOpen) return; // Only process clicks if the GUI is open
        // Current implementation for mouse click to purchase is removed.
        // If you still need to close the shop via mouse click on a specific area,
        // you would add that logic here.
    }
}