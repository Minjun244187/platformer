package gamelogic.world;

import gamelogic.Main;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GovernmentPortalGUI {

    private boolean isOpen = false;

    // Dimensions and positioning for the portal's main area
    private int portalX, portalY, portalWidth, portalHeight;
    private static final int PORTAL_PADDING = 20;
    private static final int CORNER_RADIUS = 15;
    private static final int TITLE_BAR_HEIGHT = 40;

    // Rectangles for clickable areas and drawing
    private Rectangle xButtonRect;
    private Rectangle taxButtonRect;
    private Rectangle spendingButtonRect;
    private Rectangle gdpButtonRect;
    private Rectangle interestButtonRect;
    private Rectangle populationButtonRect;
    private Rectangle graphAreaRect;

    // Constants for button layout
    private static final int BUTTON_WIDTH = 180;
    private static final int BUTTON_HEIGHT = 40;
    private static final int BUTTON_SPACING = 15;
    private static final int X_BUTTON_SIZE = 30;
    private static final int X_BUTTON_PADDING = 10;

    // Graph data is now accessed from Main.instance (historicalTaxRates, historicalSpending, historicalGrowthRate)

    private String currentGraphType = "Tax Rate"; // Default graph

    public GovernmentPortalGUI() {
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    private void calculateLayout(int windowWidth, int windowHeight) {
        portalWidth = (int) (windowWidth * 0.8);
        portalHeight = (int) (windowHeight * 0.85);

        portalX = (windowWidth - portalWidth) / 2;
        portalY = (windowHeight - portalHeight) / 2;

        // X button in the top right corner of the portal
        xButtonRect = new Rectangle(
                portalX + portalWidth - X_BUTTON_SIZE - X_BUTTON_PADDING,
                portalY + X_BUTTON_PADDING,
                X_BUTTON_SIZE,
                X_BUTTON_SIZE
        );

        // Buttons for graph selection on the left side of the portal
        int buttonPanelX = portalX + PORTAL_PADDING;
        int buttonPanelY = portalY + TITLE_BAR_HEIGHT + PORTAL_PADDING;
        int buttonCurrentY = buttonPanelY;

        taxButtonRect = new Rectangle(buttonPanelX, buttonCurrentY, BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonCurrentY += BUTTON_HEIGHT + BUTTON_SPACING;
        spendingButtonRect = new Rectangle(buttonPanelX, buttonCurrentY, BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonCurrentY += BUTTON_HEIGHT + BUTTON_SPACING;
        gdpButtonRect = new Rectangle(buttonPanelX, buttonCurrentY, BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonCurrentY += BUTTON_HEIGHT + BUTTON_SPACING;
        interestButtonRect = new Rectangle(buttonPanelX, buttonCurrentY, BUTTON_WIDTH, BUTTON_HEIGHT);
        buttonCurrentY += BUTTON_HEIGHT + BUTTON_SPACING;
        populationButtonRect = new Rectangle(buttonPanelX, buttonCurrentY, BUTTON_WIDTH, BUTTON_HEIGHT);


        // Graph drawing area on the right side
        int graphAreaX = buttonPanelX + BUTTON_WIDTH + PORTAL_PADDING * 2;
        int graphAreaY = portalY + TITLE_BAR_HEIGHT + PORTAL_PADDING;
        int graphAreaWidth = portalWidth - (graphAreaX - portalX) - PORTAL_PADDING;
        int graphAreaHeight = portalHeight - TITLE_BAR_HEIGHT - PORTAL_PADDING * 2;
        graphAreaRect = new Rectangle(graphAreaX, graphAreaY, graphAreaWidth, graphAreaHeight);
    }

    /**
     * Renders the Government Portal GUI.
     * @param g2d The Graphics2D context from the main game window.
     * @param mouseX Current mouse X coordinate.
     * @param mouseY Current mouse Y coordinate.
     */
    public void render(Graphics2D g2d, int mouseX, int mouseY) {
        if (!isOpen) return;

        // Save original transform and clip
        AffineTransform originalTransform = g2d.getTransform();
        Shape originalClip = g2d.getClip();

        // Apply rendering hints for quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw main portal background
        g2d.setColor(new Color(40, 40, 70)); // Dark blue-grey for portal background
        g2d.fill(new RoundRectangle2D.Double(portalX, portalY, portalWidth, portalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw portal border
        g2d.setColor(new Color(20, 20, 40));
        g2d.setStroke(new BasicStroke(3));
        g2d.draw(new RoundRectangle2D.Double(portalX, portalY, portalWidth, portalHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw title bar
        g2d.setColor(new Color(60, 60, 90));
        g2d.fillRect(portalX, portalY, portalWidth, TITLE_BAR_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 22f));
        String title = "Rodmania Government Status Portal";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, portalX + (portalWidth - fm.stringWidth(title)) / 2, portalY + fm.getAscent() + (TITLE_BAR_HEIGHT - fm.getHeight()) / 2);

        // Draw graph selection buttons
        drawCustomButton(g2d, taxButtonRect, "Tax Rate", mouseX, mouseY, currentGraphType.equals("Tax Rate"));
        drawCustomButton(g2d, spendingButtonRect, "Government Spending", mouseX, mouseY, currentGraphType.equals("Government Spending"));
        drawCustomButton(g2d, gdpButtonRect, "GDP Growth", mouseX, mouseY, currentGraphType.equals("GDP Growth"));
        drawCustomButton(g2d, interestButtonRect, "Interest Rate", mouseX, mouseY, currentGraphType.equals("Interest Rate"));
        drawCustomButton(g2d, populationButtonRect, "Population", mouseX, mouseY, currentGraphType.equals("Population"));


        // Draw the graph area background
        g2d.setColor(new Color(20, 20, 30)); // Darker background for graph area
        g2d.fill(new RoundRectangle2D.Double(graphAreaRect.x, graphAreaRect.y, graphAreaRect.width, graphAreaRect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(new Color(80, 80, 100));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(new RoundRectangle2D.Double(graphAreaRect.x, graphAreaRect.y, graphAreaRect.width, graphAreaRect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        // Draw the selected graph
        drawGraph(g2d, currentGraphType);

        // Draw X button
        drawCustomXButton(g2d, xButtonRect, mouseX, mouseY);

        // Restore original G2D transform and clip
        g2d.setTransform(originalTransform);
        g2d.setClip(originalClip);
    }

    private void drawCustomButton(Graphics2D g2d, Rectangle rect, String text, int mouseX, int mouseY, boolean isSelected) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color baseColor = new Color(50, 90, 120); // Blueish color for buttons
        if (isSelected) {
            baseColor = new Color(70, 150, 180); // Lighter blue if selected
        }
        Color bgColor = hovered ? baseColor.brighter() : baseColor; // Apply hover effect
        Color borderColor = baseColor.darker();
        Color textColor = Color.WHITE;

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 16f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);
    }

    private void drawCustomXButton(Graphics2D g2d, Rectangle rect, int mouseX, int mouseY) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? new Color(255, 100, 100) : new Color(220, 80, 80); // Apply hover effect
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

    private void drawGraph(Graphics2D g2d, String type) {
        List<Double> data;
        String yAxisLabel;
        String xAxisLabel = "Time (Months)";
        Color lineColor;

        // Ensure Main.instance is not null before accessing historical data
        if (Main.getInstance() == null) {
            // Fallback or error handling if Main instance is not available
            data = Arrays.asList(0.0, 0.0); // Default empty or minimal data
            yAxisLabel = "N/A";
            lineColor = Color.GRAY;
            System.err.println("Main.instance is null when drawing Government Portal graph.");
            // Draw a message indicating data is unavailable
            g2d.setColor(Color.RED);
            g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 20f));
            String msg = "Data Unavailable";
            FontMetrics fm = g2d.getFontMetrics();
            int msgX = graphAreaRect.x + (graphAreaRect.width - fm.stringWidth(msg)) / 2;
            int msgY = graphAreaRect.y + graphAreaRect.height / 2;
            g2d.drawString(msg, msgX, msgY);
            return; // Exit early if data source is not ready
        }

        switch (type) {
            case "Tax Rate":
                data = Main.getInstance().historicalTaxRates;
                yAxisLabel = "Tax Rate (%)";
                lineColor = Color.YELLOW;
                break;
            case "Government Spending":
                data = Main.getInstance().historicalSpending;
                yAxisLabel = "Government Spending ($)";
                lineColor = Color.CYAN;
                break;
            case "GDP Growth":
                data = Main.getInstance().historicalGrowthRate;
                yAxisLabel = "GDP Growth (%)";
                lineColor = Color.GREEN;
                break;
            case "Interest Rate":
                data = Main.getInstance().historicalInterestRates;
                yAxisLabel = "Interest Rate (%)";
                lineColor = Color.ORANGE;
                break;
            case "Population":
                // Convert List<Integer> to List<Double> for consistent plotting
                data = Main.getInstance().historicalPopRate.stream().map(Integer::doubleValue).collect(Collectors.toList());
                yAxisLabel = "Population";
                lineColor = Color.MAGENTA;
                break;
            default:
                data = Arrays.asList(0.0); // Fallback
                yAxisLabel = "";
                lineColor = Color.WHITE;
                break;
        }

        if (data.isEmpty()) {
            // Draw a message indicating no data
            g2d.setColor(Color.ORANGE);
            g2d.setFont(Main.getFont().deriveFont(Font.BOLD, 18f));
            String msg = "No Data Available";
            FontMetrics fm = g2d.getFontMetrics();
            int msgX = graphAreaRect.x + (graphAreaRect.width - fm.stringWidth(msg)) / 2;
            int msgY = graphAreaRect.y + graphAreaRect.height / 2;
            g2d.drawString(msg, msgX, msgY);
            return;
        }

        // Find min and max data values from the actual data
        double minDataValue = data.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
        double maxDataValue = data.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
        FontMetrics fm = g2d.getFontMetrics();

        // Determine min/max values for Y-axis scaling
        double minValue, maxValue;
        final double Y_AXIS_VALUE_PADDING_FACTOR = 0.15; // Padding for Y-axis values

        // Types that can have negative values and should be symmetric around 0
        boolean canBeNegative = type.equals("GDP Growth") || type.equals("Interest Rate");

        if (canBeNegative) {
            double absoluteMaxRange = Math.max(Math.abs(minDataValue), Math.abs(maxDataValue));
            maxValue = absoluteMaxRange * (1.0 + Y_AXIS_VALUE_PADDING_FACTOR);
            minValue = -maxValue; // Symmetric negative value
        } else { // For Tax Rate, Government Spending, Population: typically non-negative, start at 0
            minValue = 0.0; // Start Y-axis at 0
            maxValue = maxDataValue * (1.0 + Y_AXIS_VALUE_PADDING_FACTOR);
            // If data is all zero, ensure a small positive range for visibility
            if (maxValue == 0.0 && minDataValue == 0.0) {
                maxValue = 1.0;
            }
        }

        // Graph drawing area dimensions
        int graphX = graphAreaRect.x;
        int graphY = graphAreaRect.y;
        int graphWidth = graphAreaRect.width;
        int graphHeight = graphAreaRect.height;

        // Define inner graph boundaries for data points and axes
        int innerGraphX = graphX + PORTAL_PADDING;
        int innerGraphY = graphY + PORTAL_PADDING;
        int innerGraphWidth = graphWidth - PORTAL_PADDING * 2;
        int innerGraphHeight = graphHeight - PORTAL_PADDING * 2;

        // X-axis will now start at innerGraphX, filling the innerGraphWidth
        int lineGraphDrawStartX = innerGraphX;
        int lineGraphUsableWidth = innerGraphWidth;

        // Draw axes
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(1));
        // Y-axis
        g2d.drawLine(innerGraphX, innerGraphY + innerGraphHeight, innerGraphX, innerGraphY);
        // X-axis (base line for periods)
        g2d.drawLine(innerGraphX, innerGraphY + innerGraphHeight, innerGraphX + innerGraphWidth, innerGraphY + innerGraphHeight);

        // Draw horizontal line at Y=0 if the range crosses zero
        if (minValue < 0 && maxValue > 0) {
            int zeroYPos = innerGraphY + innerGraphHeight - (int)(innerGraphHeight * (0.0 - minValue) / (maxValue - minValue));
            g2d.setColor(new Color(120, 120, 120, 150)); // Slightly transparent gray for zero line
            g2d.drawLine(innerGraphX, zeroYPos, innerGraphX + innerGraphWidth, zeroYPos);
            g2d.setColor(Color.LIGHT_GRAY); // Reset color for other lines
        }

        // Draw labels
        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 12f));
        g2d.setColor(Color.WHITE); // Ensure label color is white

        // Y-axis label
        g2d.drawString(yAxisLabel, innerGraphX, innerGraphY - 5);

        // Draw Y-axis value markers
        int numYMarkers = 4; // This will result in 5 labels (min, 3 intermediates, max)
        for (int i = 0; i <= numYMarkers; i++) {
            double value = minValue + (maxValue - minValue) * (i / (double)numYMarkers);
            int yPos = innerGraphY + innerGraphHeight - (int)(innerGraphHeight * (value - minValue) / (maxValue - minValue));
            g2d.drawLine(innerGraphX - 5, yPos, innerGraphX, yPos); // Tick mark
            String valStr = String.format("%.1f", value);
            // Adjust position for Y-axis value labels
            g2d.drawString(valStr, innerGraphX - fm.stringWidth(valStr) - 10, yPos + fm.getHeight() / 4); // Place to the left of Y-axis
        }

        // Draw X-axis markers and labels (period numbers)
        if (data.size() > 0) {
            int numLabelsToShow = Math.min(data.size(), 10); // Show max 10 labels for readability
            for (int i = 0; i < data.size(); i++) {
                int currentXForLabel;
                if (data.size() > 1) {
                    currentXForLabel = lineGraphDrawStartX + (int)(lineGraphUsableWidth * i / (data.size() - 1.0));
                } else { // Single data point: align with start of axis
                    currentXForLabel = lineGraphDrawStartX;
                }

                // Draw a small tick mark
                g2d.drawLine(currentXForLabel, innerGraphY + innerGraphHeight, currentXForLabel, innerGraphY + innerGraphHeight + 5);

                // Only draw labels for a subset of points if there are many, or for all if few
                if (data.size() <= numLabelsToShow || i == 0 || i == data.size() - 1 || (data.size() > numLabelsToShow && i % (Math.max(1, data.size() / numLabelsToShow)) == 0)) {
                    String periodLabel = (i + 1) + ""; // Period 1, 2, 3, ...
                    // Center the text below the tick mark
                    int labelWidth = fm.stringWidth(periodLabel);
                    g2d.drawString(periodLabel, currentXForLabel - labelWidth / 2, innerGraphY + innerGraphHeight + 20);
                }
            }
        }

        g2d.setFont(Main.getFont().deriveFont(Font.PLAIN, 14f)); // Slightly larger font for main axis label
        int xAxisLabelWidth = fm.stringWidth(xAxisLabel);
        g2d.drawString(xAxisLabel, innerGraphX + (innerGraphWidth - xAxisLabelWidth) / 2, innerGraphY + innerGraphHeight + 35); // Position below period labels


        // Draw graph line
        g2d.setColor(lineColor);
        g2d.setStroke(new BasicStroke(2));

        int prevX = -1;
        int prevY = -1;

        for (int i = 0; i < data.size(); i++) {
            double value = data.get(i);
            int currentX;
            if (data.size() > 1) {
                currentX = lineGraphDrawStartX + (int)(lineGraphUsableWidth * i / (data.size() - 1.0));
            } else { // Single data point: align with start of axis
                currentX = lineGraphDrawStartX;
            }

            int currentY = innerGraphY + innerGraphHeight - (int)(innerGraphHeight * (value - minValue) / (maxValue - minValue));

            // Draw point
            g2d.fillOval(currentX - 3, currentY - 3, 6, 6);

            if (prevX != -1) {
                g2d.drawLine(prevX, prevY, currentX, currentY);
            }
            prevX = currentX;
            prevY = currentY;
        }
    }

    public void open() {
        isOpen = true;
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    public void close() {
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Handles mouse clicks for the Government Portal GUI.
     * @param mouseX Mouse X coordinate relative to the game window.
     * @param mouseY Mouse Y coordinate relative to the game window.
     */
    public void handleMouseClick(int mouseX, int mouseY) {
        if (!isOpen) return;

        if (xButtonRect.contains(mouseX, mouseY)) {
            close();
        } else if (taxButtonRect.contains(mouseX, mouseY)) {
            currentGraphType = "Tax Rate";
        } else if (spendingButtonRect.contains(mouseX, mouseY)) {
            currentGraphType = "Government Spending";
        } else if (gdpButtonRect.contains(mouseX, mouseY)) {
            currentGraphType = "GDP Growth";
        } else if (interestButtonRect.contains(mouseX, mouseY)) {
            currentGraphType = "Interest Rate";
        } else if (populationButtonRect.contains(mouseX, mouseY)) {
            currentGraphType = "Population";
        }
    }
}