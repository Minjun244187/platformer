package gamelogic;

import gameengine.input.KeyboardInputManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class TitleScreen {
    //Game Version
    private static final String version = "v0.1.1 alpha";
    // --- Existing Colors (adjusted BG_COLOR and button colors for consistent green tones) ---
    private static final Color BG_COLOR = new Color(0, 50, 25); // Darker green for a base
    private static final Color BUTTON_COLOR_TOP = new Color(90, 150, 220); // Lighter shade for top
    private static final Color BUTTON_COLOR_BOTTOM = new Color(40, 90, 160); // Darker shade for bottom
    private static final Color BUTTON_HOVER_TOP = new Color(120, 200, 255); // Lighter hover
    private static final Color BUTTON_HOVER_BOTTOM = new Color(60, 120, 200); // Darker hover

    private String[] mainOptions = {"NEW GAME", "LOAD GAME", "QUIT"};
    private String[] difficultyOptions = {"NORMAL", "HARD"};
    private String[] difficultyDescriptions = {
            "Basic Economics, ideal for new players.",
            "Life is hard mode, with wartime economics."
    };

    private int selectedOption = 0;
    private int selectedDifficulty = 0;
    private boolean inDifficultyMenu = false;
    private boolean isActive = true;

    // Animation variables
    private float titleY = -100;
    private float optionOffset = 50;
    private float bgOffset = 0;
    private float pulse = 0;
    private boolean pulseGrowing = true;

    // Visual effects
    private BufferedImage backgroundPattern;
    private BufferedImage logo;
    private Timer animationTimer;

    public TitleScreen() {
        backgroundPattern = createPatternTexture();
        logo = GameResources.logo;
    }

    public void update() {
        if (!inDifficultyMenu) {
            if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_UP)) {
                selectedOption = Math.max(0, selectedOption - 1);
            } else if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_DOWN)) {
                selectedOption = Math.min(mainOptions.length - 1, selectedOption + 1);
            } else if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_Z) || KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
                processMainMenuInput();
            }
        } else {
            if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_UP)) {
                selectedDifficulty = Math.max(0, selectedDifficulty - 1);
            } else if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_DOWN)) {
                selectedDifficulty = Math.min(difficultyOptions.length - 1, selectedDifficulty + 1);
            } else if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_Z) || KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
                processDifficultyInput();
                Main.getInstance().getCountryList().get(0).setDifficulty(selectedDifficulty);
                Main.getInstance().setNews();
                System.out.println("Difficulty Set: " + Main.getInstance().getCountryList().get(0).difficulty);
            } else if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) {
                inDifficultyMenu = false;
            }
        }

        if (titleY < 100) titleY += 2;
        if (optionOffset > 0) optionOffset -= 2;

        float pulseSpeed = 0.05f;
        if (pulseGrowing) {
            pulse += pulseSpeed;
            if (pulse > 1.0f) {
                pulse = 1.0f;
                pulseGrowing = false;
            }
        } else {
            pulse -= pulseSpeed;
            if (pulse < 0.0f) {
                pulse = 0.0f;
                pulseGrowing = true;
            }
        }

        bgOffset += 0.5;
        if (bgOffset > Main.SCREEN_WIDTH) bgOffset = 0;
    }

    private void processMainMenuInput() {
        switch (selectedOption) {
            case 0: // NEW GAME
                inDifficultyMenu = true;
                break;
            case 1: // LOAD GAME
                isActive = false;
                break;
            case 2: // QUIT
                System.exit(0);
                break;
        }
    }

    private void processDifficultyInput() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean shouldLoadGame() {
        return !isActive && selectedOption == 1;
    }

    public boolean isHardMode() {
        return !isActive && selectedOption == 0 && selectedDifficulty == 1;
    }


    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw animated background pattern
        for (int x = (int) (bgOffset % 64) - 64; x < Main.SCREEN_WIDTH; x += 64) {
            for (int y = 0; y < Main.SCREEN_HEIGHT; y += 64) {
                g2d.drawImage(backgroundPattern, x, y, null);
            }
        }
        // --- MODIFIED: Dark green overlay for the background ---
        g2d.setColor(new Color(0, 30, 0, 180)); // Darker green, more opaque
        g2d.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);


        // Draw Logo or Title Text
        if (logo != null) {
            int logoX = (Main.SCREEN_WIDTH - logo.getWidth()) / 2;
            int logoY = (int) titleY - logo.getHeight() / 2 + 50;
            g2d.drawImage(logo, logoX, logoY, null);
        } else {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 72));
            String title = "ECONOMIA";
            int titleWidth = g2d.getFontMetrics().stringWidth(title);
            g2d.drawString(title, (Main.SCREEN_WIDTH - titleWidth) / 2, (int) titleY);
        }


        if (!inDifficultyMenu) {
            drawMainMenu(g2d);
        } else {
            drawDifficultyMenu(g2d);
        }

        drawFooter(g2d);
    }

    private void drawMainMenu(Graphics2D g2d) {
        drawOptions(g2d, mainOptions, selectedOption);
    }

    private void drawDifficultyMenu(Graphics2D g2d) {
        drawOptions(g2d, difficultyOptions, selectedDifficulty);
        // --- NEW: Draw Difficulty Description ---
        String description = difficultyDescriptions[selectedDifficulty];
        g2d.setColor(new Color(200, 255, 200, 220)); // Light green-white for text
        g2d.setFont(new Font("Arial", Font.ITALIC, 20)); // Adjust font size as needed
        FontMetrics metrics = g2d.getFontMetrics();
        int descWidth = metrics.stringWidth(description);
        // Position it below the buttons
        int startY = Main.SCREEN_HEIGHT / 2 - (difficultyOptions.length * 60 / 2) + 20;
        int buttonHeight = 60;
        int descY = startY + difficultyOptions.length * (buttonHeight + 15) + (int) optionOffset + 60; // 60 pixels below buttons
        g2d.drawString(description, (Main.SCREEN_WIDTH - descWidth) / 2, descY);
    }

    private void drawOptions(Graphics2D g2d, String[] options, int currentSelection) {
        int buttonWidth = 250;
        int buttonHeight = 60;
        int startY = Main.SCREEN_HEIGHT / 2 - (options.length * buttonHeight / 2) + 20;

        for (int i = 0; i < options.length; i++) {
            int buttonX = (Main.SCREEN_WIDTH - buttonWidth) / 2;
            int buttonY = startY + i * (buttonHeight + 15) + (int) optionOffset;

            boolean isSelected = (i == currentSelection);

            // 1. Draw Subtle Drop Shadow
            if (isSelected) {
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fill(new RoundRectangle2D.Float(buttonX + 5, buttonY + 5, buttonWidth, buttonHeight, 20, 20));
            }

            // 2. Draw Button with Gradient
            Color topColor = isSelected ? BUTTON_HOVER_TOP : BUTTON_COLOR_TOP;
            Color bottomColor = isSelected ? BUTTON_HOVER_BOTTOM : BUTTON_COLOR_BOTTOM;

            // Adjust gradient colors slightly for pulsing effect
            if (isSelected) {
                float pulseMagnitude = (float) (Math.sin(pulse * Math.PI));
                float adjustment = pulseMagnitude * 30; // Adjust max strength of pulse (e.g., +/- 30 RGB units)

                topColor = new Color(
                        Math.max(0, Math.min(255, (int) (topColor.getRed() + adjustment))),
                        Math.max(0, Math.min(255, (int) (topColor.getGreen() + adjustment))),
                        Math.max(0, Math.min(255, (int) (topColor.getBlue() + adjustment)))
                );
                bottomColor = new Color(
                        Math.max(0, Math.min(255, (int) (bottomColor.getRed() - adjustment))),
                        Math.max(0, Math.min(255, (int) (bottomColor.getGreen() - adjustment))),
                        Math.max(0, Math.min(255, (int) (bottomColor.getBlue() - adjustment)))
                );
            }


            GradientPaint gradient = new GradientPaint(
                    buttonX, buttonY, topColor,
                    buttonX, buttonY + buttonHeight, bottomColor);
            g2d.setPaint(gradient);
            g2d.fill(new RoundRectangle2D.Float(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20));

            // 3. Draw Border
            g2d.setColor(new Color(25, 25, 25, 200));
            g2d.draw(new RoundRectangle2D.Float(buttonX, buttonY, buttonWidth, buttonHeight, 20, 20));

            // 4. Draw Text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(options[i]);
            int textX = buttonX + (buttonWidth - textWidth) / 2;
            int textY = buttonY + ((buttonHeight - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.drawString(options[i], textX, textY);
        }

        // Back prompt for difficulty menu (now moved to drawDifficultyMenu)
        if (inDifficultyMenu) {
            g2d.setFont(new Font("Arial", Font.ITALIC, 16));
            g2d.setColor(new Color(150, 150, 150));
            String backPrompt = "Press ESC to go back";
            int promptWidth = g2d.getFontMetrics().stringWidth(backPrompt);
            // Position it below the description
            int startOfButtonsY = Main.SCREEN_HEIGHT / 2 - (options.length * buttonHeight / 2) + 20;
            int bottomOfButtonsY = startOfButtonsY + options.length * (buttonHeight + 15) + (int)optionOffset;
            int promptY = bottomOfButtonsY + 100; // 100 pixels below the buttons (to accommodate description)
            g2d.drawString(backPrompt, Main.SCREEN_WIDTH/2 - promptWidth/2, promptY);
        }
    }


    private void drawFooter(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(200, 200, 200, 150));
        g2d.drawString("", 20, Main.SCREEN_HEIGHT - 20);

        String version = this.version;
        int versionWidth = g2d.getFontMetrics().stringWidth(version);
        g2d.drawString(version, Main.SCREEN_WIDTH - versionWidth - 20, Main.SCREEN_HEIGHT - 20);
    }

    private BufferedImage createPatternTexture() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(255, 255, 255, 10)); // Very light, transparent white
        g2d.fillRect(0, 0, 64, 64);
        g2d.setColor(new Color(255, 255, 255, 20));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawLine(0, 0, 64, 64);
        g2d.drawLine(64, 0, 0, 64);
        g2d.dispose();
        return img;
    }
}