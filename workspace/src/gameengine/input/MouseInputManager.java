package gameengine.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

import gameengine.graphics.MyWindow;

public final class MouseInputManager implements MouseInputListener, MouseWheelListener {

	private static boolean[] mousebutton = new boolean[5]; // Current physical state (raw from events)
	private static boolean[] justPressedThisFrame = new boolean[5]; // To track buttons just pressed
	private static float mouseX;
	private static float mouseY;

	private MyWindow window;

	public MouseInputManager(MyWindow window) {
		this.window = window;
	}

	// This method remains empty.
	public void update() {
		// No operation needed here.
	}

	// REMOVED: The clearJustPressedFlags() method is no longer needed
	// as isButtonJustPressed will now consume the flag.
	// public void clearJustPressedFlags() {
	//    for (int i = 0; i < justPressedThisFrame.length; i++) {
	//       justPressedThisFrame[i] = false;
	//    }
	// }

	public static boolean isButtonDown(int mouseButton) {
		if(mouseButton >= 0 && mouseButton < mousebutton.length && mousebutton[mouseButton]) return true;
		else return false;
	}

	// MODIFIED: This method now consumes the "just pressed" state
	public static boolean isButtonJustPressed(int mouseButton) {
		if (mouseButton >= 0 && mouseButton < justPressedThisFrame.length) {
			if (justPressedThisFrame[mouseButton]) {
				justPressedThisFrame[mouseButton] = false; // Consume the press
				//System.out.println("DEBUG: Inside isButtonJustPressed - Flag was true, now set to false for button " + mouseButton + " (at " + System.currentTimeMillis() + "ms)");
				return true;
			}
		}
		return false;
	}

	public static float getMouseX () {
		return mouseX;
	}
	public static float getMouseY () {
		return mouseY;
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		// Keep this empty
	}

	@Override
	public void mouseEntered(MouseEvent mouseEvent) {}
	@Override
	public void mouseExited(MouseEvent mouseEvent) {}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		int button = mouseEvent.getButton();
		if(button >= 0 && button < mousebutton.length) {
			mousebutton[button] = true; // Set current raw state
			justPressedThisFrame[button] = true; // Mark as just pressed for this frame
			System.out.println("DEBUG: Inside mousePressed - justPressedThisFrame[1] set to: " + justPressedThisFrame[MouseEvent.BUTTON1] + " (at " + System.currentTimeMillis() + "ms)");
		}
		//System.out.println("EVENT: mousePressed - Button " + button + " -> mousebutton[1]=" + mousebutton[MouseEvent.BUTTON1] + " (at " + System.currentTimeMillis() + "ms)");
	}

	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		int button = mouseEvent.getButton();
		if(button >= 0 && button < mousebutton.length) {
			mousebutton[button] = false; // Set current raw state
		}
		//System.out.println("EVENT: mouseReleased - Button " + button + " -> mousebutton[1]=" + mousebutton[MouseEvent.BUTTON1] + " (at " + System.currentTimeMillis() + "ms)");
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		mouseX = mouseEvent.getX() - window.getInsetX();
		mouseY = mouseEvent.getY() - window.getInsetY();
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
		mouseX = mouseEvent.getX() - window.getInsetX();
		mouseY = mouseEvent.getY() - window.getInsetY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {}
}