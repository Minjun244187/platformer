package gamelogic.world;

import gamelogic.Main; // For fonts, screen dimensions, etc.
import gamelogic.Event; // The event/headline class

import java.awt.*;
import java.awt.geom.RoundRectangle2D; // For rounded panel corners
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream; // Added for loading resources
import java.util.List;
import java.util.ArrayList; // Used for the list of events

// For image loading
import javax.imageio.ImageIO;
// import java.io.File; // Removed: Direct File access should be avoided for JAR compatibility
// import java.net.URL; // Removed: Using InputStream directly with ImageIO.read()

public class NewspaperGUI {

    private boolean isOpen = false;
    private List<Event> currentNewsEvents; // List of events/headlines to display

    // Panel dimensions and positioning
    private int panelX, panelY, panelWidth, panelHeight;
    private static final int PANEL_PADDING = 30; // Padding around the entire newspaper content
    private static final int HEADER_HEIGHT = 80; // Space for masthead and date
    private static final int CORNER_RADIUS = 10; // Slightly rounded corners for the overall GUI panel

    // Layout constants for the newspaper content
    private static final int NUM_COLUMNS = 3;
    private static final int COLUMN_SPACING = 20; // Space between newspaper columns
    private static final int HEADLINE_PADDING = 10; // Padding around each headline/image block

    // Masthead (Newspaper Title) properties
    private String mastheadText = "THE RODMANIA TIMES";
    private String dateText = "June 9, 2025";
    private String taglineText = ""; // Can be set via a method or made dynamic

    // X button properties (similar to BankGUI)
    private Rectangle xButtonRect;
    private static final int X_BUTTON_SIZE = 30;
    private static final int X_BUTTON_PADDING = 10; // Padding from top/right edge of panel


    public NewspaperGUI() {
        this.currentNewsEvents = new ArrayList<>(); // Initialize the list
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    public void open(List<Event> events, String date) {
        this.currentNewsEvents = events; // Set the events to display
        this.dateText = date; // Set the current game date
        loadImagesForEvents(); // Load images when opening
        isOpen = true;
        calculateLayout(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); // Recalculate if window size changed
    }

    public void close() {
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Attempts to load images for all events in the list from the classpath.
     * This is crucial for JAR compatibility.
     */
    private void loadImagesForEvents() {
        for (Event event : currentNewsEvents) {
            // Only attempt to load if image is null and a path is provided
            if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                String path = event.getImagePath();
                // Ensure path starts with '/' for absolute classpath lookup
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is != null) {
                        event.setImage(ImageIO.read(is));
                    } else {
                        System.err.println("Newspaper Image not found on classpath: " + path);
                        // Optionally set a placeholder image
                    }
                } catch (IOException e) {
                    System.err.println("Error loading newspaper image from classpath: " + path + " - " + e.getMessage());
                }
            }
        }
    }


    private void calculateLayout(int windowWidth, int windowHeight) {
        // Define newspaper size relative to screen
        panelWidth = (int) (windowWidth * 0.8);
        panelHeight = (int) (windowHeight * 0.9);

        // Center the panel
        panelX = (windowWidth - panelWidth) / 2;
        panelY = (windowHeight - panelHeight) / 2;

        // Calculate X button position
        xButtonRect = new Rectangle(
                panelX + panelWidth - X_BUTTON_SIZE - X_BUTTON_PADDING,
                panelY + X_BUTTON_PADDING,
                X_BUTTON_SIZE,
                X_BUTTON_SIZE
        );
    }

    /**
     * Renders the Newspaper GUI.
     * @param g2d The Graphics2D object to draw on.
     * @param mouseX Current mouse X position (for hover/click)
     * @param mouseY Current mouse Y position (for hover/click)
     */
    public void render(Graphics2D g2d, int mouseX, int mouseY) {
        if (!isOpen) return;

        // Draw main panel background (Newspaper paper color)
        g2d.setColor(new Color(245, 245, 230)); // Off-white/cream for newspaper paper
        g2d.fill(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, CORNER_RADIUS, CORNER_RADIUS));
        g2d.setColor(new Color(50, 50, 50)); // Dark grey border
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, CORNER_RADIUS, CORNER_RADIUS));

        // Draw masthead (Newspaper Title)
        g2d.setColor(Color.BLACK);
        // Accessing font through Main.getInstance()
        g2d.setFont(Main.getInstance().getTimesNewRomanFont().deriveFont(Font.BOLD, 48f)); // Large, bold for title
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(mastheadText);
        g2d.drawString(mastheadText, panelX + (panelWidth - titleWidth) / 2, panelY + PANEL_PADDING + fm.getAscent());

        // Draw date and tagline
        // Accessing font through Main.getInstance()
        g2d.setFont(Main.getInstance().getTimesNewRomanFont().deriveFont(Font.PLAIN, 16f));
        fm = g2d.getFontMetrics(); // Get new font metrics
        int dateWidth = fm.stringWidth(dateText);
        g2d.drawString(dateText, panelX + PANEL_PADDING, panelY + HEADER_HEIGHT - fm.getDescent());
        int taglineWidth = fm.stringWidth(taglineText);
        g2d.drawString(taglineText, panelX + panelWidth - PANEL_PADDING - taglineWidth, panelY + HEADER_HEIGHT - fm.getDescent());

        // Draw separator line below header
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(panelX + PANEL_PADDING, panelY + HEADER_HEIGHT, panelX + panelWidth - PANEL_PADDING, panelY + HEADER_HEIGHT);


        // Calculate content area for headlines and images
        int contentAreaX = panelX + PANEL_PADDING;
        int contentAreaY = panelY + HEADER_HEIGHT + PANEL_PADDING;
        int contentAreaWidth = panelWidth - (2 * PANEL_PADDING);
        int contentAreaHeight = panelHeight - HEADER_HEIGHT - (2 * PANEL_PADDING);

        int columnWidth = (contentAreaWidth - (NUM_COLUMNS - 1) * COLUMN_SPACING) / NUM_COLUMNS;

        // Dynamic Y position tracking for current content
        int[] columnYPositions = new int[NUM_COLUMNS];
        for (int i = 0; i < NUM_COLUMNS; i++) {
            columnYPositions[i] = contentAreaY;
        }


        g2d.setClip(contentAreaX, contentAreaY, contentAreaWidth, contentAreaHeight); // Clip drawing to content area

        // Draw Headlines with Images
        g2d.setColor(Color.BLACK);
        int headlineCount = 0;

        for (Event event : currentNewsEvents) {
            if (headlineCount >= NUM_COLUMNS * 3) break; // Limit to a few headlines for visibility

            // Find the column with the least height
            int colIndex = 0;
            int minHeight = columnYPositions[0];
            for (int i = 1; i < NUM_COLUMNS; i++) {
                if (columnYPositions[i] < minHeight) {
                    minHeight = columnYPositions[i];
                    colIndex = i;
                }
            }

            int xPos = contentAreaX + colIndex * (columnWidth + COLUMN_SPACING);
            int yPos = columnYPositions[colIndex];


            // Draw image if available
            int contentHeight = 0; // Track height of current event block
            if (event.getImage() != null) {
                int imgWidth = columnWidth - 2 * HEADLINE_PADDING;
                int imgHeight = (int)(imgWidth * (event.getImage().getHeight() / (double)event.getImage().getWidth())); // Maintain aspect ratio
                if (imgHeight > 100) imgHeight = 100; // Cap max image height for layout

                g2d.drawImage(event.getImage(), xPos + HEADLINE_PADDING, yPos, imgWidth, imgHeight, null);
                contentHeight += imgHeight + 5; // Move Y down below image
            }

            // Draw headline text
            g2d.setFont(Main.getInstance().getFont().deriveFont(Font.BOLD, 18f)); // Headline font size
            fm = g2d.getFontMetrics();
            List<String> headlineLines = wrapText(g2d, event.getHeadline(), columnWidth - 2 * HEADLINE_PADDING);
            for (String line : headlineLines) {
                g2d.drawString(line, xPos + HEADLINE_PADDING, yPos + contentHeight + fm.getAscent());
                contentHeight += fm.getHeight();
            }
            contentHeight += fm.getHeight() / 2; // Add a small gap after headline


            // Draw body text (optional)
            if (event.getBody() != null && !event.getBody().isEmpty()) {
                g2d.setFont(Main.getInstance().getFont().deriveFont(Font.PLAIN, 12f));
                fm = g2d.getFontMetrics();
                List<String> bodyLines = wrapText(g2d, event.getBody(), columnWidth - 2 * HEADLINE_PADDING);
                for (String line : bodyLines) {
                    g2d.drawString(line, xPos + HEADLINE_PADDING, yPos + contentHeight + fm.getAscent());
                    contentHeight += fm.getHeight();
                }
            }

            // Update column height
            columnYPositions[colIndex] += contentHeight + HEADLINE_PADDING; // Add extra space at the end of the block

            // Draw a subtle separator between headlines
            g2d.setColor(new Color(180, 180, 180)); // Light grey separator
            g2d.drawLine(xPos + HEADLINE_PADDING, columnYPositions[colIndex] - HEADLINE_PADDING / 2, xPos + columnWidth - HEADLINE_PADDING, columnYPositions[colIndex] - HEADLINE_PADDING / 2);
            g2d.setColor(Color.BLACK); // Reset color


            headlineCount++;
        }

        g2d.setClip(null); // Reset clipping

        // Draw X button
        drawXButton(g2d, xButtonRect, mouseX, mouseY);
    }

    // Helper method to wrap text and return a list of lines
    private List<String> wrapText(Graphics2D g2d, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split(" ");
        String currentLine = "";
        for (String word : words) {
            if (fm.stringWidth(currentLine + word) < maxWidth) {
                currentLine += word + " ";
            } else {
                lines.add(currentLine.trim());
                currentLine = word + " ";
            }
        }
        lines.add(currentLine.trim()); // Add the last line
        return lines;
    }


    private void drawXButton(Graphics2D g2d, Rectangle rect, int mouseX, int mouseY) {
        boolean hovered = rect.contains(mouseX, mouseY);
        Color bgColor = hovered ? new Color(255, 100, 100) : new Color(220, 80, 80);
        Color borderColor = new Color(150, 0, 0);
        Color textColor = Color.WHITE;

        g2d.setColor(bgColor);
        g2d.fill(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Double(rect.x, rect.y, rect.width, rect.height, CORNER_RADIUS / 2, CORNER_RADIUS / 2));

        // Accessing font through Main.getInstance()
        g2d.setFont(Main.getInstance().getFont().deriveFont(Font.BOLD, 20f));
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        String xText = "X";
        int textWidth = fm.stringWidth(xText);
        int textX = rect.x + (rect.width - textWidth) / 2;
        int textY = rect.y + (rect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(xText, textX, textY);
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (!isOpen) return;

        if (xButtonRect.contains(mouseX, mouseY)) {
            close();
        }
        // Add more click handling here if headlines are clickable
    }
}