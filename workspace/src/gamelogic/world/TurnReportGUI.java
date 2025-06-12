package gamelogic.world;

import gamelogic.Main; // Import Main for SCREEN_WIDTH/HEIGHT
import gamelogic.audio.SoundManager;
import gamelogic.player.Player;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TurnReportGUI {
    private boolean isVisible = false;
    private Player player;
    private List<String> lastTurnEvents = new ArrayList<>();
    private int x, y, width, height;
    private Font reportFont;
    private SoundManager sm = new SoundManager();

    // --- NEW: Animation Fields ---
    public List<String> allReportLines; // Stores all lines to be displayed
    public int currentLineIndex = 0; // Tracks how many lines are currently visible
    public long lastRevealTime = 0L; // Timestamp of the last line reveal
    private static final long LINE_REVEAL_DELAY = 240; // Delay in milliseconds between each line (e.g., 100-300ms)

    public TurnReportGUI(Player player) {
        this.player = player;
        this.width = 340;
        this.height = 380; // Increased height to accommodate more lines and events
        this.x = (Main.SCREEN_WIDTH - width) / 2;
        this.y = 100;
        this.reportFont = new Font("Courier New", Font.PLAIN, 16); // Typewriter-like font
        this.allReportLines = new ArrayList<>();
        sm.loadClip("paper", "music/paper.wav");
    }

    public void addEvent(String event) {
        this.lastTurnEvents.add(event);
    }

    public void clearEvents() {
        this.lastTurnEvents.clear();
    }

    // --- NEW: prepareReportLines method ---
    private void prepareReportLines() {
        allReportLines.clear(); // Clear previous report content

        // Add main status lines
        allReportLines.add("--- STATUS REPORT ---");
        allReportLines.add(" "); // Spacer
        allReportLines.add("Health: " + player.getCondition());
        allReportLines.add("Money: $" + String.format("%.2f", player.getPurse()));
        allReportLines.add("Bank Balance: $" + String.format("%.2f", player.getBalance()));
        allReportLines.add("Job: " + player.getJob());
        allReportLines.add(" "); // Spacer

        // Add turn events if any
        if (!lastTurnEvents.isEmpty()) {
            allReportLines.add("--- Events ---");
            for (String event : lastTurnEvents) {
                allReportLines.add("- " + event);
            }
        }
    }

    public void show() {
        isVisible = true;
        currentLineIndex = 0; // Start with no lines visible
        lastRevealTime = System.currentTimeMillis(); // Reset reveal timer
        prepareReportLines(); // Prepare all lines for the current report
        sm.play("paper");
    }

    public void hide() {
        isVisible = false;
        currentLineIndex = 0; // Reset for next time
    }

    public boolean isVisible() {
        return isVisible;
    }

    // --- NEW: Update method for animation ---
    public void update() {
        if (!isVisible) {
            return;
        }

        // If not all lines have been revealed and enough time has passed
        if (currentLineIndex < allReportLines.size()) {
            if (System.currentTimeMillis() - lastRevealTime >= LINE_REVEAL_DELAY) {
                currentLineIndex++;
                lastRevealTime = System.currentTimeMillis(); // Update timer for the next line
            }
        }
    }

    public void render(Graphics2D g2d) {
        if (!isVisible) {
            return;
        }

        Color paperColor = new Color(245, 245, 220, 240); // Off-white paper, slightly transparent
        Color borderColor = new Color(100, 100, 100, 200); // Dark gray border, slightly transparent

        // Simulate uneven edges
        RoundRectangle2D paperShape = new RoundRectangle2D.Float(x - 5, y - 5, width + 10, height + 10, 15, 15);
        g2d.setColor(paperColor);
        g2d.fill(paperShape);
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(paperShape);
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(Color.BLACK);
        g2d.setFont(reportFont); // Use the base font for line height calculation

        int lineHeight = g2d.getFontMetrics().getHeight() + 2; // Add a small buffer between lines
        int currentY = y + 30; // Starting Y for the first line

        // --- MODIFIED: Loop only up to currentLineIndex ---
        for (int i = 0; i < currentLineIndex && i < allReportLines.size(); i++) {
            String line = allReportLines.get(i);
            // Apply different font for titles if needed
            if (line.startsWith("---")) {
                g2d.setFont(new Font(reportFont.getName(), Font.BOLD, 20));
                int titleWidth = g2d.getFontMetrics().stringWidth(line);
                g2d.drawString(line, x + (width - titleWidth) / 2, currentY);
                g2d.setFont(reportFont); // Reset to normal font for subsequent lines
            } else {
                g2d.drawString(line, x + 20, currentY); // Draw with a left indent
            }
            currentY += lineHeight; // Move to the next line position
        }
    }
}