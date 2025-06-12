package gamelogic;

import java.awt.*;
import java.text.DecimalFormat; // For formatting time if needed

import gamelogic.world.country; // Import the country class to get time

public class TimeDisplayGUI {

    private int x, y, width, height;
    private Color backgroundColor;
    private Color borderColor;
    private Color textColor;
    private Font font;
    private country gameCountry; // Reference to the country object
    public static int baseYear = 2002;

    public TimeDisplayGUI(int screenWidth, int screenHeight, country initialCountry) {
        // Define dimensions and position (top right)
        this.width = 190; // Width of the rectangle
        this.height = 40; // Height of the rectangle
        int padding = 10; // Padding from the screen edges
        this.x = screenWidth - this.width - padding - 40; // Calculate X for top-right
        this.y = padding; // Y for top-right

        // Minecraft-style colors (dark, slightly transparent background)
        this.backgroundColor = new Color(255, 255, 255, 200); // White, semi-transparent
        this.borderColor = new Color(200, 200, 200, 255); // Light gray for border
        this.textColor = Color.BLACK; // White text

        // Font for the time display
        this.font = new Font("Monospaced", Font.BOLD, 20); // A clean, bold font

        this.gameCountry = initialCountry; // Set the initial country instance
    }

    // Method to update the country instance if it changes (e.g., on level load)
    public void setCountry(country gameCountry) {
        this.gameCountry = gameCountry;
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother text rendering
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Draw the background rectangle (Minecraft inventory style)
        g2d.setColor(backgroundColor);
        g2d.fillRect(x, y, width, height);

        // 2. Draw the border
        g2d.setColor(borderColor);
        g2d.drawRect(x, y, width, height); // Outer border
        // Optional: Inner border for more depth (adjust values)
        // g2d.drawRect(x + 1, y + 1, width - 2, height - 2);

        // 3. Get and format the time
        String timeString = "Time: N/A";
        if (gameCountry != null) {
            // Assuming getTime() returns an integer representing game days/turns
            int time = gameCountry.getTime();
            timeString = "" + (getMonth(((time % 12) + 1)) + "," + (baseYear + (time / 12))); // Example format: "Day: 123"
            // If you have hours/minutes, you'd format differently:
            // int hours = time / 60;
            // int minutes = time % 60;
            // timeString = String.format("Time: %02d:%02d", hours, minutes);
        }
        g2d.drawImage(GameResources.calendar, 860, 10, 40, 40, null);

        // 4. Draw the time text
        g2d.setColor(textColor);
        g2d.setFont(font);

        // Center the text within the rectangle
        FontMetrics fm = g2d.getFontMetrics(font);
        int textWidth = fm.stringWidth(timeString) - 15;
        int textHeight = fm.getHeight();
        int textX = x + (width - textWidth) / 2 + 10;
        int textY = y + ((height - textHeight) / 2) + fm.getAscent();
        g2d.drawString(timeString, textX, textY);
    }
    public static String getCalender(int time) {
        return getMonth(((time % 12) + 1)) + "," + (baseYear + (time / 12));
    }
    private static String getMonth(int i) {
        if (i == 12) {
            return "December";
        } else if (i == 11) {
            return "November";
        } else if (i == 10) {
            return "October";
        } else if (i == 9) {
            return "September";
        } else if (i == 8) {
            return "August";
        } else if (i == 7) {
            return "July";
        } else if (i == 6) {
            return "June";
        } else if (i == 5) {
            return "May";
        } else if (i == 4) {
            return "April";
        } else if (i == 3) {
            return "March";
        } else if (i == 2) {
            return "February";
        } else if (i == 1) {
            return "January";
        } else {
            return "";
        }
    }
}
