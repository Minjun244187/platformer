package gameengine;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import gameengine.graphics.MyWindow;
import gameengine.input.KeyboardInputManager;
import gameengine.input.MouseInputManager;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent; // Ensure this import is here

public abstract class GameBase{
	protected MyWindow window;
	protected MouseInputManager mouseInputManager;

	public abstract void init();
	public abstract void update(float tslf);
	public abstract void draw(Graphics graphics);

	public void start(String title, int width, int height) {
		window = new MyWindow(title, width, height);

		window.addKeyListener(KeyboardInputManager.getInstance());

		this.mouseInputManager = new MouseInputManager(window);
		window.addMouseListener(this.mouseInputManager);
		window.addMouseMotionListener(this.mouseInputManager);
		window.addMouseWheelListener(this.mouseInputManager);

		window.setFocusable(true);
		window.requestFocusInWindow();

		long StartOfInit = System.currentTimeMillis();
		init();
		long StartOfGame = System.currentTimeMillis();
		System.out.println("Time needed for initialization: [" + (StartOfGame - StartOfInit) + "ms]");

		long lastFrame = System.currentTimeMillis();

		while(true) {
			lastFrame = System.currentTimeMillis();
			while(window.isShowing()) {
				long thisFrame = System.currentTimeMillis();
				float tslf = (float)(thisFrame - lastFrame) / 1000f;
				lastFrame = thisFrame;

				// --- Input Processing Order ---
				KeyboardInputManager.update();
				this.mouseInputManager.update(); // Call update (now empty)

				// Current frame state checks (Main.update() is called here)
				//System.out.println("FRAME: " + System.currentTimeMillis() + "ms - START of frame (before Main.update())");
				//System.out.println("  Raw mousebutton[1]: " + MouseInputManager.isButtonDown(MouseEvent.BUTTON1));
				// Removed prevMousebutton print as it's no longer used for justPressed
				//System.out.println("  isButtonJustPressed[1]: " + MouseInputManager.isButtonJustPressed(MouseEvent.BUTTON1));

				update(tslf); // Calling Main.update()

				//System.out.println("FRAME: " + System.currentTimeMillis() + "ms - END of frame (after Main.update())");
				//System.out.println("  Raw mousebutton[1]: " + MouseInputManager.isButtonDown(MouseEvent.BUTTON1));
				//System.out.println("  isButtonJustPressed[1]: " + MouseInputManager.isButtonJustPressed(MouseEvent.BUTTON1)); // Will be true if it was just pressed

				// --- Drawing ---
				BufferStrategy bs = window.beginDrawing();
				do{
					do{
						Graphics g = bs.getDrawGraphics();
						g.translate(window.getInsetX(), window.getInsetY());
						draw(g);
						g.dispose();
					}while(bs.contentsLost());
					bs.show();
				}while(bs.contentsLost());

				// NEW: Clear justPressed flags at the very end of the frame
				// This ensures isButtonJustPressed is true for one full frame before being reset.
			}
			if (!window.isShowing()) {
				System.exit(0);
			}
		}
	}

	public MouseInputManager getMouseInputManager() {
		return this.mouseInputManager;
	}
}