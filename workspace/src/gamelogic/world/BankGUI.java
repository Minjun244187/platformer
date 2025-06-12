package gamelogic.world;

import gamelogic.Main; // To access Main's static purse, loan, and historicalInterestRates
import gamelogic.audio.SoundManager;
import gamelogic.player.Player; // To access player methods if needed
import gameengine.input.KeyboardInputManager; // For key presses
import gameengine.input.MouseInputManager; // For mouse clicks

import java.awt.*;
import java.awt.event.KeyEvent; // For VK_ codes
import java.awt.geom.Line2D; // For drawing lines on the graph
import java.awt.geom.RoundRectangle2D; // For rounded panel
import java.util.ArrayList; // Not strictly needed here but good to include if you might modify lists
import java.util.List;


public class BankGUI {

    private boolean isOpen = false;
    private String title = "Bank Services";

    // Panel dimensions and positioning
    private int panelX, panelY, panelTotalWidth, panelTotalHeight;
    private static final int PANEL_PADDING = 20; // Padding around the buttons/content
    private static final int HEADER_HEIGHT = 50; // Space for title and money display
    private static final int CORNER_RADIUS = 20; // Rounded corner radius for the main panel

    // Button dimensions
    private static final int BUTTON_WIDTH = 180;
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTON_SPACING = 20; // Vertical space between buttons

    // Button hitboxes
    private Rectangle depositButtonRect;
    private Rectangle withdrawButtonRect;
    private Rectangle payLoanButtonRect;
    private Rectangle takeLoanButtonRect;

    // Messages for user feedback
    private String feedbackMessage = "";
    private long feedbackMessageDisplayTime = 0;
    private final long FEEDBACK_DURATION = 3000; // Milliseconds to display feedback

    // Dimensions for the small feedback chatbox (if desired as a separate box)
    private static final int FEEDBACK_BOX_HEIGHT = 50;
    private static final int FEEDBACK_BOX_SPACING_FROM_PANEL = 10;

    // X button properties
    private Rectangle xButtonRect;
    private static final int X_BUTTON_SIZE = 30;
    private static final int X_BUTTON_PADDING = 10; // Padding from top/right edge of panel

    // Graph area properties
    private Rectangle graphAreaRect;
    private static final int GRAPH_MIN_WIDTH = 300; // Minimum width for the graph area
    private static final int GRAPH_HORIZONTAL_PADDING = 20; // Padding inside the graph area for x-axis
    private static final int GRAPH_VERTICAL_PADDING = 20; // Padding inside the graph area for y-axis
    private SoundManager sm;


    public BankGUI() {
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); // Initial layout calc
        sm = new SoundManager();
        sm.loadClip("coin", "music/coin.wav");
        sm.loadClip("money", "music/moneydrop-1989.wav");
    }

    public void open() {
        isOpen = true;
        feedbackMessage = ""; // Clear any old messages
        feedbackMessageDisplayTime = 0; // Reset timer
        // Ensure initial layout is calculated when opened
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    public void close() {
        isOpen = false;
        feedbackMessage = "";
        feedbackMessageDisplayTime = 0; // Reset timer
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Calculates the dimensions and positions for the GUI panel and its contents.
     * This method should be called when the GUI is initialized or when window size changes.
     * @param windowWidth The current width of the game window.
     * @param windowHeight The current height of the game window.
     */
    private void calculateLayout(int windowWidth, int windowHeight) {
        // Calculate dimensions for the button column
        int buttonColumnWidth = BUTTON_WIDTH + (PANEL_PADDING * 2);

        // Calculate graph column width based on desired width of the entire panel.
        // Let's aim for a wider panel overall.
        panelTotalWidth = 700; // Fixed width for the entire bank GUI
        if (panelTotalWidth < buttonColumnWidth + GRAPH_MIN_WIDTH + PANEL_PADDING) {
            panelTotalWidth = buttonColumnWidth + GRAPH_MIN_WIDTH + PANEL_PADDING;
        }
        int graphColumnWidth = panelTotalWidth - buttonColumnWidth - PANEL_PADDING;


        // Calculate total panel height (driven by buttons, or could be fixed)
        panelTotalHeight = (BUTTON_HEIGHT * 4) + (BUTTON_SPACING * 3) + (PANEL_PADDING * 2) + HEADER_HEIGHT;


        // Center the panel
        panelX = (windowWidth - panelTotalWidth) / 2;
        panelY = (windowHeight - panelTotalHeight) / 2;

        // Calculate button positions (left column)
        int buttonStartX = panelX + PANEL_PADDING;
        int currentButtonY = panelY + HEADER_HEIGHT + PANEL_PADDING;

        depositButtonRect = new Rectangle(buttonStartX, currentButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        currentButtonY += BUTTON_HEIGHT + BUTTON_SPACING;

        withdrawButtonRect = new Rectangle(buttonStartX, currentButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        currentButtonY += BUTTON_HEIGHT + BUTTON_SPACING;

        payLoanButtonRect = new Rectangle(buttonStartX, currentButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        currentButtonY += BUTTON_HEIGHT + BUTTON_SPACING;

        takeLoanButtonRect = new Rectangle(buttonStartX, currentButtonY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Calculate X button position
        xButtonRect = new Rectangle(
                panelX + panelTotalWidth - X_BUTTON_SIZE - X_BUTTON_PADDING,
                panelY + X_BUTTON_PADDING,
                X_BUTTON_SIZE,
                X_BUTTON_SIZE
        );

        // Calculate Graph Area position (right column)
        graphAreaRect = new Rectangle(
                panelX + buttonColumnWidth + PANEL_PADDING, // Starts after button column + padding
                panelY + HEADER_HEIGHT + PANEL_PADDING, // Aligned with buttons vertically
                graphColumnWidth - PANEL_PADDING, // Adjusted for padding
                panelTotalHeight - HEADER_HEIGHT - (PANEL_PADDING * 2) // Adjusted for padding and header
        );
    }

    /**
     * Renders the bank GUI.
     * @param g2d The Graphics2D object to draw on.
     * @param windowWidth The current width of the game window.
     * @param windowHeight The current height of the game window.
     * @param player The Player object (for accessing purse, etc., though Main.purse is static here).
     * @param mouseX Current mouse X position (for hover/click)
     * @param mouseY Current mouse Y position (for hover/click)
     */
    public void render(Graphics2D g2d, int windowWidth, int windowHeight, Player player, int mouseX, int mouseY) {
        if (!isOpen) return;

        // Recalculate layout in case window size changed
        calculateLayout(windowWidth, windowHeight);

        // Draw main panel background (LIGHTER COLOR)
        g2d.setColor(new Color(220, 220, 240, 220)); // Light semi-transparent background
        g2d.fill(new RoundRectangle2D.Double(panelX, panelY, panelTotalWidth, panelTotalHeight, CORNER_RADIUS, CORNER_RADIUS));
        g2d.setColor(new Color(100, 100, 150, 255)); // Border color (Adjusted for contrast)
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(new RoundRectangle2D.Double(panelX, panelY, panelTotalWidth, panelTotalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw title
        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 28f));
        g2d.setColor(Color.DARK_GRAY); // Darker text for lighter background
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, panelX + (panelTotalWidth - titleWidth) / 2, panelY + HEADER_HEIGHT / 2 + fm.getAscent() / 2);


        //draw rect for display player's money stat.
        int startx = 10;
        int starty = 7;
        int panelw = 300;
        int panelh = 110;
        int fontstartx = 30;
        int fontstarty = 30;
        int fontmargin = 23;

        //draw rect
        g2d.setColor(new Color(27, 27, 81, 220)); // Light semi-transparent background
        g2d.fill(new RoundRectangle2D.Double(startx, starty, panelw, panelh, CORNER_RADIUS, CORNER_RADIUS));
        g2d.setColor(new Color(62, 48, 41, 255)); // Border color (Adjusted for contrast)
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new RoundRectangle2D.Double(startx, starty, panelw, panelh, CORNER_RADIUS, CORNER_RADIUS));;


        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 18f));
        g2d.setColor(new Color(222, 224, 237)); // Green for money
        String statusStr = "# Rodman's Financial Profile";
        g2d.drawString(statusStr, fontstartx,  fontstarty );

        // Draw current money
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 18f));
        g2d.setColor(new Color(80, 200, 80)); // Green for money
        String moneyStr = "Money: $" + String.format("%.2f", Main.purse);
        int moneyWidth = fm.stringWidth(moneyStr);
        //g2d.drawString(moneyStr, panelX + (panelTotalWidth - moneyWidth) / 2, panelY + HEADER_HEIGHT + fm.getAscent() + 5);
      //  g2d.drawString(moneyStr, panelX,  panelY + HEADER_HEIGHT + fm.getAscent());
        g2d.drawString(moneyStr, fontstartx,  fontstarty +  fontmargin);

        //Draw current balance
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 18f));
        g2d.setColor(Color.YELLOW); // Yellow for balance
        String balStr = "Account Balance: $" + String.format("%.2f", Main.balance);
        int balWidth = fm.stringWidth(balStr);
       // g2d.drawString(balStr, panelX + (panelTotalWidth - balWidth) / 2, panelY + HEADER_HEIGHT + fm.getAscent() * 2 + 10);
        g2d.drawString(balStr, fontstartx,  fontstarty + fontmargin + fontmargin);

        // Draw current loan debt
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 18f));
        g2d.setColor(new Color(248, 52, 52)); // Red for loan debt
        String loanStr = "Loan: $" + String.format("%.2f", Main.debt);
        int loanWidth = fm.stringWidth(loanStr);
       // g2d.drawString(loanStr, panelX + (panelTotalWidth - loanWidth) / 2, panelY + HEADER_HEIGHT + fm.getAscent() * 3 + 15);
        g2d.drawString(loanStr, fontstartx,  fontstarty + fontmargin + fontmargin + fontmargin);

        // Draw Buttons
        drawButton(g2d, depositButtonRect, "Deposit $100", mouseX, mouseY);
        drawButton(g2d, withdrawButtonRect, "Withdraw $100", mouseX, mouseY);
        drawButton(g2d, payLoanButtonRect, "Pay Loan $50", mouseX, mouseY);
        drawButton(g2d, takeLoanButtonRect, "Take Loan $100", mouseX, mouseY);

        // Draw X button
        drawXButton(g2d, xButtonRect, mouseX, mouseY);

        // Draw Graph Area
        g2d.setColor(new Color(200, 200, 200, 180)); // Light grey background for graph area
        g2d.fill(new RoundRectangle2D.Double(graphAreaRect.x, graphAreaRect.y, graphAreaRect.width, graphAreaRect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(new Color(80, 80, 100)); // Darker border for graph area
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(graphAreaRect.x, graphAreaRect.y, graphAreaRect.width, graphAreaRect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));


        drawInterestRateGraph(g2d, mouseX, mouseY);


        // Draw feedback message in a small box below the main panel
        if (!feedbackMessage.isEmpty() && System.currentTimeMillis() - feedbackMessageDisplayTime < FEEDBACK_DURATION) {
            int feedbackBoxY = panelY + panelTotalHeight + FEEDBACK_BOX_SPACING_FROM_PANEL;
            int feedbackBoxX = panelX;
            int feedbackBoxWidth = panelTotalWidth;

            // Draw feedback box background
            g2d.setColor(new Color(180, 180, 200, 200)); // Lighter feedback box
            g2d.fill(new RoundRectangle2D.Double(feedbackBoxX, feedbackBoxY, feedbackBoxWidth, FEEDBACK_BOX_HEIGHT, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
            g2d.setColor(new Color(80, 80, 100)); // Darker border
            g2d.setStroke(new BasicStroke(1));
            g2d.draw(new RoundRectangle2D.Double(feedbackBoxX, feedbackBoxY, feedbackBoxWidth, FEEDBACK_BOX_HEIGHT, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

            // Draw feedback text
            g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 20f));
            g2d.setColor(Color.BLUE.darker()); // Darker blue text for lighter background
            int msgWidth = fm.stringWidth(feedbackMessage);
            g2d.drawString(feedbackMessage, feedbackBoxX + (feedbackBoxWidth - msgWidth) / 2, feedbackBoxY + FEEDBACK_BOX_HEIGHT / 2 + fm.getAscent() / 2);
        }
    }

    private void drawButton(Graphics2D g2d, Rectangle rect, String text, int mouseX, int mouseY) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? new Color(200, 200, 220) : new Color(180, 180, 200); // Lighter background for buttons
        Color borderColor = new Color(100, 100, 150); // Darker border for contrast
        Color textColor = Color.BLACK; // Black text for lighter background

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 22f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    // Draw X button method
    private void drawXButton(Graphics2D g2d, Rectangle rect, int mouseX, int mouseY) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? new Color(255, 100, 100) : new Color(220, 80, 80); // Red, lighter when hovered
        Color borderColor = new Color(150, 0, 0); // Darker border
        Color textColor = Color.WHITE; // White text for red background

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        // Draw the 'X' character
        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 20f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        String xText = "X";
        int textWidth = fm.stringWidth(xText);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(xText, textX, textY);
    }

    private void drawInterestRateGraph(Graphics2D g2d, int mouseX, int mouseY) {
        List<Double> interestRates = Main.historicalInterestRates;

        FontMetrics fm = g2d.getFontMetrics();

        if (interestRates == null || interestRates.size() < 2) {
            // Draw a message if not enough data
            g2d.setFont(Main.getFont().deriveFont(Font.ITALIC, 16f));
            g2d.setColor(Color.GRAY);
            fm = g2d.getFontMetrics();
            String noDataMsg = "Not enough data for graph.";
            int msgWidth = fm.stringWidth(noDataMsg);
            g2d.drawString(noDataMsg, graphAreaRect.x + (graphAreaRect.width - msgWidth) / 2, graphAreaRect.y + graphAreaRect.height / 2);
            return;
        }

        // Determine graph boundaries within the graphAreaRect, accounting for internal padding
        int graphPlotX = graphAreaRect.x + GRAPH_HORIZONTAL_PADDING;
        int graphPlotY = graphAreaRect.y + GRAPH_VERTICAL_PADDING;
        int graphPlotWidth = graphAreaRect.width - 2 * GRAPH_HORIZONTAL_PADDING;
        int graphPlotHeight = graphAreaRect.height - 2 * GRAPH_VERTICAL_PADDING;

        if (graphPlotWidth <= 0 || graphPlotHeight <= 0) return; // Prevent drawing errors if dimensions are too small

        // Find min and max interest rates for scaling
        double minRate = interestRates.get(0);
        double maxRate = interestRates.get(0);
        for (double rate : interestRates) {
            if (rate < minRate) minRate = rate;
            if (rate > maxRate) maxRate = rate;
        }

        // Add a small buffer to maxRate to prevent graph touching the top edge
        maxRate *= 1.1; // 10% buffer
        if (minRate < 0) minRate *= 1.1; // If negative, buffer for the bottom

        // If min and max are the same (flat line), give it a small range to avoid division by zero
        if (maxRate == minRate) {
            maxRate = minRate + 0.01; // Ensure a tiny range for scaling
            minRate = minRate - 0.01;
            if (minRate < 0) minRate = 0; // Prevent negative min if starting at 0
        }


        // Draw graph lines
        g2d.setColor(new Color(0, 150, 0)); // Green for interest rate line
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i < interestRates.size() - 1; i++) {
            double rate1 = interestRates.get(i);
            double rate2 = interestRates.get(i + 1);

            // Scale X coordinates (time)
            int x1 = (int) (graphPlotX + (i / (double) (interestRates.size() - 1)) * graphPlotWidth);
            int x2 = (int) (graphPlotX + ((i + 1) / (double) (interestRates.size() - 1)) * graphPlotWidth);

            // Scale Y coordinates (interest rate) - invert for screen coordinates
            // Ensure values are within the range for scaling
            double scaledRate1 = (rate1 - minRate) / (maxRate - minRate);
            double scaledRate2 = (rate2 - minRate) / (maxRate - minRate);

            int y1 = (int) (graphPlotY + graphPlotHeight * (1 - scaledRate1));
            int y2 = (int) (graphPlotY + graphPlotHeight * (1 - scaledRate2));

            g2d.draw(new Line2D.Double(x1, y1, x2, y2));
        }

        // Draw Y-axis label
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 12f));
        g2d.setColor(Color.DARK_GRAY);
        String yLabel = "Interest Rate (%)";
        // Rotate text for Y-axis
        g2d.translate(graphPlotX - fm.getHeight(), graphPlotY + graphPlotHeight / 2);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString(yLabel, -fm.stringWidth(yLabel) / 2, 0); // Position centered
        g2d.rotate(Math.PI / 2); // Rotate back
        g2d.translate(-(graphPlotX - fm.getHeight()), -(graphPlotY + graphPlotHeight / 2));

        // Draw X-axis label
        String xLabel = "Time Passed (Months)";
        g2d.drawString(xLabel, graphPlotX + (graphPlotWidth - fm.stringWidth(xLabel)) / 2, +10 + graphPlotY + graphPlotHeight + fm.getAscent() + 5);

        // Draw min/max Y-axis values
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 10f));
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawString(String.format("%.2f%%", maxRate), 30 + graphPlotX - fm.stringWidth(String.format("%.2f%%", maxRate)) - 5, graphPlotY + fm.getAscent() / 2);
        g2d.drawString(String.format("%.2f%%", minRate), 30 + graphPlotX - fm.stringWidth(String.format("%.2f%%", minRate)) - 5, graphPlotY + graphPlotHeight + fm.getAscent() / 2);


        if (graphAreaRect.contains(mouseX, mouseY)) {
            // Calculate which data point the mouse is closest to
            double mouseXRelativeToGraph = mouseX - graphPlotX;
            double percentageAcross = mouseXRelativeToGraph / graphPlotWidth;

            // Ensure percentageAcross is clamped between 0 and 1
            percentageAcross = Math.max(0.0, Math.min(1.0, percentageAcross));

            int hoveredIndex = (int) (percentageAcross * (interestRates.size() - 1));
            // Clamp index to valid range
            hoveredIndex = Math.max(0, Math.min(interestRates.size() - 1, hoveredIndex));

            double hoveredRate = interestRates.get(hoveredIndex);
            String tooltipText = String.format("Interest Rate: %.2f%%", hoveredRate);

            // Draw tooltip
            g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 14f));
            g2d.setColor(new Color(50, 50, 50, 200)); // Dark semi-transparent background
            g2d.fillRect(mouseX + 10, mouseY + 10, g2d.getFontMetrics().stringWidth(tooltipText) + 10, g2d.getFontMetrics().getHeight() + 5);
            g2d.setColor(Color.WHITE);
            g2d.drawString(tooltipText, mouseX + 15, mouseY + 10 + g2d.getFontMetrics().getAscent());
        }
    }


    /**
     * Handles mouse clicks for the bank GUI buttons.
     * @param mouseX Mouse X position.
     * @param mouseY Mouse Y position.
     * @param player The player instance.
     */
    public void handleMouseClick(int mouseX, int mouseY, Player player) {
        System.out.println("handled bank");
        if (!isOpen) return;

        if (depositButtonRect.contains(mouseX, mouseY)) {
            if (Main.purse >= 100) {
                if (Main.debt >= 1000) {
                    displayFeedback("Too much debt! Pay off loan for transactions!");
                } else {
                    Main.purse -= 100;
                    Main.balance += 100;// Deposit $100
                    displayFeedback("Deposited $100");
                    sm.play("money");
                }

            } else {
                displayFeedback("Not enough money!");
            }
        } else if (withdrawButtonRect.contains(mouseX, mouseY)) {
            if (Main.balance >= 100) {
                if (Main.debt >= 1000) {
                    displayFeedback("Too much debt! Pay off loan for transactions!");
                } else {
                    Main.purse += 100;
                    Main.balance -= 100;// Withdraw $100
                    displayFeedback("Withdrew $100");
                    sm.play("money");
                }
            } else {
                displayFeedback("Not enough money!");
            }
        } else if (payLoanButtonRect.contains(mouseX, mouseY)) {
            if ((Main.balance >= 50 || Main.purse >= 50) && Main.debt >= 50) {
                Main.payLoan(50);
                displayFeedback("Paid $50 loan!");
                sm.play("money");
            } else if (Main.debt >= 0 && (Main.balance >= Main.debt || Main.purse >= Main.debt)) {
                Main.payLoan(Main.debt);
                displayFeedback("Paid remaining loan!");
                sm.play("money");
            } else {
                displayFeedback("You can't pay your loan!");
            }
             // Pay $50 towards loan
        } else if (takeLoanButtonRect.contains(mouseX, mouseY)) {
            if (Main.debt >= 1000) {
                displayFeedback("Too much debt! Pay off loan for transactions!");
            } else {
                Main.takeLoan(100);
                displayFeedback("Took 100$ loan from bank!");
                sm.play("money");
            }
            // Take a $100 loan
        } else if (xButtonRect.contains(mouseX, mouseY)) {
            close(); // Close the bank GUI
        }
    }

    private void displayFeedback(String message) {
        this.feedbackMessage = message;
        this.feedbackMessageDisplayTime = System.currentTimeMillis();
    }
}