package gamelogic.world;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import gameengine.hitbox.RectHitbox;
import gameengine.maths.Vector2D;
import gamelogic.player.Player;
import gamelogic.player.Item; // Assuming Item class is in gamelogic.player

public class ShopNPC {

    private Vector2D position;
    private int width, height;
    private BufferedImage image;
    private RectHitbox hitbox;
    private ShopCategoryGUI shopGUI;
    private BankGUI bankGUI;// The GUI for this shop
    private boolean shopOpen = false;
    private boolean solid = true; // Add this field
    private boolean collidable = false;// To track if this specific shop is open
    private DialogueTriggerListener dialogueListener;
    private String dialogueId;
    private boolean bankOpen = false;// Field to store the ID of the dialogue this NPC triggers
    private long lastInteractionTime = 0; // Cooldown to prevent rapid triggers
    private final long INTERACTION_COOLDOWN = 300;

    // Constructor
    public ShopNPC(float x, float y, int width, int height, BufferedImage image,
                   String shopTitle, List<Item> itemsForSale, String dialogueId, DialogueTriggerListener listener) {
        this.position = new Vector2D(x, y);
        this.width = width;
        this.height = height;
        this.image = image;
        // Hitbox for interaction, slightly larger than the image for easier collision
        this.hitbox = new RectHitbox(position.x + 50, position.y + 90, 0, 0, width / 3, height / 3);

        // Initialize the GUIs associated with this NPC
        this.shopGUI = new ShopCategoryGUI(shopTitle, itemsForSale);
        this.bankGUI = new BankGUI();
        this.dialogueId = dialogueId; // Assign dialogueId from constructor
        this.dialogueListener = listener; // Assign dialogueListener from constructor
    }

    // Method to check for player interaction
    public void update(Player player, boolean interactKeyPressed) {

    }

    /**
     * Draws the ShopNPC and its associated shop GUI if open.
     * @param g The Graphics object to draw on.
     * @param player The Player object, used for rendering player-specific info in the shop GUI.
     */
    public void draw(Graphics g, Player player, int mouseX, int mouseY, Graphics2D g2d) { // MODIFIED: Added Player parameter
        // Draw the NPC image relative to camera
        g.drawImage(image, (int)position.x, (int)position.y, width, height, null);
        // Optional: Draw hitbox for debugging
        if (hitbox != null) hitbox.draw(g);

    }

    public void openShop() {
        shopOpen = true;
        shopGUI.open(); // Call the shopGUI's open method
        System.out.println("Shop opened: " + shopGUI.getTitle());
        // Potentially pause player movement or game updates here
    }

    public void closeShop() {
        shopOpen = false;
        shopGUI.close(); // Call the shopGUI's close method
        System.out.println("Shop closed: " + shopGUI.getTitle());
        // Potentially resume player movement or game updates here
    }

    public boolean isShopOpen() {
        return shopOpen;
    }

    public ShopCategoryGUI getShopGUI() {
        return shopGUI;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public RectHitbox getHitbox() {
        return this.hitbox;
    }

    public String getDialogueNodeId() {
        return this.dialogueId;
    }

    public void handleMouseClick(int x, int y, int button, Player player) {
    }

    public void openBank() {
        if (!bankOpen || !bankGUI.isOpen()) {
            bankOpen = true;
            bankGUI.open();
            System.out.println("Bank opened!");
        }
    }

    public void closeBank() {
        if (bankOpen || bankGUI.isOpen()) {
            bankOpen = false;
            bankGUI.close();
            System.out.println("Bank closed!");
        }
    }

    public boolean isBankOpen() {
        return bankOpen;
    }

    public BankGUI getBankGUI() {
        return bankGUI;
    }
}