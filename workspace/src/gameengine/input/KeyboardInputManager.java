package gameengine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class KeyboardInputManager implements KeyListener {

	private static boolean[] keys = new boolean[256]; // Use 256 for typical key codes (0-255 range)
	// or 65536 for full Unicode, but 256 is often enough
	// for standard keys. 1024 is also fine.
	private static boolean[] justPressedKeys = new boolean[keys.length];
	private static boolean[] lastKeys = new boolean[keys.length]; // To track state from previous frame
	private static int lastKeyCode = KeyEvent.CHAR_UNDEFINED; // Store the raw key code
	private static char lastCharTyped = KeyEvent.CHAR_UNDEFINED;

	// No public constructor. This is a singleton.
	private KeyboardInputManager() {}

	// Static instance
	private static KeyboardInputManager instance = new KeyboardInputManager();

	public static KeyboardInputManager getInstance() {
		return instance;
	}

	// --- IMPORTANT: Update the internal state for the start of a new frame ---
	// This method should be called once per game loop iteration (e.g., in GameBase.update() or Main.update())
	public static void update() {
		for (int i = 0; i < keys.length; i++) {
			justPressedKeys[i] = keys[i] && !lastKeys[i]; // Key is down NOW, but was UP last frame
			lastKeys[i] = keys[i]; // Store current state for next frame's 'lastKeys'
		}
	}

	// --- Corrected setKey method ---
	// This method should primarily be used internally by the KeyListener methods
	// It's not usually called externally.
	private static void setKeyInternal(int keyCode, boolean value) {
		if(keyCode >= 0 && keyCode < keys.length) {
			keys[keyCode] = value;
		}
	}

	public static boolean isKeyDown(int keyCode){
		if(keyCode >= 0 && keyCode < keys.length) {
			return keys[keyCode]; // Just return the stored state
		}
		return false;
	}

	// --- NEW: Check if a key was just pressed this frame ---
	public static boolean isKeyJustPressed(int keyCode) {
		if(keyCode >= 0 && keyCode < justPressedKeys.length) {
			return justPressedKeys[keyCode];
		}
		return false;
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		// Only set to true. keyReleased will set to false.
		// No need to worry about 'justPressedKeys' here, 'update()' handles it.
		setKeyInternal(keyCode, true);
		// We're not consuming the event here, so other listeners *could* still get it if they were added.
		// However, the design now intends for this to be the *only* KeyListener.
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		setKeyInternal(keyCode, false);
	}

	@Override
	public void keyTyped(KeyEvent keyEvent) {
		// Typically, you don't track key 'down' states with keyTyped.
		// If you need char input (e.g., for a text field), you'd handle e.getKeyChar() here.
		// For simple game input, this method is often left empty or used for direct character input.
		// We will NOT set keys to false here.
		// For chat input, you might use the char.
		// But for now, leave it empty as it was incorrectly setting keys to false.
	}
	public static int getLastKeyPressed() {
		int code = lastKeyCode;
		lastKeyCode = KeyEvent.CHAR_UNDEFINED; // Reset after reading to prevent continuous detection
		return code;
	}

	public static char getLastKeyTyped() {
		char character = lastCharTyped;
		lastCharTyped = KeyEvent.CHAR_UNDEFINED; // Reset after reading
		return character;
	}
}