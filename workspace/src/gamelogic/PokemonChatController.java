package gamelogic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent; // Still needed for VK_ codes
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

import gameengine.loaders.FontLoader; // Ensure FontLoader is imported

public class PokemonChatController implements ActionListener {

    private Font pokemonFont;
    private String fullMessage = "";
    private List<String> wrappedLines = new ArrayList<>();
    private int charIndex = 0;
    private Timer typewriterTimer;
    private int charsPerTick = 7;
    private int delay = 50;

    private boolean typingComplete = false;
    private boolean chatBoxVisible = false;

    private List<String> options = new ArrayList<>();
    private int selectedOptionIndex = 0;
    private boolean showingOptions = false;

    private static final int TEXT_BOX_HEIGHT = 100;
    private static final int PADDING = 10;
    private static final int LINE_SPACING = 2;
    private static final int OPTION_PADDING_TOP = 20;
    private static final int OPTION_LINE_HEIGHT = 25;

    private int screenWidth;
    private int screenHeight;

    private List<ChatActionListener> chatActionListeners = new ArrayList<>();

    public PokemonChatController(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        //pokemonFont = FontLoader.loadFont(18f);
        pokemonFont = new Font("Verdana", 0, 15);
        typewriterTimer = new Timer(delay, this);
    }

    public void update(float tslf) {
        // Timer handles its own updates
    }

    public void draw(Graphics g) {
        if (!chatBoxVisible) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int currentTextBoxHeight = TEXT_BOX_HEIGHT;
        if (showingOptions && !options.isEmpty()) {
            currentTextBoxHeight += OPTION_PADDING_TOP + (options.size() * OPTION_LINE_HEIGHT);
        }

        int boxWidth = screenWidth - 40;
        int boxX = 20;
        int boxY = screenHeight - currentTextBoxHeight - 20;

        g2d.setColor(new Color(64, 64, 64));
        g2d.fillRect(boxX, boxY, boxWidth, currentTextBoxHeight);
        g2d.setColor(new Color(192, 192, 192));
        g2d.fillRect(boxX + 2, boxY + 2, boxWidth - 4, currentTextBoxHeight - 4);

        g2d.setFont(pokemonFont);
        g2d.setColor(Color.BLACK);

        FontMetrics fm = g2d.getFontMetrics(pokemonFont);
        int lineHeight = fm.getHeight() + LINE_SPACING;
        int currentY = boxY + PADDING + fm.getAscent();

        String displayedText = fullMessage.substring(0, charIndex);

        List<String> currentDisplayedLines = new ArrayList<>();
        String[] words = displayedText.split(" ");
        String lineBuffer = "";
        int tempMaxLineWidth = screenWidth - (2 * PADDING);

        for (String word : words) {
            if (fm.stringWidth(word) > tempMaxLineWidth) {
                if (!lineBuffer.isEmpty()) {
                    currentDisplayedLines.add(lineBuffer);
                    lineBuffer = "";
                }
                for (char c : word.toCharArray()) {
                    if (fm.stringWidth(lineBuffer + c) < tempMaxLineWidth) {
                        lineBuffer += c;
                    } else {
                        currentDisplayedLines.add(lineBuffer);
                        lineBuffer = String.valueOf(c);
                    }
                }
            } else if (fm.stringWidth(lineBuffer + (lineBuffer.isEmpty() ? "" : " ") + word) < tempMaxLineWidth) {
                if (!lineBuffer.isEmpty()) lineBuffer += " ";
                lineBuffer += word;
            } else {
                if (!lineBuffer.isEmpty()) currentDisplayedLines.add(lineBuffer);
                lineBuffer = word;
            }
        }
        if (!lineBuffer.isEmpty()) currentDisplayedLines.add(lineBuffer);


        for (int i = 0; i < currentDisplayedLines.size(); i++) {
            if (currentY + lineHeight > boxY + TEXT_BOX_HEIGHT - PADDING) {
                break;
            }
            g2d.drawString(currentDisplayedLines.get(i), boxX + PADDING, currentY);
            currentY += lineHeight;
        }


        if (showingOptions && typingComplete && !options.isEmpty()) {
            int optionY = boxY + TEXT_BOX_HEIGHT + OPTION_PADDING_TOP;

            for (int i = 0; i < options.size(); i++) {
                if (i == selectedOptionIndex) {
                    g2d.setColor(Color.BLUE);
                    int pointerX = boxX + PADDING + 5;
                    int pointerY = optionY + (OPTION_LINE_HEIGHT * i) + (fm.getAscent() / 2);
                    g2d.fillPolygon(new int[]{pointerX, pointerX + 5, pointerX},
                            new int[]{pointerY, pointerY - 5, pointerY + 5}, 3);
                } else {
                    g2d.setColor(Color.BLACK);
                }
                g2d.drawString(options.get(i), boxX + PADDING + 15, optionY + (OPTION_LINE_HEIGHT * i) + fm.getAscent());
            }
        }

        if (typingComplete && !showingOptions) {
            g2d.setColor(Color.BLACK);
            int indicatorX = boxX + boxWidth - PADDING - 15;
            int indicatorY = boxY + TEXT_BOX_HEIGHT - PADDING - 5;
            int indicatorSize = 8;
            g2d.fillPolygon(new int[]{indicatorX, indicatorX + indicatorSize, indicatorX},
                    new int[]{indicatorY, indicatorY, indicatorY + indicatorSize}, 3);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!typingComplete) {
            int targetCharIndex = charIndex + charsPerTick;
            if (targetCharIndex >= fullMessage.length()) {
                charIndex = fullMessage.length();
                typingComplete = true;
                if (!options.isEmpty()) {
                    showingOptions = true;
                    selectedOptionIndex = 0;
                }
                typewriterTimer.stop();
            } else {
                charIndex = targetCharIndex;
            }
        }
    }

    public void handleChatAdvanceOrOptionSelect(int keyCode) {
        if (!chatBoxVisible) return;

        if (showingOptions) {
            if (keyCode == KeyEvent.VK_Z || keyCode == KeyEvent.VK_ENTER) {
                // Store the selected option before hiding the chat box
                int chosenIndex = selectedOptionIndex;
                String chosenOptionText = "";
                // Added safety check to ensure index is within bounds before accessing
                if (chosenIndex >= 0 && chosenIndex < options.size()) {
                    chosenOptionText = options.get(chosenIndex);
                }

                hideChatBox(); // Now safe to hide and clear options

                for (ChatActionListener listener : chatActionListeners) {
                    listener.onOptionSelected(chosenIndex, chosenOptionText);
                }
            }
        } else { // This block is for advancing simple text without options
            if (keyCode == KeyEvent.VK_Z || keyCode == KeyEvent.VK_ENTER) {
                if (typingComplete) {
                    hideChatBox();
                    for (ChatActionListener listener : chatActionListeners) {
                        listener.onChatBoxAdvanced();
                    }
                } else {
                    charIndex = fullMessage.length();
                    typingComplete = true;
                    if (!options.isEmpty()) {
                        showingOptions = true;
                        selectedOptionIndex = 0;
                    }
                    typewriterTimer.stop();
                }
            }
        }
    }

    public void handleChatOptionNavigation(int keyCode) {
        if (!chatBoxVisible || !showingOptions) return;

        if (keyCode == KeyEvent.VK_UP) {
            selectedOptionIndex = Math.max(0, selectedOptionIndex - 1);
        } else if (keyCode == KeyEvent.VK_DOWN) {
            selectedOptionIndex = Math.min(options.size() - 1, selectedOptionIndex + 1);
        }
    }

    public void showMessage(String message) {
        showMessage(message, null);
    }

    public void showMessage(String message, List<String> optionsList) {
        this.fullMessage = message;
        this.options.clear();
        if (optionsList != null) {
            this.options.addAll(optionsList);
        }

        this.charIndex = 0;
        this.typingComplete = false;
        this.showingOptions = false;
        this.selectedOptionIndex = 0;
        this.chatBoxVisible = true;
        wrapText();
        typewriterTimer.start();
    }

    public void hideChatBox() {
        this.chatBoxVisible = false;
        this.typingComplete = false;
        this.showingOptions = false;
        this.options.clear();
        typewriterTimer.stop();
        for (ChatActionListener listener : chatActionListeners) {
            listener.onChatBoxHidden();
        }
    }

    public boolean isChatBoxActive() {
        return chatBoxVisible;
    }

    public boolean isActiveAndAwaitingPlayerAction() {
        return chatBoxVisible && (typingComplete || showingOptions);
    }

    private void wrapText() {
        wrappedLines.clear();
        Graphics tempG = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB).getGraphics();
        tempG.setFont(pokemonFont);
        FontMetrics fm = tempG.getFontMetrics();
        tempG.dispose();

        int maxLineWidth = screenWidth - (2 * PADDING);

        if (fm == null || maxLineWidth <= 0) {
            wrappedLines.add(fullMessage);
            return;
        }

        String[] words = fullMessage.split(" ");
        String currentLine = "";

        for (String word : words) {
            if (fm.stringWidth(word) > maxLineWidth) {
                if (!currentLine.isEmpty()) {
                    wrappedLines.add(currentLine);
                    currentLine = "";
                }
                for (char c : word.toCharArray()) {
                    if (fm.stringWidth(currentLine + c) < maxLineWidth) {
                        currentLine += c;
                    } else {
                        wrappedLines.add(currentLine);
                        currentLine = String.valueOf(c);
                    }
                }
            } else if (fm.stringWidth(currentLine + (currentLine.isEmpty() ? "" : " ") + word) < maxLineWidth) {
                if (!currentLine.isEmpty()) {
                    currentLine += " ";
                }
                currentLine += word;
            } else {
                if (!currentLine.isEmpty()) {
                    wrappedLines.add(currentLine);
                }
                currentLine = word;
            }
        }
        if (!currentLine.isEmpty()) {
            wrappedLines.add(currentLine);
        }
    }

    public interface ChatActionListener {
        void onChatBoxAdvanced();
        void onChatBoxHidden();
        void onOptionSelected(int selectedIndex, String selectedOptionText);
    }

    public void addChatActionListener(ChatActionListener listener) {
        chatActionListeners.add(listener);
    }

    public void removeChatActionListener(ChatActionListener listener) {
        chatActionListeners.remove(listener);
    }
}