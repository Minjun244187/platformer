package gamelogic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import gameengine.GameBase;
import gameengine.graphics.MyWindow;
import gameengine.input.KeyboardInputManager;
import gameengine.loaders.LeveldataLoader;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;
import gamelogic.player.Inventory;
import gamelogic.player.Item;

public class Main extends GameBase implements PlayerDieListener, PlayerWinListener, ScreenTransitionListener, KeyListener{
	public static final int SCREEN_WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-200;
	public static final int SCREEN_HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-200;
	public static final boolean DEBUGGING = false;

	private ScreenTransition screenTransition = new ScreenTransition();

	private LevelData[] levels;
	private Level currentLevel;
	private int currentLevelIndex;
	private boolean active;
	
	private int numberOfTries;
	private long levelStartTime;
	private long levelFinishTime;
	
	private LevelCompleteBar levelCompleteBar;

	//inventory
	private boolean showInventory = false;
	private Inventory inventory = new Inventory(2);
    private final int SLOT_SIZE = 64, COLS = 5;
	public int screenWidth = 800;
    public int screenHeight = 600;
	

	public static void main(String[] args) {
		Main main = new Main();
		main.start("Economia", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	@Override
	public void init() {
		GameResources.load();

		currentLevelIndex = 0;

		levels = new LevelData[2];
		try {
			levels[0] = LeveldataLoader.loadLeveldata("/workspaces/platformer/workspace/maps/level.txt");
			levels[1] = LeveldataLoader.loadLeveldata("/workspaces/platformer/workspace/maps/map1.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentLevel = new Level(levels[currentLevelIndex]);

		currentLevel.addPlayerDieListener(this);
		currentLevel.addPlayerWinListener(this);

		screenTransition.addScreenTransitionListener(this);
		
		active = true;
		
		numberOfTries = 0;
		levelStartTime = System.currentTimeMillis();
		
		levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
	}
	
	//-----------------------------------------------------Screen Transition Listener
	@Override
	public void onTransitionActivationFinished() {
		if(currentLevel.isPlayerDead()) {
			currentLevel.restartLevel();
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
		}
		if(currentLevel.isPlayerWin()) {
			if(currentLevelIndex < levels.length-1) {
				changeLevel();
			}
		}
	}

	@Override
	public void onTransitionFinished() {
		active = true;
	}

	//-----------------------------------------------Player Listener
	@Override
	public void onPlayerDeath() {
		numberOfTries++;
		levelStartTime = System.currentTimeMillis();
		if(DEBUGGING && currentLevel.player.playerHP == 0) {
			currentLevel.restartLevel();
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
			return;
		}
		screenTransition.showLoseScreen(numberOfTries);
		
		active = false;
	}

	@Override
	public void onPlayerWin() {
		levelFinishTime = System.currentTimeMillis();
		screenTransition.showVictorySceen(levelFinishTime - levelStartTime);
		
		active = false;
	}

	private void changeLevel() {
		numberOfTries = 0;
		if(currentLevelIndex < levels.length-1) {
			currentLevelIndex++;
			currentLevel = new Level(levels[currentLevelIndex]);

			currentLevel.addPlayerDieListener(this);
			currentLevel.addPlayerWinListener(this);
			levelCompleteBar = new LevelCompleteBar(100, 10, SCREEN_WIDTH - 200, 10, currentLevel.getPlayer());
		}
	}

	@Override
	public void update(float tslf) {
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_N)) init();
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_ESCAPE)) System.exit(0);

		if (active) currentLevel.update(tslf);

		screenTransition.update(tslf);
		
		levelCompleteBar.update(tslf);
	}

	@Override
	public void draw(Graphics g) {
		long currentTime = System.currentTimeMillis();
		
		drawBackground(g);
		//Camera-translate
		currentLevel.draw(g);
		//- Camera-translate
		
		levelCompleteBar.draw(g);
		
		screenTransition.draw(g);

		if (currentLevel.player.inv) {
			drawInventory(g);
            
        }
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.GREEN);
		g.fillRect(0, 0-MyWindow.getInsetY(), SCREEN_WIDTH, SCREEN_HEIGHT+MyWindow.getInsetY()*2);
		g.setColor(Color.BLACK);
        g.drawString("Game Running - Press I to toggle inventory", 50, 50);

	}

	private void drawInventory(Graphics g) {
        int rows = (inventory.getMaxSlots() + COLS - 1) / COLS;
        int invWidth = COLS * SLOT_SIZE;
        int invHeight = rows * SLOT_SIZE;

        int startX = (screenWidth - invWidth) / 2;
        int startY = (screenHeight - invHeight) / 2;

        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, startY + 100, invWidth + 20, invHeight + 20);

        ArrayList<Item> items = inventory.getItems();
        for (int i = 0; i < inventory.getMaxSlots(); i++) {
            int row = i / COLS;
            int col = i % COLS;
            int x = 10 + col * SLOT_SIZE;
            int y = startY + 110 + row * SLOT_SIZE;

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);
            g.setColor(Color.BLACK);
            g.drawRect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);

            if (i < items.size()) {
                g.drawString(items.get(i).getName(), x + 5, y + SLOT_SIZE / 2);
            }
        }
    }

	@Override
    public void keyPressed(KeyEvent e) {
        char key = Character.toUpperCase(e.getKeyChar());

        
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

}
