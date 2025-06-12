package gameengine.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import gameengine.input.KeyboardInputManager;
import gameengine.input.MouseInputManager;

public class TransitionManager {

    public enum TransitionState {
        IDLE,       // No transition happening
        FADING_IN,  // Screen is black, fading in to game
        FADING_OUT  // Game is visible, fading out to black
    }

    private TransitionState currentState = TransitionState.IDLE;
    private float alpha = 0.0f; // Current transparency (0.0f = fully transparent, 1.0f = fully opaque)
    private float fadeSpeed = 0.8f; // Speed of fading (alpha units per second)

    // Callbacks for when transitions complete
    private Runnable fadeInCompleteCallback;
    private Runnable fadeOutCompleteCallback;

    public TransitionManager() {
        // Default constructor
    }

    /**
     * Starts a fade-out transition (game visible -> black).
     * @param speed The speed of the fade (e.g., 1.0f for 1 second transition).
     * @param onComplete A Runnable to execute when the fade-out is complete (screen is fully black).
     */
    public void fadeOut(float speed, Runnable onComplete) {
        if (currentState != TransitionState.IDLE) return; // Prevent starting a new transition if one is active
        this.fadeSpeed = speed;
        this.alpha = 0.0f; // Start from transparent
        this.currentState = TransitionState.FADING_OUT;
        this.fadeOutCompleteCallback = onComplete;
    }

    /**
     * Starts a fade-in transition (black -> game visible).
     * @param speed The speed of the fade (e.g., 1.0f for 1 second transition).
     * @param onComplete A Runnable to execute when the fade-in is complete (screen is fully visible again).
     */
    public void fadeIn(float speed, Runnable onComplete) {
        if (currentState != TransitionState.IDLE) return; // Prevent starting a new transition if one is active
        this.fadeSpeed = speed;
        this.alpha = 1.0f; // Start from opaque
        this.currentState = TransitionState.FADING_IN;
        this.fadeInCompleteCallback = onComplete;
    }

    /**
     * Updates the transition state. Should be called every frame.
     * @param tslf Time since last frame in seconds.
     */
    public void update(float tslf) {
        if (currentState == TransitionState.FADING_OUT) {
            alpha += fadeSpeed * tslf;
            if (alpha >= 1.0f) {
                alpha = 1.0f; // Ensure it's fully opaque
                currentState = TransitionState.IDLE; // Transition complete
                if (fadeOutCompleteCallback != null) {
                    fadeOutCompleteCallback.run(); // Execute callback
                    fadeOutCompleteCallback = null; // Clear callback
                }
            }
        } else if (currentState == TransitionState.FADING_IN) {
            alpha -= fadeSpeed * tslf;
            if (alpha <= 0.0f) {
                alpha = 0.0f; // Ensure it's fully transparent
                currentState = TransitionState.IDLE; // Transition complete
                if (fadeInCompleteCallback != null) {
                    fadeInCompleteCallback.run(); // Execute callback
                    fadeInCompleteCallback = null; // Clear callback
                }
            }
        }
    }

    /**
     * Draws the black overlay. Should be called at the very end of your draw method.
     * @param g2d The Graphics2D object to draw on.
     * @param screenWidth The width of the game window.
     * @param screenHeight The height of the game window.
     */
    public void draw(Graphics2D g2d, int screenWidth, int screenHeight) {
        if (currentState == TransitionState.IDLE && alpha == 0.0f) {
            return; // Don't draw if not transitioning and fully transparent
        }

        // Set the composite to apply transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Draw a black rectangle covering the entire screen
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, screenWidth, screenHeight);

        // Reset the composite to default (important for subsequent drawings)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    public boolean isActive() {
        return currentState != TransitionState.IDLE;
    }

    public TransitionState getCurrentState() {
        return currentState;
    }

    // You might want to override input managers' methods to block input during transitions
    // For now, we'll control it in Main.java
}
