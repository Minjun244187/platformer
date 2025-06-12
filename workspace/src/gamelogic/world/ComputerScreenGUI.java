package gamelogic.world;

import gamelogic.Main;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;

public class ComputerScreenGUI {

    private boolean isOpen = false;

    // Dimensions and positioning for the main screen area
    private int screenX, screenY, screenWidth, screenHeight;
    private static final int SCREEN_PADDING = 20;
    private static final int CORNER_RADIUS = 15;

    // Rectangles for clickable areas and drawing
    private Rectangle xButtonRect;
    private Rectangle blackjackButtonRect;
    private Rectangle governmentPortalButtonRect;
    private Rectangle businessButtonRect;

    // Constants for button layout within the screen GUI
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_SPACING = 20;
    private static final int X_BUTTON_SIZE = 30;
    private static final int X_BUTTON_PADDING = 10; // Padding for the X button from the screen edge

    public ComputerScreenGUI() {
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    private void calculateLayout(int windowWidth, int windowHeight) {
        screenWidth = (int) (windowWidth * 0.7);
        screenHeight = (int) (windowHeight * 0.8);

        screenX = (windowWidth - screenWidth) / 2;
        screenY = (windowHeight - screenHeight) / 2;

        // Calculate bounds for X button relative to the main game window
        xButtonRect = new Rectangle(
                screenX + screenWidth - X_BUTTON_SIZE - X_BUTTON_PADDING,
                screenY + X_BUTTON_PADDING,
                X_BUTTON_SIZE,
                X_BUTTON_SIZE
        );

        // Calculate bounds for app buttons relative to the main game window
        int buttonsTotalHeight = 3 * BUTTON_HEIGHT + 2 * BUTTON_SPACING;
        int startY = screenY + screenHeight / 2 - buttonsTotalHeight / 2; // Vertically center the block of buttons
        int startX = screenX + (screenWidth - BUTTON_WIDTH) / 2; // Horizontally center buttons

        blackjackButtonRect = new Rectangle(startX, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        governmentPortalButtonRect = new Rectangle(startX, startY + BUTTON_HEIGHT + BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);
        businessButtonRect = new Rectangle(startX, startY + 2 * (BUTTON_HEIGHT + BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    /**
     * Renders the computer screen GUI entirely using Graphics2D.
     * @param g2d The Graphics2D context from the main game window.
     * @param mouseX Current mouse X coordinate relative to the game window.
     * @param mouseY Current mouse Y coordinate relative to the game window.
     */
    public void render(Graphics2D g2d, int mouseX, int mouseY) {
        if (!isOpen) return;

        // Save current G2D transform and clip to restore them later
        AffineTransform originalTransform = g2d.getTransform();
        Shape originalClip = g2d.getClip();

        // Apply rendering hints for quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate the graphics context to the top-left corner of our screen GUI
        g2d.translate(screenX, screenY);

        // Draw main desktop background (coordinates are now relative to screenX, screenY, so start from 0,0)
        g2d.setColor(new Color(0, 50, 100)); // Dark blue desktop
        g2d.fill(new RoundRectangle2D.Double(0, 0, screenWidth, screenHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw a subtle border (relative to screenX, screenY)
        g2d.setColor(new Color(20, 20, 20));
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(new RoundRectangle2D.Double(0, 0, screenWidth, screenHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw a "taskbar" or bottom bar (relative to screenX, screenY)
        g2d.setColor(new Color(50, 50, 50));
        g2d.fillRect(0, screenHeight - 40, screenWidth, 40);
        g2d.setColor(new Color(80, 80, 80));
        g2d.drawRect(0, screenHeight - 40, screenWidth, 40);

        // Draw a simple "Start" button on the taskbar (relative to screenX, screenY)
        g2d.setColor(new Color(100, 200, 100)); // Greenish start button
        g2d.fillRect(10, screenHeight - 35, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 16f));
        g2d.drawString("Start", 18, screenHeight - 15);

        // Reset translation for drawing buttons at their absolute positions on the main game window
        g2d.setTransform(originalTransform);

        // Draw the application buttons manually
        //drawCustomButton(g2d, blackjackButtonRect, "Blackjack Gambling", mouseX, mouseY, new Color(60, 179, 113));
        drawCustomButton(g2d, governmentPortalButtonRect, "Government Portal", mouseX, mouseY, new Color(70, 130, 180));
        //drawCustomButton(g2d, businessButtonRect, "My Business", mouseX, mouseY, new Color(255, 140, 0));

        // Draw the X button manually
        drawCustomXButton(g2d, xButtonRect, mouseX, mouseY);

        // Restore original G2D transform and clip after all rendering
        g2d.setClip(originalClip);
    }

    private void drawCustomButton(Graphics2D g2d, Rectangle rect, String text, int mouseX, int mouseY, Color baseColor) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? baseColor.brighter() : baseColor;
        Color borderColor = baseColor.darker();
        Color textColor = Color.WHITE;

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 20f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    private void drawCustomXButton(Graphics2D g2d, Rectangle rect, int mouseX, int mouseY) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? new Color(255, 100, 100) : new Color(220, 80, 80);
        Color borderColor = new Color(150, 0, 0);
        Color textColor = Color.WHITE;

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 24f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        String xText = "X";
        int textWidth = fm.stringWidth(xText);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(xText, textX, textY);
    }

    /**
     * Opens the computer screen GUI.
     */
    public void open() {
        isOpen = true;
        // Re-calculate layout in case screen size changed
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    /**
     * Closes the computer screen GUI.
     */
    public void close() {
        isOpen = false;
    }

    /**
     * Checks if the computer screen GUI is currently open.
     * @return true if open, false otherwise.
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Handles mouse clicks for the computer screen GUI.
     * This method must be called from Main's mouse click handler.
     * @param mouseX Mouse X coordinate relative to the game window.
     * @param mouseY Mouse Y coordinate relative to the game window.
     */
    public void handleMouseClick(int mouseX, int mouseY) {
        if (!isOpen) return; // Only process clicks if the GUI is open

        // Check if the click occurred within any of our button rectangles
        if (xButtonRect.contains(mouseX, mouseY)) {
            close(); // Close the screen
        } else if (blackjackButtonRect.contains(mouseX, mouseY)) {
            System.out.println("Opening Blackjack Gambling...");
            // TODO: In a future step, you will replace this with code to open your Blackjack gambling GUI.
        } else if (governmentPortalButtonRect.contains(mouseX, mouseY)) {
            System.out.println("Opening Government Portal...");
            if (Main.getInstance().governmentPortalGUI != null) {
                Main.getInstance().governmentPortalGUI.open();
                System.out.println("Opened");
            }
        } else if (businessButtonRect.contains(mouseX, mouseY)) {
            System.out.println("Opening My Business...");
            // TODO: In a future step, you will replace this with code to open your Business GUI.
            if (Main.getInstance().businessGUI != null) {
                Main.getInstance().businessGUI.open();
                System.out.println("Opened");
            }

        }
    }
}