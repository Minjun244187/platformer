package gamelogic.world; // Assuming it's in the world package, similar to GovernmentPortalGUI

import gamelogic.Main;
import gamelogic.world.business.AirportBusiness;
import gamelogic.world.business.ProductionMarketBusiness;
import gamelogic.world.business.ProductType;
import gamelogic.world.business.Business; // Import Business for type checking

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform; // This import might not be needed if not used, but keep for now

public class BusinessGUI {

    private boolean isOpen = false;

    // Dimensions and positioning for the portal's main area
    private int portalX, portalY, portalWidth, portalHeight;
    private static final int PORTAL_PADDING = 20;
    private static final int CORNER_RADIUS = 15;
    private static final int TITLE_BAR_HEIGHT = 40;

    // Rectangles for clickable areas and drawing
    private Rectangle xButtonRect;
    private Rectangle startProductionButtonRect;
    private Rectangle startAirportButtonRect;
    private Rectangle mainContentAreaRect; // Not directly used for drawing, more for conceptual layout

    // Constants for button layout
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_SPACING = 20;
    private static final int X_BUTTON_SIZE = 30;
    private static final int X_BUTTON_PADDING = 10;
    private ProductManagementPanel productManagementPanel;

    public BusinessGUI() {
        // Initialize with dummy values, will be recalculated on open()
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
        // Initialize productManagementPanel here, it will be sized correctly on calculateLayout()
        // It needs to be initialized with dimensions from the content area of BusinessGUI
        // For now, use placeholder values, will be updated in calculateLayout and draw
        productManagementPanel = new ProductManagementPanel(0, 0, 0, 0);
    }

    private void calculateLayout(int screenWidth, int screenHeight) {
        // Make GUI bigger
        portalWidth = screenWidth * 4 / 5; // Increased size
        portalHeight = screenHeight * 4 / 5; // Increased size
        portalX = (screenWidth - portalWidth) / 2;
        portalY = (screenHeight - portalHeight) / 2;

        xButtonRect = new Rectangle(portalX + portalWidth - X_BUTTON_SIZE - X_BUTTON_PADDING,
                portalY + X_BUTTON_PADDING, X_BUTTON_SIZE, X_BUTTON_SIZE);

        // Define the main content area where either business start buttons or product management panel goes
        mainContentAreaRect = new Rectangle(portalX + PORTAL_PADDING, portalY + TITLE_BAR_HEIGHT + PORTAL_PADDING,
                portalWidth - 2 * PORTAL_PADDING, portalHeight - TITLE_BAR_HEIGHT - 2 * PORTAL_PADDING);

        // Adjust positions of start business buttons relative to the main content area
        int startButtonY = mainContentAreaRect.y + (mainContentAreaRect.height - (BUTTON_HEIGHT * 2 + BUTTON_SPACING)) / 2;

        startProductionButtonRect = new Rectangle(mainContentAreaRect.x + (mainContentAreaRect.width - BUTTON_WIDTH) / 2,
                startButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        startAirportButtonRect = new Rectangle(startProductionButtonRect.x,
                startProductionButtonRect.y + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Update productManagementPanel's dimensions
        productManagementPanel = new ProductManagementPanel(mainContentAreaRect.x, mainContentAreaRect.y,
                mainContentAreaRect.width, mainContentAreaRect.height);
        // Pass current products to the panel if a business exists
        Business playerBusiness = Main.getInstance().getPlayerBusiness();
        if (playerBusiness instanceof ProductionMarketBusiness) {
            productManagementPanel.setLaunchedProducts(((ProductionMarketBusiness) playerBusiness).getProducts());
        } else {
            productManagementPanel.setLaunchedProducts(null); // Clear products if not production business
        }
    }

    public void draw(Graphics g) {
        if (!isOpen) return;

        Graphics2D g2d = (Graphics2D) g;

        // Draw portal background
        g2d.setColor(new Color(40, 40, 60, 240));
        g2d.fill(new RoundRectangle2D.Double(portalX, portalY, portalWidth, portalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw title bar
        g2d.setColor(new Color(20, 20, 30));
        g2d.fill(new RoundRectangle2D.Double(portalX, portalY, portalWidth, TITLE_BAR_HEIGHT, CORNER_RADIUS, CORNER_RADIUS));
        g2d.fillRect(portalX, portalY + CORNER_RADIUS, portalWidth, TITLE_BAR_HEIGHT - CORNER_RADIUS);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String title = "Business Management Portal";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, portalX + (portalWidth - titleWidth) / 2, portalY + 28);

        // Draw X button
        g2d.setColor(Color.RED);
        g2d.fillOval(xButtonRect.x, xButtonRect.y, xButtonRect.width, xButtonRect.height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("X", xButtonRect.x + xButtonRect.width / 2 - 5, xButtonRect.y + xButtonRect.height / 2 + 6);


        // Check if player already owns a business
        Business playerBusiness = Main.getInstance().getPlayerBusiness();

        if (playerBusiness == null) {
            // Player has no business, show options to start one
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String instruction = "Choose a business to start:";
            int instWidth = g2d.getFontMetrics().stringWidth(instruction);
            g2d.drawString(instruction, mainContentAreaRect.x + (mainContentAreaRect.width - instWidth) / 2,
                    mainContentAreaRect.y + 50);

            // Draw buttons for starting businesses
            drawButton(g2d, startProductionButtonRect, "Start Production Market ($3000)", new Color(50, 150, 50));
            drawButton(g2d, startAirportButtonRect, "Start Airport ($100,000,000)", new Color(50, 100, 150));
        } else if (playerBusiness instanceof ProductionMarketBusiness) {
            // Player owns a ProductionMarketBusiness, show product management panel
            productManagementPanel.render(g2d, (int) Main.getInstance().getMouseInputManager().getMouseX(),
                    (int) Main.getInstance().getMouseInputManager().getMouseY());
        } else {
            // Player owns another type of business (e.g., AirportBusiness)
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String msg = "You own " + playerBusiness.getName() + " (" + playerBusiness.getType() + ")";
            int msgWidth = g2d.getFontMetrics().stringWidth(msg);
            g2d.drawString(msg, mainContentAreaRect.x + (mainContentAreaRect.width - msgWidth) / 2,
                    mainContentAreaRect.y + mainContentAreaRect.height / 2);
            g2d.setFont(new Font("Arial", Font.PLAIN, 18));
            String msg2 = "Management features for this business are coming soon!";
            int msg2Width = g2d.getFontMetrics().stringWidth(msg2);
            g2d.drawString(msg2, mainContentAreaRect.x + (mainContentAreaRect.width - msg2Width) / 2,
                    mainContentAreaRect.y + mainContentAreaRect.height / 2 + 40);
        }
    }

    private void drawButton(Graphics2D g2d, Rectangle rect, String text, Color color) {
        g2d.setColor(color);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 10, 10));
        g2d.setColor(Color.BLACK);
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, 10, 10));
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();
        g2d.drawString(text, rect.x + (rect.width - textWidth) / 2, rect.y + (rect.height - textHeight) / 2 + textHeight - 4);
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (xButtonRect.contains(mouseX, mouseY)) {
            close();
            return;
        }

        Business playerBusiness = Main.getInstance().getPlayerBusiness();

        if (playerBusiness == null) {
            // Only process start business clicks if no business is owned
            if (startProductionButtonRect.contains(mouseX, mouseY)) {
                if (Main.purse >= 3000) { // Access purse via player object
                    Main.purse -= 3000; // Deduct from player's purse
                    Main.getInstance().setPlayerBusiness(
                            new ProductionMarketBusiness("Player's Production Market", 3000, Main.getInstance().getCurrentLevel().getPlayer())
                    );
                    Main.getInstance().turnReportGUI.addEvent("Successfully started a Production Market business!");
                } else {
                    Main.getInstance().turnReportGUI.addEvent("Not enough money for Production Market! ($3000 needed)");
                }
                Main.getInstance().turnReportGUI.show();
            } else if (startAirportButtonRect.contains(mouseX, mouseY)) {
                if (Main.purse >= 100_000_000) {
                    Main.purse -= 100_000_000;
                    Main.getInstance().setPlayerBusiness(
                            new AirportBusiness("Rodmania Airport", 100_000_000, Main.getInstance().getCurrentLevel().getPlayer(), 500, 100000)
                    );
                    Main.getInstance().turnReportGUI.addEvent("Successfully started an Airport business!");
                } else {
                    Main.getInstance().turnReportGUI.addEvent("Not enough money for Airport! ($100,000,000 needed)");
                }
                Main.getInstance().turnReportGUI.show();
            }
        } else if (playerBusiness instanceof ProductionMarketBusiness) {
            // If it's a ProductionMarketBusiness, pass the click to its panel
            productManagementPanel.handleMouseClick(mouseX, mouseY);
        }
        // No action for other business types for now
    }

    public void handleKeyPress(int keyCode) {
        // Allow ProductManagementPanel to handle key presses if it's active
        Business playerBusiness = Main.getInstance().getPlayerBusiness();
        if (playerBusiness instanceof ProductionMarketBusiness) {
            productManagementPanel.handleKeyPress(keyCode);
        }
        // Main GUI escape to close itself
        if (keyCode == java.awt.event.KeyEvent.VK_ESCAPE) {
            close();
        }
    }


    public void open() {
        isOpen = true;
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); // Recalculate layout on open
        // When opening, ensure the product management panel is also aware it's being "opened"
        if (Main.getInstance().getPlayerBusiness() instanceof ProductionMarketBusiness) {
            productManagementPanel.open();
        }
    }

    public void close() {
        isOpen = false;
        // When closing, ensure the product management panel is also aware it's being "closed"
        if (Main.getInstance().getPlayerBusiness() instanceof ProductionMarketBusiness) {
            // productManagementPanel.close(); // Not needed as it just clears the form
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}