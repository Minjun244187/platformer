package gamelogic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import gameengine.graphics.MyGraphics;
import gameengine.graphics.MyWindow;
import gameengine.maths.Vector2D;

public class ScreenTransition {

	private boolean isActive = false;
	private boolean isActivating = false;
	private boolean isDeactivating = false;
	private Vector2D position;
	private int width;
	private int height;
	private float velocity = Main.SCREEN_WIDTH * 1.5f;

	private String text[];
	private Rectangle textBox[];
	private Font font = new Font("Arial", Font.BOLD, Main.SCREEN_WIDTH/10);

	private List<ScreenTransitionListener> listeners = new ArrayList<>();

	public ScreenTransition() {
		this.position = new Vector2D(-Main.SCREEN_WIDTH, 0);
		this.width = Main.SCREEN_WIDTH;
		this.height = Main.SCREEN_HEIGHT;

		text = new String[2];
		// Initialize text array elements to empty strings to prevent NullPointerException
		text[0] = ""; //
		text[1] = ""; //

		textBox = new Rectangle[2];
		textBox[0] = new Rectangle(MyWindow.getInsetX(), MyWindow.getInsetY()+20, Main.SCREEN_WIDTH, 200);
		textBox[1] = new Rectangle(MyWindow.getInsetX(), MyWindow.getInsetY()+120, Main.SCREEN_WIDTH, 200); // Changed from Main.SCREEN_HEIGHT/2 for better positioning
	}

	public void update(float tslf) {
		if (isActive) {
			if (isActivating) {
				position.x += velocity * tslf;
				if (position.x >= 0) {
					position.x = 0;
					isActivating = false;
					throwTransitionActivationFinishedEvent();
				}
			} else if (isDeactivating) {
				position.x += velocity * tslf;
				if (position.x >= Main.SCREEN_WIDTH) {
					position.x = Main.SCREEN_WIDTH; // Ensure it moves fully off-screen
					isDeactivating = false;
					isActive = false;
					throwTransitionFinishedEvent();
				}
			}
		}
	}

	public void draw(Graphics g) {
		if (isActive) {
			g.translate((int)position.x, (int)position.y);

			// Background fill
			g.setColor(Color.BLACK);
			// Ensure the fill covers the entire screen, accounting for position translation
			g.fillRect((int)-position.x, (int)-position.y, (int)width, (int)height+MyWindow.getInsetY()*2); //

			if(text != null) {
				g.setColor(Color.WHITE);

				for (int i = 0; i < text.length; i++) {
					MyGraphics.drawCenteredString(g, text[i], textBox[i], font); //
				}
			}

			g.translate((int)-position.x, (int)-position.y);
		}
	}

	public void activate() {
		position.x = -Main.SCREEN_WIDTH;
		isActive = true;
		isActivating = true;
		isDeactivating = false;
	}

	public void deactivate() {
		// Correct initial position for deactivation to move from current visible state
		// If it's already on screen (position.x == 0), then it should start moving from 0.
		// If it's somewhere else, it might have been partially deactivated or activated.
		// To ensure it always slides out, set its starting point for deactivation.
		// It should start from its current visible position (which should be 0 when active).
		// Then, set velocity to positive to move it off-screen to the right.
		position.x = 0; // Start moving from the current visible position
		isDeactivating = true;
		isActivating = false; // Ensure activating flag is false
	}

	public void showLoadingScreen() {
		text[0] = "LOADING"; //
		text[1] = "Please wait..."; //
		activate();
	}

	public void showLoseScreen(int numberOfTries) {
		text[0] = "Loading"; //
		activate();
	}

	public void showVictorySceen(float finishTime) {
		text[0] = "Loading"; //
		activate();
	}

	//------------------------Listener
	public void throwTransitionActivationFinishedEvent() {
		for (ScreenTransitionListener screenTransitionListener : listeners) {
			screenTransitionListener.onTransitionActivationFinished(); //
		}
	}

	public void throwTransitionFinishedEvent() {
		for (ScreenTransitionListener screenTransitionListener : listeners) {
			screenTransitionListener.onTransitionFinished(); //
		}
	}

	public void addScreenTransitionListener(ScreenTransitionListener listener) {
		listeners.add(listener);
	}

	public void removeScreenTransitionListener(ScreenTransitionListener listener) {
		listeners.remove(listener);
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isActivating() {
		return isActivating;
	}

	public boolean isDeactivating() {
		return isDeactivating;
	}
}