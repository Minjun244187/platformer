package gamelogic;

import gameengine.graphics.MyGraphics;
import gameengine.graphics.TransitionManager;
import gameengine.maths.Vector2D;
import gamelogic.player.Player;
import gamelogic.world.*;
import gamelogic.audio.SoundManager;
import gamelogic.world.BankGUI;


import java.awt.*;
import java.awt.event.KeyEvent; // Still needed for VK_ codes
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
// Import for mouse events
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.awt.geom.RoundRectangle2D; // For rounded rectangles

import javax.swing.SwingUtilities;

import gameengine.GameBase;
import gameengine.graphics.MyWindow;
import gameengine.input.KeyboardInputManager;
import gameengine.input.MouseInputManager;
// Import the manager
import gameengine.loaders.LeveldataLoader;
import gamelogic.level.Level;
import gamelogic.level.LevelData;
import gamelogic.level.PlayerDieListener;
import gamelogic.level.PlayerWinListener;
import gamelogic.player.Inventory;
import gamelogic.player.Item;
import gameengine.loaders.FontLoader;
import gamelogic.TimeDisplayGUI;
import org.w3c.dom.ls.LSOutput;
import gamelogic.world.business.Business;
import gamelogic.world.business.ProductionMarketBusiness;
import gamelogic.world.business.AirportBusiness;
import gamelogic.world.business.ProductType;

import static gamelogic.level.Level.player;


// --- REMOVE 'implements KeyListener' ---
public class Main extends GameBase implements PlayerDieListener, PlayerWinListener, ScreenTransitionListener, PokemonChatController.ChatActionListener, DialogueTriggerListener {
	//public static final int SCREEN_WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()-200;
	//public static final int SCREEN_HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()-200;
	public static final boolean DEBUGGING = false;
	public static final int SCREEN_WIDTH = 1080;
	public static final int SCREEN_HEIGHT = 600;


	private TitleScreen titleScreen;
	public static int playerHP = 1000;
	public static double purse = 100.0;
	public static double balance;
	public static int luck = 0;
	public static int satisfiction = 50; //in percentage
	public static int intelligence = 0;
	public static int knowledge;
	public static String job = "UNEMPLOYED";
	public static long lasInteractionTime = 0;

	private ScreenTransition screenTransition = new ScreenTransition();

	private static LevelData[] levels;
	private static Level currentLevel;
	private static int currentLevelIndex;
	private boolean active;
	public static double extraProductionCost;
	public static double goldCost;
	private List<Product> launchedProducts;

	public String lastPurchasedItem;
	public Double lastPurchasedCost;


	private int numberOfTries;
	private long levelStartTime;
	private long levelFinishTime;

	private int mouseX = 0;
	private int mouseY = 0;

	private static Main instance;
	private Item hoveredItem = null;
	private String inventoryMessage = null;
	private long messageDisplayEndTime = 0;
	private static final long MESSAGE_DURATION = 2000;

	public static final long chatCoolDown = 350;
	public static long lastChatTriggerTime;


	private int inventoryX = -300; // Start off-screen to the left
	private int targetInventoryX = 0; // Final X position
	private boolean inventoryVisible = false;
	private boolean inventoryAnimating = false;
	private long lastInventoryToggleTime = 0;
	private final long inventoryToggleCooldown = 400;
	public static boolean isDead;

	private int statY = 700; // Start off-screen to the bottom
	private int targetStatY = 300; // Final Y position
	private boolean statVisible = false;
	private boolean statAnimating = false;
	private long lastStatToggleTime = 0;
	private final long statToggleCooldown = 350;
	private long lastNewsToggleTime = 0;
	private final long NewsToggleCooldown = 300;
	private Map<String, Boolean> triggeredDialogues;
	private boolean initialDialogueTriggered = false;
	public static double monthlyIncome;
	public static double moneySpent;
	public static double incomeTax;
	public static double debt;
	public static double houseRent;
	public static int workChance;
	public static boolean bankrupted;
	public static List<Double> historicalInterestRates;
	public List<Double> historicalTaxRates;
	public List<Double> historicalSpending;
	public List<Double> historicalGrowthRate;
	public List<Integer> historicalPopRate;
	private static Font timesNewRomanFont;

	public boolean fireArm = false;





	public TurnReportGUI turnReportGUI;

	public NewspaperGUI newspaperGUI;

	public ComputerScreenGUI computerScreenGUI;

	public GovernmentPortalGUI governmentPortalGUI;

	private TransitionManager transitionManager;

	public BusinessGUI businessGUI;

	// Business Variables (NEW / MODIFIED)
	public static boolean ownsBusiness = false; // Whether the player owns a business - now derived from playerBusiness != null
	public static String businessType = null; // "Production Market" or "Airport" - now derived from playerBusiness.getType()
	public Business playerBusiness = null; // To hold the actual business object
	public Economy economy;



	private SoundManager soundManager;

	private static final String SAVE_FILE = "economia_save.dat";

	public ShopNPC lastInteractedShopNPC = null;

	public static boolean isSleeping;
	private boolean showInventory = false;
	private static int maxSlots = 2;
	private static Inventory inventory = new Inventory(maxSlots);

	private final int SLOT_SIZE = 64, COLS = 5;
	private KeyboardInputManager keyboardInputManager;

	private PokemonChatController chatController;
	private boolean gameLogicPausedForChat = false;
	private static boolean isInitialized = false;
	public static boolean mapInitialized = false;
	public static boolean inBuilding;
	public static Vector2D playerLastVector;
	public static boolean finalBattle;

	//countries
	private country fLand = new country(
			"Rodmania", 20000000, 1400000000000.0, 3.0, 5.0);

	private static ArrayList<country> countryList = new ArrayList<>();

	private DialogueManager dialogueManager;
	private String currentDialogueNodeId = null;

	private TimeDisplayGUI timeDisplayGUI;

	public static void main(String[] args) {
		Main main = new Main();
		main.start("Economia", SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public Main() {
		instance = this;
	}

	public static Inventory getInventory() {
		return inventory;
	}

	public static String hasWorkChance() {
		if (workChance > 0) {
			System.out.println("yes");
			return "true";

		} else {
			System.out.println("no");
			return "false";
		}
	}

	public static Font getFont() {
		return Main.getInstance().window.getFont();
	}

	public static void payLoan(double amount) {
		if (purse >= amount) {
			debt -= amount;
			purse -= amount;
		} else if (balance >= amount) {
			debt -= amount;
			balance -= amount;
		}

	}

	public static void takeLoan(double amount) {
		debt += amount;
		balance += amount;

	}

	public void equipFireArm() {
		fireArm = true;
	}

	public static void startFinalBattle() {
		finalBattle = true;
		getInstance().jukeBox();
		getInstance().startDialogue("war_q");
	}


	@Override
	public void init() {
		System.out.println(SCREEN_WIDTH);
		System.out.println(SCREEN_HEIGHT);
		isInitialized = false;
		mapInitialized = false;
		GameResources.load(); // Load resources at the very beginning
		titleScreen = new TitleScreen();
		transitionManager = new TransitionManager();
		screenTransition.addScreenTransitionListener(this);


		// Show initial loading transition if needed
		if (!titleScreen.isActive()) {
			screenTransition.showLoadingScreen();
		}
		triggeredDialogues = new HashMap<>();
		dialogueManager = new DialogueManager();
		launchedProducts = new ArrayList<>();
		currentLevelIndex = 0;

		levels = new LevelData[9];
		try {
			levels[0] = LeveldataLoader.loadLeveldata("maps/level.txt");
			levels[1] = LeveldataLoader.loadLeveldata("maps/house.txt");
			levels[2] = LeveldataLoader.loadLeveldata("maps/conv.txt");
			levels[3] = LeveldataLoader.loadLeveldata("maps/bookStore.txt");
			levels[4] = LeveldataLoader.loadLeveldata("maps/mcdon.txt");
			levels[5] = LeveldataLoader.loadLeveldata("maps/hospital.txt");
			levels[6] = LeveldataLoader.loadLeveldata("maps/bank.txt");
			levels[7] = LeveldataLoader.loadLeveldata("maps/school.txt");
			levels[8] = LeveldataLoader.loadLeveldata("maps/map1.txt");

		} catch (Exception e) {
			e.printStackTrace();
		}
		currentLevel = new Level(levels[currentLevelIndex], this);

		currentLevel.addPlayerDieListener(this);
		currentLevel.addPlayerWinListener(this);

		screenTransition.addScreenTransitionListener(this);

		active = true;
		gameLogicPausedForChat = false;

		numberOfTries = 0;
		isSleeping = false;
		isDead = false;
		levelStartTime = System.currentTimeMillis();

		chatController = new PokemonChatController(SCREEN_WIDTH, SCREEN_HEIGHT);
		chatController.addChatActionListener(this);

		turnReportGUI = new TurnReportGUI(currentLevel.getPlayer());
		computerScreenGUI = new gamelogic.world.ComputerScreenGUI();
		governmentPortalGUI = new GovernmentPortalGUI();
		businessGUI = new BusinessGUI();
		economy = new Economy();

		try {
			// Attempt to create a Font object for "Times New Roman"
			// You can specify style (PLAIN, BOLD, ITALIC) and initial size
			timesNewRomanFont = new Font("Times New Roman", Font.PLAIN, 12);

			// OPTIONAL: Verify if "Times New Roman" was actually loaded.
			// If the system doesn't have it, Java might silently substitute a default font.
			// Check if the font family name matches what we requested.
			if (!timesNewRomanFont.getFamily().equals("Times New Roman")) {
				System.out.println("Warning: 'Times New Roman' not found on system. Falling back to a generic serif font.");
				timesNewRomanFont = new Font(Font.SERIF, Font.PLAIN, 12); // Fallback
			}
		} catch (Exception e) {
			System.err.println("Error initializing Times New Roman font: " + e.getMessage());
			timesNewRomanFont = new Font(Font.SERIF, Font.PLAIN, 12); // Fallback on error
		}

		newspaperGUI = new NewspaperGUI();
		this.keyboardInputManager = KeyboardInputManager.getInstance();



		//initialize countries
		countryList.add(fLand);
		countryList.add(new country("DARP Republic", 30000000, 2100000000000.0, 3.0, 1.0));
		countryList.add(new country("Tim Republic", 50000000, 4000000000000.0, 1.5, 3.5));
		countryList.add(new country("Greg Empire", 2000000, 1111111111115.0, 6.0, 2.0));
		countryList.add(new country("College Board Empire", 100000000, 9000000000000.0, 4.0, 4.0));



		//sounds
		soundManager = new SoundManager();
		soundManager.loadClip("coin", "music/coin.wav");
		soundManager.loadClip("money", "music/moneydrop-1989.wav");
		soundManager.loadClip("eat", "music/eat.wav");
		soundManager.loadClip("bgm", "music/preubenlied.wav");
		soundManager.loadClip("war", "music/warmarch.wav");
		soundManager.loadClip("sad", "music/dietotenerwachen.wav");
		soundManager.loadClip("yap", "music/yap.wav");
		soundManager.loadClip("tada", "music/tada.wav");
		soundManager.loadClip("paper", "music/paper.wav");
		soundManager.loadClip("frown", "music/frown.wav");
		soundManager.loadClip("warcry", "music/warcry.wav");
		soundManager.loadClip("siren", "music/airraid.wav");
		soundManager.loadClip("bass", "music/bassitem-3144.wav");
		soundManager.loadClip("dead", "music/dead.wav");

		this.timeDisplayGUI = new TimeDisplayGUI(1100, 80, countryList.get(0));

		isInitialized = true;
		mapInitialized = true;
		workChance = 5;
		bankrupted = false;
		gameLogicPausedForChat = false;
		jukeBox();

	}
	public void setNews() {
		if (countryList.get(0).difficulty == 1) {
			countryList.get(0).hardNews();
		} else {
			countryList.get(0).initialNews();
		}
	}

	public NewspaperGUI getNewspaperGUI() {
		return this.newspaperGUI;
	}

	public void openComputa() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNewsToggleTime >= NewsToggleCooldown) {
			soundManager.play("bass");
			computerScreenGUI.open();
			lastNewsToggleTime = currentTime;

		}
	}
	public void openBusinessGUI() {
		if (businessGUI != null) {
			businessGUI.open();
			// Optional: Add dialogue for business GUI opening
			// startDialogue("business_access");
		}
	}

	public boolean hasBusiness() {
		return playerBusiness != null;
	}

	public Business getPlayerBusiness() {
		return playerBusiness;
	}

	public void setPlayerBusiness(Business business) {
		this.playerBusiness = business;
	}

	public void openNews() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastNewsToggleTime >= NewsToggleCooldown) {
			soundManager.play("paper");
			newspaperGUI.open(countryList.get(0).getCountryNews(), TimeDisplayGUI.getCalender(countryList.get(0).getTime()));
			lastNewsToggleTime = currentTime;
			String id = countryList.get(0).getCountryNews().get(0).getID();
			if (id.equals("warDeclare")) {
				soundManager.play("war");
			} else if (id.equals("aboutWar")) {
				soundManager.play("frown");
			} else if (id.equals("forestBurn")) {
				soundManager.play("frown");
			} else if (id.equals("invasion")) {
				soundManager.play("siren");
			} else if (id.equals("bombing")) {
				soundManager.play("siren");
			} else if (id.equals("forward")) {
				soundManager.play("war");
			} else if (id.equals("backward")) {
				soundManager.play("frown");
			}
		}

	}

	public void startDialogue(String nodeId) {

		long currentTime = System.currentTimeMillis();
		if (chatController != null) {
			chatController.hideChatBox();

		}
		if (currentTime - lastChatTriggerTime < chatCoolDown) {

		}

		DialogueNode node = dialogueManager.getDialogueNode(nodeId);

		if (node != null) {
			currentDialogueNodeId = nodeId;
			gameLogicPausedForChat = true;

			// Execute on-enter action if available
			if (node.getOnEnterAction() != null) {
				// Pass 'this' (Main instance) as context for the action
				node.getOnEnterAction().accept(this);
			}

			if (node.hasOptions()) {
				chatController.showMessage(node.getMessage(), node.getOptions());
			} else {
				chatController.showMessage(node.getMessage());
			}
		} else {
			System.err.println("DialogueNode not found with ID: " + nodeId + ". Hiding chat.");
			chatController.hideChatBox(); // Node not found, hide chat
			currentDialogueNodeId = null; // Clear current dialogue ID
			gameLogicPausedForChat = false; // Ensure game resumes
		}

	}

	public ArrayList<country> getCountryList() { return countryList; }


	@Override
	public void onTransitionActivationFinished() {
		if (titleScreen != null && !titleScreen.isActive()) {
			// This is when transitioning from title screen to game
			handlePostTransitionLoading();
			screenTransition.deactivate(); // Start fading out the transition
		} else if (currentLevel.isPlayerWin()) {
			if (currentLevelIndex < levels.length-1) {
				changeLevel();
			}
		}
	}

	public Level getCurrentLevel() { return currentLevel; }

	@Override
	public void onTransitionFinished() {
		active = true;
		if (!initialDialogueTriggered) {
			startDialogue("intro_start"); // Your initial dialogue ID
			initialDialogueTriggered = true;
		}
	}

	public void onTriggerDialogue(String dialogueNodeId, ShopNPC triggeringNPC) { // UPDATED signature
		if (!gameLogicPausedForChat) {
			// Set the lastInteractedShopNPC right when the dialogue is triggered
			this.lastInteractedShopNPC = triggeringNPC;
			System.out.println("Main: Setting lastInteractedShopNPC to " + triggeringNPC.getClass().getSimpleName());
			startDialogue(dialogueNodeId);
		}
	}

	@Override
	public void onPlayerDeath() {
		numberOfTries++;
		levelStartTime = System.currentTimeMillis();
		if(DEBUGGING && player.playerHP == 0) {
			currentLevel.restartLevel();
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
	public static void startLoading() {
		if (instance != null) {
			instance.isInitialized = false;
			instance.mapInitialized = false;
			System.out.println("Loading screen activated.");
		}
	}
	public static void finishLoading() {
		if (instance != null) {
			instance.isInitialized = true;
			instance.mapInitialized = true;
			System.out.println("Loading screen deactivated. Game ready.");
		}
	}

	public static void setLevel(int index) {
		currentLevel.clearBuildings();
		currentLevelIndex = index;
		currentLevel = new Level(levels[index], Main.getInstance());
		currentLevel.clearDroppedItems();
	}

	public KeyboardInputManager getKeyboardInputManager() {
		return keyboardInputManager;
	}


	public static Main getInstance() {
		return instance;
	}

	public static int getCurrentLevelIndex() { return currentLevelIndex; }

	private void changeLevel() {
		numberOfTries = 0;
		if(currentLevelIndex < levels.length-1) {
			currentLevelIndex++;
			currentLevel = new Level(levels[currentLevelIndex], this);
			currentLevel.clearDroppedItems();


			currentLevel.addPlayerDieListener(this);
			currentLevel.addPlayerWinListener(this);
		}
	}

	@Override
	public void update(float tslf) {
		if (titleScreen != null && titleScreen.isActive()) {
			titleScreen.update();
			if (!titleScreen.isActive()) {
				screenTransition.activate(); // Start transition
				return;
			}
			return;
		}

		if (!isInitialized) {
			return;
		}

		chatController.update(tslf);

		transitionManager.update(tslf);

		bankrupted = countryList.get(0).bankrupted;
		historicalInterestRates = countryList.get(0).historicalInterestRates;
		historicalTaxRates = countryList.get(0).historicalTaxRates;
		historicalSpending = countryList.get(0).historicalSpending;
		historicalGrowthRate = countryList.get(0).historicalGrowthRate;
		historicalPopRate = countryList.get(0).historicalPopRate;



		// Determine if game logic should be paused due to any GUI being open
		      // The `isGameLogicPaused()` method (formerly `isGameLogicPausedForChat`)
				       // now correctly combines all GUI states.
		boolean gameIsPaused = isGameLogicPausedForChat(); // Call the updated method

		if (gameIsPaused) {
			long currentTime = System.currentTimeMillis();
			// If any GUI is active, only handle GUI-specific input
			if (chatController.isActiveAndAwaitingPlayerAction()) {
				             // Only handle chat-specific input if chat is the active reason for pausing
						             if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_Z) || KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
					                if (currentTime - lastChatTriggerTime >= chatCoolDown) {
										chatController.handleChatAdvanceOrOptionSelect(KeyEvent.VK_Z);
										lastChatTriggerTime = currentTime;
									}
					             }
									 if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_UP)) {
					                chatController.handleChatOptionNavigation(KeyEvent.VK_UP);
							 }
				             if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_DOWN)) {
					                chatController.handleChatOptionNavigation(KeyEvent.VK_DOWN);
							 }
			}



			if (lastInteractedShopNPC != null && lastInteractedShopNPC.isShopOpen()) {
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_UP)) {
					lastInteractedShopNPC.getShopGUI().handleKeyPress(KeyEvent.VK_UP, currentLevel.getPlayer());
				}
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_DOWN)) {
					lastInteractedShopNPC.getShopGUI().handleKeyPress(KeyEvent.VK_DOWN, currentLevel.getPlayer());
				}
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_LEFT)) {
					lastInteractedShopNPC.getShopGUI().handleKeyPress(KeyEvent.VK_LEFT, currentLevel.getPlayer());
				}
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_RIGHT)) {
					lastInteractedShopNPC.getShopGUI().handleKeyPress(KeyEvent.VK_RIGHT, currentLevel.getPlayer());
				}
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) {
					lastInteractedShopNPC.getShopGUI().handleKeyPress(KeyEvent.VK_ENTER, currentLevel.getPlayer());
				}
			}
		} else {
			currentLevel.update(tslf);

			if (businessGUI != null && businessGUI.isOpen()) {


				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ESCAPE)) { // Allow closing with ESC
					businessGUI.handleKeyPress(KeyEvent.VK_ESCAPE);
				}
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER)) { // Allow closing with ESC
					businessGUI.handleKeyPress(KeyEvent.VK_ENTER);
				}
			}

			// Example: Trigger a new dialogue when certain conditions are met
			// Using the directly managed 'triggeredDialogues' map
			if (currentLevel.getPlayer().getX() > 500 && currentDialogueNodeId == null && !triggeredDialogues.getOrDefault("first_npc_chat", false)) {
				startDialogue("npc_quest_start");
				triggeredDialogues.put("first_npc_chat", true); // Mark as triggered
			}
			long currentTime = System.currentTimeMillis();
			if (inBuilding && currentLevel.getPlayer().getConv()) {
				if (currentTime - lastChatTriggerTime >= chatCoolDown) {
					startDialogue("shop_greeting");
					currentLevel.getPlayer().setConv(false);
					triggeredDialogues.put("shop_greeting", true);
				}
				currentLevel.getPlayer().setConv(false);

			}
			if (inBuilding && currentLevel.getPlayer().getBook()) {
				if (currentTime - lastChatTriggerTime >= chatCoolDown) {
					startDialogue("book_greeting");
					currentLevel.getPlayer().setBook(false);
					triggeredDialogues.put("book_greeting", true);
				}
				currentLevel.getPlayer().setBook(false);
			}
			if (inBuilding && currentLevel.getPlayer().getMcDon()) {

				if (currentTime - lastChatTriggerTime >= chatCoolDown) {
					startDialogue("mc_greeting");
					currentLevel.getPlayer().setMcDon(false);
					triggeredDialogues.put("mc_greeting", true);
				}
				currentLevel.getPlayer().setMcDon(false);
			}
			if (inBuilding && currentLevel.getPlayer().getHospital()) {

				if (currentTime - lastChatTriggerTime >= chatCoolDown) {
					startDialogue("hospital_greeting");
					currentLevel.getPlayer().setHospital(false);
					triggeredDialogues.put("hospital_greeting", true);
				}
				currentLevel.getPlayer().setHospital(false);
			}
			if (inBuilding && currentLevel.getPlayer().getSchool()) {

				if (currentTime - lastChatTriggerTime >= chatCoolDown) {
					startDialogue("school_greeting");
					currentLevel.getPlayer().setSchool(false);
					triggeredDialogues.put("school_greeting", true);
				}
				currentLevel.getPlayer().setSchool(false);
			}
			if (inBuilding && currentLevel.getPlayer().getBank()) {
				if (!bankrupted) {
					if (currentTime - lastChatTriggerTime >= chatCoolDown) {
						startDialogue("bank_greeting");
						currentLevel.getPlayer().setBank(false);
						triggeredDialogues.put("bank_greeting", true);
					}
					currentLevel.getPlayer().setBank(false);
				} else {
					startDialogue("bankrupt");
					currentLevel.getPlayer().setBank(false);
					triggeredDialogues.put("bankrupt", true);
				}

			}


		}

		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_N)) init();
		if(KeyboardInputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
			if (gameLogicPausedForChat) {
				lastInteractedShopNPC.closeShop();
				gameLogicPausedForChat = false;
				startDialogue("shop_farewell");
			} else {
				//System.exit(0);
			}

		}

		if (!gameLogicPausedForChat) {
			if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_I)) { // Use isKeyJustPressed for toggles
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastInventoryToggleTime >= inventoryToggleCooldown && !inventoryAnimating) {
					inventoryVisible = !inventoryVisible;
					inventoryAnimating = true;
					lastInventoryToggleTime = currentTime;
				}
			}
			if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_Q) && hoveredItem != null) {
				inventory.dropSelectedItem(currentLevel, inventory.getSlotOf(hoveredItem), (int) currentLevel.getPlayer().getX(), (int) currentLevel.getPlayer().getY());

			}

			if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_L)) { // Use isKeyJustPressed for toggles
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastStatToggleTime >= statToggleCooldown && !statAnimating) {
					statVisible = !statVisible;
					statAnimating = true;
					lastStatToggleTime = currentTime;
				}
			}

			if (Main.playerHP <= 0) {
				declareDead();
				soundManager.play("dead");
			}

		}
		if (fireArm = true) {

		}



		PointerInfo pInfo = MouseInfo.getPointerInfo();
		if (pInfo != null) {
			Point screenPoint = pInfo.getLocation();
			screenPoint.translate(-window.getX() - window.getInsetX(), -window.getY() - window.getInsetY());
			mouseX = screenPoint.x;
			mouseY = screenPoint.y;
		}

		if (active && !gameLogicPausedForChat) currentLevel.update(tslf);

		screenTransition.update(tslf);

		if (inventoryAnimating) {
			int speed = 10;
			if (inventoryVisible) {
				inventoryX += speed;
				if (inventoryX >= targetInventoryX) {
					inventoryX = targetInventoryX;
					inventoryAnimating = false;
				}
			} else {
				inventoryX -= speed;
				if (inventoryX <= -300) {
					inventoryX = -300;
					inventoryAnimating = false;
				}
			}
		}

		if (statAnimating) {
			int speed = 10;
			if (statVisible) {
				statY -= speed;
				if (statY <= targetStatY) {
					statY = targetStatY;
					statAnimating = false;
				}
			} else {
				statY += speed;
				if (statY >= 1000) {
					statY = 1000;
					statAnimating = false;
				}
			}
		}
		if (currentLevel.getShopNPCs().size() != 0 && currentLevel.getShopNPCs().get(0).isShopOpen() && currentLevel.getShopNPCs().get(0).getShopGUI().noMoney) {
			currentLevel.getShopNPCs().get(0).closeShop();
			startDialogue("no_money");
			currentLevel.getShopNPCs().get(0).getShopGUI().noMoney = false;
		}
		if (currentLevel.getShopNPCs().size() != 0 && currentLevel.getShopNPCs().get(0).isShopOpen() && currentLevel.getShopNPCs().get(0).getShopGUI().invFull) {
			currentLevel.getShopNPCs().get(0).closeShop();
			startDialogue("inventory_full");
			currentLevel.getShopNPCs().get(0).getShopGUI().invFull = false;
		}
		if (currentLevel.getShopNPCs().size() != 0 && currentLevel.getShopNPCs().get(0).isShopOpen() && currentLevel.getShopNPCs().get(0).getShopGUI().purchasedCost != -1 && currentLevel.getShopNPCs().get(0).getShopGUI().purchasedItem != "") {
			currentLevel.getShopNPCs().get(0).closeShop();
			lastPurchasedCost = currentLevel.getShopNPCs().get(0).getShopGUI().purchasedCost;
			lastPurchasedItem = currentLevel.getShopNPCs().get(0).getShopGUI().purchasedItem;
			startDialogue("purchased");
			currentLevel.getShopNPCs().get(0).getShopGUI().purchasedCost = -1;
			currentLevel.getShopNPCs().get(0).getShopGUI().purchasedItem = "";
		}

		if (turnReportGUI.isVisible()) {
			turnReportGUI.update();
		}

		if (turnReportGUI.currentLineIndex >= turnReportGUI.allReportLines.size() && turnReportGUI.allReportLines.size() > 0) {
			gameLogicPausedForChat = true;
		}

		if (turnReportGUI.isVisible()) { // Don't hide if another GUI pauses game
			// If all lines are revealed, you might want to wait a bit then hide
			if (turnReportGUI.currentLineIndex >= turnReportGUI.allReportLines.size() &&
					System.currentTimeMillis() - turnReportGUI.lastRevealTime >= 3000) { // Wait 3 seconds after last line
				if (KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_ENTER) || KeyboardInputManager.isKeyJustPressed(KeyEvent.VK_Z)) {
					turnReportGUI.hide();
					gameLogicPausedForChat = false;
				}
			}
		}

		int mouseX = (int) getMouseInputManager().getMouseX();
		int mouseY = (int) getMouseInputManager().getMouseY();
		ShopNPC currentShopGUI = null;
		if (currentLevel.getShopNPCs().size() > 0) {
			currentShopGUI = currentLevel.getShopNPCs().get(0);
		} else {

		}


		if (getMouseInputManager().isButtonJustPressed(MouseEvent.BUTTON1)) {
			System.out.println("DEBUG: Left Mouse Button JUST PRESSED handled in Main.update()!");
			if (inventoryVisible && currentLevel.getPlayer() != null) {
				handleInventoryClick(mouseX, mouseY, MouseEvent.BUTTON1, currentLevel.getPlayer());
			} else if (currentShopGUI != null && currentShopGUI.isShopOpen()) {
				currentShopGUI.handleMouseClick(mouseX, mouseY, MouseEvent.BUTTON1, currentLevel.getPlayer());
			} else if (currentShopGUI != null && currentShopGUI.isBankOpen()) {
				currentShopGUI.getBankGUI().handleMouseClick(mouseX, mouseY, currentLevel.getPlayer());
			} else if (newspaperGUI.isOpen()) {
				newspaperGUI.handleMouseClick(mouseX, mouseY);
				return; // Consume the click so it doesn't interact with other elements behind the GUI
			} else if (governmentPortalGUI != null && governmentPortalGUI.isOpen()) {
				governmentPortalGUI.handleMouseClick(mouseX, mouseY);
			} else if (businessGUI != null && businessGUI.isOpen()) {
				businessGUI.handleMouseClick(mouseX, mouseY);
			} else if (computerScreenGUI != null && computerScreenGUI.isOpen()) {
				computerScreenGUI.handleMouseClick(mouseX, mouseY);
			} else if (!gameIsPaused && currentLevel != null && currentLevel.getPlayer() != null) {

			}

		}

		calculateStats();


	}

	private void calculateStats() {
		intelligence = 0;
		luck = 0;
		maxSlots = 2;
		intelligence += knowledge;
		for (Item i : inventory.getItems()) {
			if (i != null) {
				intelligence += i.getIntelliBoost();
				luck += i.getLuckBoost();
				if (i.getName().equalsIgnoreCase("Small Bag")) {
					maxSlots += 3;
				} else if (i.getName().equalsIgnoreCase("Large Bag")) {
					maxSlots += 5;
				}
				if (maxSlots >= 10) {
					maxSlots = 10;
				}
			}

		}
	}

	private void handlePostTransitionLoading() {
		//loadGameResources();
		if (titleScreen.shouldLoadGame()) {
			loadGame();
		}
		if (titleScreen.isHardMode()) {
			applyHardMode();
		}
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (!isInitialized) {
			return;
		}
		// Cast to Graphics2D for advanced drawing

		// 1. Save the current transform state (this is usually the identity transform for the screen)
		AffineTransform originalTransform = g2d.getTransform();

		// 2. Apply Camera Translation for World Objects
		// All subsequent drawings will be relative to the camera's view
		g2d.translate(-currentLevel.getCamera().getX(), -currentLevel.getCamera().getY());

		// 3. Draw all game world elements
		currentLevel.draw(g2d, mouseX, mouseY, g2d); // Draws map tiles and other level elements
		player.draw(g2d); // Draws the player

		// Draw NPCs, ensuring ShopNPCs only draw themselves, not their GUI
		for (ShopNPC npc : currentLevel.getShopNPCs()) {
			if (npc instanceof ShopNPC) {
				ShopNPC shopNpc = (ShopNPC) npc;
				// Make sure shopNpc.render() in ShopNPC.java only draws the NPC itself,
				// and DOES NOT call shopGUI.render()
				shopNpc.draw(g2d, player, mouseX, mouseY, g2d);
			} else {

			}
		}

		// 4. Reset the Graphics2D transform
		// This brings the drawing origin back to (0,0) of the screen
		g2d.setTransform(originalTransform);

		ShopNPC currentShopNPC = null;
		if (currentLevel.getShopNPCs().size() > 0) {
			currentShopNPC = currentLevel.getShopNPCs().get(0);
		} else {

		}



		if (titleScreen != null && titleScreen.isActive()) {
			titleScreen.draw(g2d); // Pass g2d
			return;
		}



		drawBackground(g2d); // Pass g2d
		currentLevel.draw(g2d, mouseX, mouseY, g2d); // Pass g2d (ensure currentLevel.draw also accepts Graphics2D)

		screenTransition.draw(g2d); // Pass g2d (ensure screenTransition.draw also accepts Graphics2D)


		if (inventoryVisible || inventoryAnimating) {
			drawInventory(g2d);// Pass g2d
		}
		int mouseX = (int) getMouseInputManager().getMouseX();
		int mouseY = (int) getMouseInputManager().getMouseY();

		if (currentShopNPC != null && currentShopNPC.isShopOpen()) {
			// MODIFIED: Add window.getWidth() and window.getHeight()
			currentShopNPC.getShopGUI().render(g2d, player, mouseX, mouseY, window.getWidth(), window.getHeight());
		}
		if (currentShopNPC != null && currentShopNPC.isBankOpen()) {
			currentShopNPC.getBankGUI().render(g2d, window.getWidth(), window.getHeight(), player, mouseX, mouseY);
		}
		if (computerScreenGUI != null && computerScreenGUI.isOpen()) {
			computerScreenGUI.render(g2d, mouseX, mouseY);
		}

		if (governmentPortalGUI != null && governmentPortalGUI.isOpen()) {
			governmentPortalGUI.render(g2d, mouseX, mouseY);
		}

		if (businessGUI != null && businessGUI.isOpen()) {
			businessGUI.draw(g2d);
		}


		if (newspaperGUI.isOpen()) {
			newspaperGUI.render(g2d, (int) MouseInputManager.getMouseX(), (int) MouseInputManager.getMouseY());
		}


		if (statVisible || statAnimating) {
			drawStats(g2d); // Pass g2d
		}

		// Mouse coordinates text (already has some 3D-ish feel with fillRect/drawRect)
		/*String coords = "(" + mouseX + ", " + mouseY + ")";
		g2d.setFont(new Font("Arial", Font.PLAIN, 12)); // Set a reasonable font size
		int textWidth = g2d.getFontMetrics().stringWidth(coords);

		g2d.setColor(new Color(255, 255, 255, 200)); // Slightly transparent white background
		g2d.fillRect(mouseX + 5, mouseY - 20, textWidth + 6, 18); // Offset a bit for clarity
		g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black border
		g2d.drawRect(mouseX + 5, mouseY - 20, textWidth + 6, 18);
		g2d.setColor(Color.BLACK); // Text color
		g2d.drawString(coords, mouseX + 8, mouseY - 6); // Adjust text position   */


		chatController.draw(g2d); // Pass g2d (ensure chatController.draw also accepts Graphics2D)

		if (timeDisplayGUI != null) {
			// Ensure the timeDisplayGUI always has the correct country instance
			// (especially if you switch which country is 'active' or if levels change countries)
			// For now, assuming countryList.get(0) is the main one:
			timeDisplayGUI.setCountry(countryList.get(0)); // Update the country reference
			timeDisplayGUI.render(g);

		} else {
			System.out.println("isNulL!");
		}

		transitionManager.draw(g2d, window.getWidth(), window.getHeight());

		if (turnReportGUI != null && turnReportGUI.isVisible()) {
			turnReportGUI.render(g2d);
		}

	}

	public void drawBackground(Graphics g) {
		Graphics2D g2d = (Graphics2D) g; // Cast to Graphics2D
		// A subtle gradient for the background
		GradientPaint bgGradient = new GradientPaint(
				0, 0, new Color(0, 100, 50), // Darker green at top
				0, Main.SCREEN_HEIGHT, new Color(0, 180, 90)); // Lighter green at bottom
		g2d.setPaint(bgGradient);
		g2d.fillRect(0, 0 - MyWindow.getInsetY(), SCREEN_WIDTH, SCREEN_HEIGHT + MyWindow.getInsetY() * 2);

		g2d.setColor(Color.BLACK);
		g2d.drawString("Game Running - Press I to toggle inventory", 50, 50);
	}

	private void drawInventory(Graphics2D g2d) {
		inventory.setMaxSlots(maxSlots);// Changed to Graphics2D
		int actualSlots = inventory.getMaxSlots();
		int displayCols = Math.min(COLS, actualSlots);
		int displayRows = (actualSlots + COLS - 1) / COLS;

		int backgroundWidth = displayCols * SLOT_SIZE;
		int backgroundHeight = displayRows * SLOT_SIZE;

		int startX = (SCREEN_WIDTH - backgroundWidth) / 2;
		int startY = (SCREEN_HEIGHT - backgroundHeight) / 2;

		int xOffset = inventoryX;


		// 1. Draw a subtle drop shadow for the whole panel
		g2d.setColor(new Color(0, 0, 0, 120)); // Semi-transparent black shadow
		g2d.fill(new RoundRectangle2D.Float(xOffset - 10 + 10, startY + 90 + 10, backgroundWidth + 20, backgroundHeight + 20, 20, 20)); // Offset shadow

		// 2. Draw the main panel background with a gradient and rounded corners
		GradientPaint panelGradient = new GradientPaint(
				xOffset - 10, startY + 90, new Color(50, 50, 70, 220), // Dark grey-blue top
				xOffset - 10, startY + 90 + backgroundHeight + 20, new Color(20, 20, 30, 220)); // Even darker bottom
		g2d.setPaint(panelGradient);
		g2d.fill(new RoundRectangle2D.Float(xOffset - 10, startY + 90, backgroundWidth + 20, backgroundHeight + 20, 20, 20));

		// 3. Draw a border for the panel
		g2d.setColor(new Color(100, 100, 150)); // Lighter border color
		g2d.setStroke(new BasicStroke(2)); // Thicker border
		g2d.draw(new RoundRectangle2D.Float(xOffset - 10, startY + 90, backgroundWidth + 20, backgroundHeight + 20, 20, 20));
		g2d.setStroke(new BasicStroke(1)); // Reset stroke

		hoveredItem = null;

		ArrayList<Item> items = inventory.getItems();
		for (int i = 0; i < actualSlots; i++) {
			int row = i / COLS;
			int col = i % COLS;

			int x = xOffset + col * SLOT_SIZE;
			int y = startY + 100 + row * SLOT_SIZE;
			int slotX = inventoryX + 20 + col * SLOT_SIZE;
			int slotY = y;

			// 4. Draw individual slot rectangles with a sunken/beveled effect
			// Inner fill (darker, recessed look)
			g2d.setColor(new Color(30, 30, 40)); // Darker interior
			g2d.fillRect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);

			// Highlights (top and left)
			g2d.setColor(new Color(80, 80, 100));
			g2d.drawLine(x + 2, y + 2, x + SLOT_SIZE - 2, y + 2); // Top line
			g2d.drawLine(x + 2, y + 2, x + 2, y + SLOT_SIZE - 2); // Left line

			// Shadows (bottom and right)
			g2d.setColor(new Color(10, 10, 15));
			g2d.drawLine(x + 2, y + SLOT_SIZE - 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2); // Bottom line
			g2d.drawLine(x + SLOT_SIZE - 2, y + 2, x + SLOT_SIZE - 2, y + SLOT_SIZE - 2); // Right line

			// Draw the border
			g2d.setColor(new Color(60, 60, 80)); // Medium gray border
			g2d.drawRect(x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4);


			// Draw item name if the slot has an item
			if (i < items.size()) {
				g2d.setColor(Color.WHITE); // Set color for item name
				g2d.setFont(new Font("Arial", Font.PLAIN, 14)); // Smaller font for items
				Item item = items.get(i);
				if (item.getImage() != null) {
					// Draw item image, scaling it to fit the SLOT_SIZE (64x64) inner area
					g2d.drawImage(item.getImage(), x, -30 + y + SLOT_SIZE / 2, SLOT_SIZE, SLOT_SIZE, null);
				}
				if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
						mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
					hoveredItem = item;
				}
			}
		}


		// --- NEW: Render Tooltip for Hovered Item ---
		if (hoveredItem != null) {
			renderTooltip(g2d, hoveredItem, mouseX, mouseY);

		}

		if (inventoryMessage != null && System.currentTimeMillis() < messageDisplayEndTime) {
			g2d.setFont(new Font("Arial", Font.BOLD, 18));
			g2d.setColor(Color.YELLOW); // Distinct color for inventory messages
			int messageWidth = g2d.getFontMetrics().stringWidth(inventoryMessage);
			int messageX = inventoryX + (SCREEN_WIDTH - messageWidth) / 2;
			int messageY = 200 + SCREEN_HEIGHT - 20; // Position at bottom of panel
			g2d.drawString(inventoryMessage, messageX, messageY);
		} else if (inventoryMessage != null && System.currentTimeMillis() >= messageDisplayEndTime) {
			inventoryMessage = null; // Clear message if its display time has passed
		}
	}


	private void renderTooltip(Graphics2D g2d, Item item, int mouseX, int mouseY) { // Removed Player parameter
		int tooltipWidth = 280;
		int tooltipHeight = 100; // Base height from your code

		// Adjust tooltip height based on content
		// If desc2 is present, add space for it
		if (item.getDesc2() != null && !item.getDesc2().isEmpty()) {
			tooltipHeight += 15; // Extra line for desc2
		}
		// If it's food and has effects, add space for those lines
		if (item.getType() == Item.ItemType.FOOD) {
			if (item.getHealthRestore() > 0 || item.getSatisfactionBoost() > 0 || item.getLuckBoost() > 0) {
				tooltipHeight += 45; // Add space for effect lines
			}
		}


		// --- Screen Boundary Adjustment (re-added for better UX) ---
		int actualTooltipX = mouseX + 15; // Offset from mouse
		int actualTooltipY = mouseY + 15;

		if (actualTooltipX + tooltipWidth > SCREEN_WIDTH) {
			actualTooltipX = mouseX - tooltipWidth - 15;
		}
		if (actualTooltipY + tooltipHeight > SCREEN_HEIGHT) {
			actualTooltipY = SCREEN_HEIGHT - tooltipHeight - 15;
		}
		if (actualTooltipX < 0) actualTooltipX = 5;
		if (actualTooltipY < 0) actualTooltipY = 5;


		g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black background for tooltip
		g2d.fillRect(actualTooltipX, actualTooltipY, tooltipWidth, tooltipHeight);
		g2d.setColor(Color.WHITE); // White text for tooltip content

		int currentTextY = actualTooltipY + 15; // Start drawing text from here

		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14)); // Bold for item name
		g2d.drawString(item.getName(), actualTooltipX + 5, currentTextY);
		currentTextY += 15;

		g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12)); // Plain for description
		g2d.drawString(item.getDesc(), actualTooltipX + 5, currentTextY);
		currentTextY += 15;

		// --- NEW: Draw desc2 ---
		if (item.getDesc2() != null && !item.getDesc2().isEmpty()) {
			g2d.setColor(Color.CYAN);
			g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12));
			g2d.drawString(item.getDesc2(), actualTooltipX + 5, currentTextY);
			currentTextY += 15;
		}


		g2d.setColor(item.getRarityColor()); //for rarity
		g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 12)); // Bold for rarity
		g2d.drawString("Rarity: " + item.getRarity(), actualTooltipX + 5, currentTextY); // Explicitly display "Rarity: "
		currentTextY += 15;

		// --- Display consumption effects for food items (no player stats) ---
		if (item.getType() == Item.ItemType.FOOD) {
			g2d.setFont(g2d.getFont().deriveFont(Font.ITALIC, 11));
			g2d.setColor(new Color(180, 255, 180)); // Light green for effects

			if (item.getHealthRestore() != 0) {
				g2d.drawString("Restores HP: +" + item.getHealthRestore(), actualTooltipX + 5, currentTextY);
				currentTextY += 13;
			}
			if (item.getSatisfactionBoost() > 0) {
				g2d.drawString("Boosts Satisfaction: +" + item.getSatisfactionBoost() + "%", actualTooltipX + 5, currentTextY);
				currentTextY += 13;
			}
			if (item.getLuckBoost() > 0) {
				g2d.drawString("Boosts Luck: +" + item.getLuckBoost(), actualTooltipX + 5, currentTextY);
				currentTextY += 13;
			}
			if (item.getType() == Item.ItemType.FOOD) {
				g2d.setColor(Color.YELLOW);
				g2d.drawString("Click to Consume!", actualTooltipX + 5, currentTextY);
				currentTextY += 13;
			}
			 // Reset color
		}
		g2d.setColor(Color.RED);
		g2d.drawString("Press Q to throw it away!", actualTooltipX + 5, currentTextY);
		currentTextY += 13;
		g2d.setColor(Color.WHITE);
	}

	public void jukeBox() {
		if (finalBattle) {
			soundManager.stop("bgm");
			soundManager.loop("sad");
		} else {
			soundManager.stop("sad");
			soundManager.loop("bgm");
		}
	}


	private void handleInventoryClick(int mouseX, int mouseY, int button, Player player) {
		if (button == MouseEvent.BUTTON1) { // Left-click
			Inventory inventory = this.inventory;
			int actualSlots = inventory.getMaxSlots();
			int displayCols = Math.min(COLS, actualSlots);

			int panelTotalWidth = displayCols * SLOT_SIZE + 40;
			int panelY = (SCREEN_HEIGHT - ( (actualSlots + COLS - 1) / COLS * SLOT_SIZE + 120)) / 2;

			for (int i = 0; i < actualSlots; i++) {
				int row = i / COLS;
				int col = i % COLS;

				int displayRows = (actualSlots + COLS - 1) / COLS;

				int backgroundHeight = displayRows * SLOT_SIZE;

				int slotX = inventoryX + 20 + col * SLOT_SIZE;
				int slotY = (SCREEN_HEIGHT - backgroundHeight) / 2 + 100 + row * SLOT_SIZE;


				if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE &&
						mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
					// Clicked on a slot
					if (i < inventory.getItems().size()) {
						Item clickedItem = inventory.getItems().get(i);
						System.out.println(clickedItem);
						if (clickedItem != null) {
							System.out.println("Clicked on item: " + clickedItem.getName());
							// Attempt to use the item
							boolean used = player.consumeItem(clickedItem);
							if (used) {
								System.out.println(clickedItem.getName() + " used!");
								soundManager.play("eat");
								inventory.removeItem(clickedItem.getName()); // Remove item after use
								setInventoryMessage("Consumed " + clickedItem.getName() + "!");
							} else {
								System.out.println("Cannot use " + clickedItem.getName() + " now.");
								setInventoryMessage("Cannot use " + clickedItem.getName() + "!");
							}
						}
					} else {
						System.out.println("Clicked on empty slot " + i);
					}
					return; // Exit after handling the click
				}
			}

			// Click outside inventory slots but within the inventory panel to close
			if (mouseX >= inventoryX && mouseX < inventoryX + panelTotalWidth &&
					mouseY >= panelY && mouseY < panelY + ((actualSlots + COLS - 1) / COLS * SLOT_SIZE + 120)) {
				// Clicked within the inventory panel but not on a slot, so do nothing (keep open)
			} else {
				inventoryVisible = false; // Close inventory if clicked outside the panel
				System.out.println("Inventory closed.");
			}
		}
	}
	public void setInventoryMessage(String message) {
		this.inventoryMessage = message;
		this.messageDisplayEndTime = System.currentTimeMillis() + MESSAGE_DURATION;
	}

	private void drawStats(Graphics2D g2d) { // Changed to Graphics2D
		int statWidth = 240;
		int statHeight = 200;
		int startX = 792;
		int startY = 100;

		int yOffset = statY;

		// 1. Draw a subtle drop shadow for the whole panel
		g2d.setColor(new Color(0, 0, 0, 120)); // Semi-transparent black shadow
		g2d.fill(new RoundRectangle2D.Float(startX + 10, yOffset + 10, statWidth + 30, statHeight + 190, 20, 20)); // Offset shadow

		// 2. Draw the main panel background with a gradient and rounded corners
		GradientPaint panelGradient = new GradientPaint(
				startX, yOffset, new Color(70, 50, 50, 220), // Dark red-brown top
				startX, yOffset + statHeight + 190, new Color(40, 20, 20, 220)); // Even darker bottom
		g2d.setPaint(panelGradient);
		g2d.fill(new RoundRectangle2D.Float(startX, yOffset, statWidth + 30, statHeight + 190, 20, 20));

		// 3. Draw a border for the panel
		g2d.setColor(new Color(150, 100, 100)); // Lighter border color
		g2d.setStroke(new BasicStroke(2)); // Thicker border
		g2d.draw(new RoundRectangle2D.Float(startX, yOffset, statWidth + 30, statHeight + 190, 20, 20));
		g2d.setStroke(new BasicStroke(1)); // Reset stroke


		g2d.setFont(new Font("Arial", Font.BOLD, 18)); // Slightly larger font for title
		g2d.setColor(Color.WHITE);
		MyGraphics.drawCenteredString(g2d, "Current Status", new Rectangle(startX, yOffset + 10, statWidth + 30, 20), g2d.getFont()); // Center title

		g2d.setFont(new Font("Arial", Font.PLAIN, 15)); // Smaller font for stats

		// Draw stats with subtle shadows (optional, but adds depth)
		g2d.drawImage(currentLevel.getPlayer().getHeart(), 810, yOffset + 30, 25, 25, null);
		drawStatLine(g2d, "Health: " + currentLevel.getPlayer().getCondition(), 840, yOffset + 50, Color.RED);

		g2d.drawImage(GameResources.cash, 810, yOffset + 60, 25, 25, null);
		drawStatLine(g2d, "Money: $" + String.format("%.2f", currentLevel.getPlayer().getPurse()), 840, yOffset + 80, Color.YELLOW);

		drawStatLine(g2d, "Intelligence: " + currentLevel.getPlayer().getIntelligence(), 810, yOffset + 110, Color.CYAN);

		drawStatLine(g2d, "Satisfaction: " + currentLevel.getPlayer().getSat() + "%", 810, yOffset + 140, Color.PINK);

		drawStatLine(g2d, "Luck: " + currentLevel.getPlayer().getLuck(), 810, yOffset + 170, Color.GREEN);

		drawStatLine(g2d, "Job: " + currentLevel.getPlayer().getJob(), 810, yOffset + 200, Color.MAGENTA);
	}

	// Helper method to draw a stat line with a subtle shadow
	private void drawStatLine(Graphics2D g2d, String text, int x, int y, Color color) {
		// Shadow for text
		g2d.setColor(new Color(0, 0, 0, 100)); // Dark, semi-transparent
		g2d.drawString(text, x + 1, y + 1); // Draw slightly offset

		// Actual text
		g2d.setColor(color);
		g2d.drawString(text, x, y);
	}


	@Override
	public void onChatBoxAdvanced() {
		long currentTime = System.currentTimeMillis();
		if (currentDialogueNodeId != null) {
			DialogueNode currentNode = dialogueManager.getDialogueNode(currentDialogueNodeId);
			if (currentNode != null && !currentNode.hasOptions()) {
				String nextNodeId = currentNode.getDefaultNextNodeId();
				if (nextNodeId != null) {
					if (currentNode.getOnExitAction() != null) {
						currentNode.getOnExitAction().accept(this);
					}
						startDialogue(nextNodeId);


				} else {
					chatController.hideChatBox();
				}
			} else {
				chatController.hideChatBox();
			}
		} else {
			chatController.hideChatBox();
		}
		if (lastInteractedShopNPC != null && lastInteractedShopNPC.isShopOpen()) {
			gameLogicPausedForChat = true;
		}
	}

	@Override
	public void onChatBoxHidden() {
		gameLogicPausedForChat = false;
		System.out.println("Chat box hidden. Game logic resumed. Current Dialogue Node: " + currentDialogueNodeId);
		if (currentDialogueNodeId != null && dialogueManager.getDialogueNode(currentDialogueNodeId) != null &&
				(dialogueManager.getDialogueNode(currentDialogueNodeId).getDefaultNextNodeId() == null &&
						!dialogueManager.getDialogueNode(currentDialogueNodeId).hasOptions())) {
			currentDialogueNodeId = null;
		}
		if (lastInteractedShopNPC == null || !lastInteractedShopNPC.isShopOpen()) {
			gameLogicPausedForChat = false;
		}
	}

	@Override
	public void onOptionSelected(int selectedIndex, String selectedOptionText) {
		if (currentDialogueNodeId != null) {
			DialogueNode currentNode = dialogueManager.getDialogueNode(currentDialogueNodeId);
			if (currentNode != null && currentNode.hasOptions()) {
				String nextNodeId = currentNode.getNextNodeIdForOption(selectedIndex);

				if (currentNode.getOnExitAction() != null) {
					currentNode.getOnExitAction().accept(this);
				}

				if (nextNodeId != null) {
					startDialogue(nextNodeId);
				} else {
					chatController.hideChatBox();
				}
			} else {
				chatController.hideChatBox();
			}
		} else {
			chatController.hideChatBox();
		}
	}

	private void applyHardMode() {
		// Example: Reduce player money, increase prices, etc.
		currentLevel.getPlayer().setPurse(currentLevel.getPlayer().getPurse() / 2); // Half starting money
		countryList.get(0).difficulty = 1;
	}

	public void saveGame() {
		try (ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream(SAVE_FILE))) {

			// Create a save data container
			GameSaveData saveData = new GameSaveData();

			// 1. Player Data
			saveData.playerX = currentLevel.getPlayer().getX();
			saveData.playerY = currentLevel.getPlayer().getY();
			saveData.playerHP = currentLevel.getPlayer().playerHP;
			saveData.money = currentLevel.getPlayer().getPurse();
			saveData.inventory = inventory.getItems();
			saveData.loan = debt;
			saveData.knowledge = knowledge;

			// 2. Game State
			saveData.currentLevelIndex = currentLevelIndex;
			saveData.triggeredDialogues = new HashMap<>(triggeredDialogues);

			// 3. World Data
			saveData.countryEconomies = new ArrayList<>();
			for (country c : countryList) {
				saveData.countryEconomies.add(new CountrySaveData(
						c.getName(),
						c.getGDP(),
						c.getPop(),
						c.getIR(),
						c.getTax(),
						c.getSpending(),
						c.getCountryNews(),
						c.getFrontLine(),
						c.isInWar(),
						c.getTime()
				));
			}

			// Write to file
			out.writeObject(saveData);
			System.out.println("Game saved successfully!");

		} catch (IOException e) {
			System.err.println("Failed to save game: " + e.getMessage());
		}
	}

	public void loadGame() {
		try (ObjectInputStream in = new ObjectInputStream(
				new FileInputStream(SAVE_FILE))) {

			GameSaveData saveData = (GameSaveData) in.readObject();

			// 1. Restore Player
			Player player = new Player(saveData.playerX, saveData.playerY, currentLevel);
			playerHP = saveData.playerHP;
			purse = saveData.money;
			balance = saveData.balance;
			debt = saveData.loan;
			knowledge = saveData.knowledge;
			currentLevel.setPlayer(player);

			// 2. Restore Inventory
			inventory = new Inventory(maxSlots);
			for (Item item : saveData.inventory) {
				inventory.addItem(item);
			}

			// 3. Restore Game State
			currentLevelIndex = saveData.currentLevelIndex;
			triggeredDialogues = new HashMap<>(saveData.triggeredDialogues);

			// 4. Restore Countries
			countryList.clear();
			for (CountrySaveData csd : saveData.countryEconomies) {
				countryList.add(new country(
						csd.name,
						csd.population,
						csd.gdp,
						5.0,
						csd.IR// Default values
				));
				countryList.get(countryList.size()-1).setTime(csd.time);
				countryList.get(countryList.size()-1).setPop(csd.population);
				countryList.get(countryList.size()-1).setFL(csd.frontLine);
				countryList.get(countryList.size()-1).setInWar(csd.isInWar);
				countryList.get(countryList.size()-1).setCountryNews(csd.countrynews);
				countryList.get(countryList.size()-1).setTax(csd.taxRate);
				countryList.get(countryList.size()-1).setSpending(csd.spending);

			}

			// Reload level
			currentLevel = new Level(levels[currentLevelIndex], this);
			currentLevel.setPlayer(player);
			isInitialized = true;


			System.out.println("Game loaded successfully!");

		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Failed to load game: " + e.getMessage());
			init(); // Fallback to new game
		}
	}

	public PokemonChatController getChatController() {
		return chatController;
	}

	public void removeLaunchedProduct(Product product) {
		this.launchedProducts.remove(product);
	}

	public List<Product> getLaunchedProducts() {
		return this.launchedProducts;
	}

	public void addLaunchedProduct(Product newProduct) {
		this.launchedProducts.add(newProduct);
	}

	private static class GameSaveData implements Serializable {
		// Player Data
		float playerX, playerY;
		int playerHP;
		double money;
		double loan;
		double balance;
		int knowledge;
		ArrayList<Item> inventory;

		// Game State
		int currentLevelIndex;
		HashMap<String, Boolean> triggeredDialogues;

		// World Data
		ArrayList<CountrySaveData> countryEconomies;
	}

	private static class CountrySaveData implements Serializable {
		String name;
		double gdp;
		int population;
		double IR;
		double taxRate;
		double spending;
		List<Event> countrynews;
		int frontLine;
		boolean isInWar;
		int time;


		public CountrySaveData(String name, double gdp, int population, double IR, double taxRate, double spending, List<Event> countrynews, int frontLine, boolean isInWar, int time) {
			this.name = name;
			this.gdp = gdp;
			this.population = population;
			this.IR = IR;
			this.taxRate = taxRate;
			this.spending = spending;
			this.countrynews = countrynews;
			this.frontLine = frontLine;
			this.isInWar = isInWar;
			this.time = time;
		}
	}

	public static void calculateIncomeTax() {
		incomeTax = (monthlyIncome * countryList.get(0).getTax()) / 100;
	}

	public static double getTaxRate() {
		return countryList.get(0).getTax();
	}

	public static void sleep() {
		long currentTime = System.currentTimeMillis();
		if (isSleeping) {

		} else {
			if (currentTime - lastChatTriggerTime >= chatCoolDown) {
				Main.getInstance().startDialogue("sleeper");
			}

		}
	}

	public static void timeUpdate() {
		getInstance().saveGame();
		workChance = 5;
		houseRent = 250 + ((200 * getTaxRate()) / 100);
		debt += (debt * countryList.get(0).getIR()) / 100;
		for (country c : countryList) {
			c.timeLapse();
		}
		if (instance != null && instance.currentLevel != null) {
			instance.turnReportGUI.clearEvents(); // Clear events from previous turn
			// Example: Add some events dynamically
			instance.turnReportGUI.addEvent("This month's earnings: $" + monthlyIncome);
			calculateIncomeTax();
			instance.turnReportGUI.addEvent("You paid $" + String.format("%.2f", incomeTax) + " for income tax");
			pay(incomeTax);
			instance.turnReportGUI.addEvent("You spent $" + String.format("%.2f", moneySpent)  + " this month");
			instance.turnReportGUI.addEvent("You earned $" + String.format("%.2f", (balance * countryList.get(0).getIR()) / 100) + " from interest");
			balance += (balance * countryList.get(0).getIR()) / 100;
			if (debt <= 0) {
				instance.turnReportGUI.addEvent("You gained +5% Satisfaction");
				satisfiction += 5;
				instance.turnReportGUI.addEvent("You gained +100 Health");
				playerHP += 100;
			} else if (debt > 50) {
				instance.turnReportGUI.addEvent("You lost " + ((int) (debt / 100) + 5) + " % Satisfaction from debt pressure");
				satisfiction -= ((int) (debt / 100) + 5);
			}
			instance.turnReportGUI.addEvent("Paid $" + String.format("%.2f", houseRent) + " for house rent");
			pay(houseRent);
			instance.turnReportGUI.addEvent("You have $" + String.format("%.2f", debt) + " loan debt");
			if (debt > 0) {
				instance.turnReportGUI.addEvent("Pay it off at the bank.");
			}
			if (countryList.get(0).isInWar()) {
				instance.turnReportGUI.addEvent("Your Country Rodmania is in war.");
			}
			instance.turnReportGUI.show(); // Show the report GUI
		}
		monthlyIncome = 0;
		moneySpent = 0;
	}

	public static void declareDead() {
		isDead = true;
		Main.getInstance().startFadeOut(0.1f, () -> {
			isDead = true;
			if (instance != null && instance.currentLevel != null) {
				instance.turnReportGUI.clearEvents(); // Clear events from previous turn
				// Example: Add some events dynamically
				getInstance().soundManager.play("frown");
				instance.turnReportGUI.addEvent("You Died!");
				instance.turnReportGUI.addEvent("Your weakened health caused your death.");
				instance.turnReportGUI.addEvent("How was your life?");
				instance.turnReportGUI.addEvent("Paid $" + 1000 + " for funeral fees");
				pay(1000);
				instance.turnReportGUI.addEvent("You had $" + String.format("%.2f", debt) + " loan debt");
				instance.turnReportGUI.show();
				// Show the report GUI
				Main.getInstance().startFadeIn(0f, () -> {


				});

			}
		});

	}

	public static void pay(double amount) {
		moneySpent += amount;
		if (purse >= amount) {
			purse -= amount;
		} else if (balance >= amount) {
			balance -= amount;
		} else {
			debt += amount;
		}
	}

	public static void labor(double earning, int stress) {
		workChance--;
		instance.turnReportGUI.clearEvents(); // Clear events from previous turn
		// Example: Add some events dynamically
		instance.turnReportGUI.addEvent("You earned: $" + earning);
		purse += earning;
		monthlyIncome += earning;
		instance.turnReportGUI.addEvent("-" + stress + " satisfaction from stress");
		satisfiction -= stress;
		if (satisfiction < 0) {
			instance.turnReportGUI.addEvent("-" + stress * 2 + " health from overstress");
			playerHP -= stress * 2;
		}
		instance.turnReportGUI.addEvent("You can still work " + workChance + " times");
		if (countryList.get(0).isInWar()) {
			instance.turnReportGUI.addEvent("Your Country Rodmania is in war.");
		}
		instance.turnReportGUI.show(); // Show the report GUI
	}

	public static void learn(double cost, int stress, int smarter) {
		instance.turnReportGUI.clearEvents(); // Clear events from previous turn
		// Example: Add some events dynamically
		instance.turnReportGUI.addEvent("You paid: $" + cost + " for class");
		pay(cost);
		instance.turnReportGUI.addEvent("+" + smarter + " intelligence");
		knowledge += smarter;
		instance.turnReportGUI.addEvent("-" + stress + " satisfaction from stress");
		satisfiction -= stress;
		if (satisfiction < 0) {
			instance.turnReportGUI.addEvent("-" + stress * 2 + " health from overstress");
			playerHP -= stress * 2;
		}
		instance.turnReportGUI.addEvent("You have $" + debt + " loan debt");
		if (countryList.get(0).isInWar()) {
			instance.turnReportGUI.addEvent("Your Country Rodmania is in war.");
		}
		instance.turnReportGUI.show(); // Show the report GUI
	}

	public static String tryOut(int requiredIntelligence, String job) {
		if (workChance <= 0) {
			return("no_work");
		}
		if (Main.job == job) {
			return(job + "_work_confirmed");
		} else if (intelligence >= requiredIntelligence) {
			Main.job = job;
			return(job + "_pass");
		} else {
			return("not_pass");
		}
	}

	public void canWork(String id) {
		if (workChance > 0) {
			if (id.equals("mc")) {

				job = "McDonalds Worker";
			} else if (id.equals("shop")) {

				job = "Convenience Store Worker";

			}
		} else {
			startDialogue("no_work");
		}
	}

	@Override
	public boolean isGameLogicPausedForChat() {
		return chatController.isActiveAndAwaitingPlayerAction() ||
				               inventoryVisible ||
				               statVisible ||
				               (lastInteractedShopNPC != null && lastInteractedShopNPC.isShopOpen());
	}

	public void hideTurnReport() {
		turnReportGUI.hide();
	}

	public void showTurnReport() {
		turnReportGUI.show();
	}

	public void startFadeOut(float speed, Runnable onComplete) {
		transitionManager.fadeOut(speed, onComplete);
	}

	public void startFadeIn(float speed, Runnable onComplete) {
		transitionManager.fadeIn(speed, onComplete);
	}

	public boolean isGameLogicPausedForTransition() {
		return transitionManager.isActive();
	}

	public static Font getTimesNewRomanFont() {
		return timesNewRomanFont;
	}


}

