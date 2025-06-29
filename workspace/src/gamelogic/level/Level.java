package gamelogic.level;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.ImageLoader;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gameengine.loaders.TilesetLoader;
import gameengine.maths.Vector2D;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Item;
import gamelogic.player.DroppedItem;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.*;
import gamelogic.world.Building;
import gamelogic.world.DialogueTriggerListener;
import gamelogic.world.ShopNPC;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;
	private boolean initialized;
	private boolean enter;



	private boolean active;
	private boolean playerDead;
	private boolean playerWin;
	private boolean isVillage = true;
	private List<DroppedItem> droppedItems = new ArrayList<>();

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private List<Building> buildings = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private ArrayList<ShopNPC> shopNPCs;

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	private DialogueTriggerListener dialogueTriggerListener;

	public Level(LevelData leveldata, DialogueTriggerListener dialogueTriggerListener) {
		this.leveldata = leveldata;
		this.dialogueTriggerListener = dialogueTriggerListener;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		player = new Player(0, 0,
				this);
		restartLevel();

		if (!Main.mapInitialized || Main.inBuilding) {
			player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
					this);
			System.out.println("inited");
			initialized = true;
		} else {
			if (Main.playerLastVector != null) {
				Vector2D v = Main.playerLastVector;
				player = new Player(v.x, v.y - 20,
						this);
			}
		}
		restartLevel();

	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		Building house = new Building(1, 0, 400, 340, GameResources.house);
		Building convStore = new Building(3, 0, 389, 309, GameResources.conv);
		Building mcStore = new Building(6, 2, 375, 344, GameResources.mcdon);
		Building school = new Building(9, 1, 528, 489, GameResources.school);
		Building bookStore = new Building(9, 0, 245, 245, GameResources.bookStore);
		Building hospital = new Building(6.05f, 1.67f, 538, 361, GameResources.hospital);
		Building bank = new Building(10, 0, 404, 365, GameResources.bank);
		Building empty = new Building(7, 0, 404, 365, GameResources.emptyStore);


		if (Main.getCurrentLevelIndex() == 0) {
			// Example: Add a test building
			buildings.clear(); // clear any existing buildings
			addBuilding(house);
			addBuilding(convStore);
			addBuilding(mcStore);
			addBuilding(school);
			addBuilding(bookStore);
			addBuilding(hospital);
			addBuilding(bank);
			addBuilding(empty);
		}
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new Interaction(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this, 0);
				else if (values[x][y] == 7)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
				else if (values[x][y] == 22 || values[x][y] == 23)
					tiles[x][y] = new Door(xPosition, yPosition, tileSize, tileset.getImage("Door1"), this);
				else if (values[x][y] == 24)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Tree2"), this);
				else if (values[x][y] == 25)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("Green"), this);
				else if (values[x][y] == 26)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("Brick"), this);
				else if (values[x][y] == 27)
					tiles[x][y] = new Chest(xPosition, yPosition, tileSize, tileset.getImage("Chest"), this);
				else if (values[x][y] == 28 || values[x][y] == 29)
					tiles[x][y] = new Computa(xPosition, yPosition, tileSize, tileset.getImage("Comta1"), this);
				else if (values[x][y] == 30)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Quartz"), this);
				else if (values[x][y] == 31)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Table"), this);
				else if (values[x][y] == 32)
					tiles[x][y] = new Bed(xPosition, yPosition, tileSize, tileset.getImage("Bed2"), this);
				else if (values[x][y] == 33)
					tiles[x][y] = new Bed(xPosition, yPosition, tileSize, tileset.getImage("Bed1"), this);
				else if (values[x][y] == 34)
					tiles[x][y] = new Interaction(xPosition, yPosition, tileSize, tileset.getImage("Interact"), this, 0);
				else if (values[x][y] == 35)
					tiles[x][y] = new Interaction(xPosition, yPosition, tileSize, tileset.getImage("Interact1"), this, 0);
				else if (values[x][y] == 36)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("blk1"), this);
				else if (values[x][y] == 37)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("blk2"), this);
				else if (values[x][y] == 38)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("blk3"), this);
				else if (values[x][y] == 39)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("cblk1"), this);
				else if (values[x][y] == 40)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("cblk2"), this);
				else if (values[x][y] == 41)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("cblk3"), this);
				else if (values[x][y] == 42)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("macfloor"), this);
				else if (values[x][y] == 43)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("bookfloor"), this);
				else if (values[x][y] == 44)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("schoolfloor"), this);
				else if (values[x][y] == 45)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("housefloor"), this);
				else if (values[x][y] == 46)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("hospitalfloor"), this);
				else if (values[x][y] == 47)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("testfloor1"), this);
				else if (values[x][y] == 48)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("testfloor2"), this);
				else if (values[x][y] == 49)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("testfloor3"), this);
				else if (values[x][y] == 50)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("w_drawer1"), this);
				else if (values[x][y] == 51)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("w_drawer2"), this);
				else if (values[x][y] == 52)
					tiles[x][y] = new Computa(xPosition, yPosition, tileSize, tileset.getImage("w_monitor"), this);
				else if (values[x][y] == 53)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("w_cabinet1"), this);
				else if (values[x][y] == 54)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("w_cabinet2"), this);
				else if (values[x][y] == 55)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_box1"), this);
				else if (values[x][y] == 56)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_bookcase1"), this);
				else if (values[x][y] == 57)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_bookcase2"), this);
				else if (values[x][y] == 58)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_bookcase3"), this);
				else if (values[x][y] == 59)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_gbox"), this);
				else if (values[x][y] == 60)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_table"), this);
				else if (values[x][y] == 61)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_drawer3"), this);
				else if (values[x][y] == 62)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_bed1"), this);
				else if (values[x][y] == 63)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("b_bed2"), this);
				else if (values[x][y] == 64)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_pot1"), this);
				else if (values[x][y] == 65)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_pot2"), this);
				else if (values[x][y] == 66)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_pot3"), this);
				else if (values[x][y] == 67)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_pot4"), this);
				else if (values[x][y] == 68)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_flower1"), this);
				else if (values[x][y] == 69)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_flower2"), this);
				else if (values[x][y] == 70)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("p_pot"), this);
				else if (values[x][y] == 71)
					tiles[x][y] = new Bed(xPosition, yPosition, tileSize, tileset.getImage("p_bed1"), this);
				else if (values[x][y] == 72)
					tiles[x][y] = new Bed(xPosition, yPosition, tileSize, tileset.getImage("p_bed2"), this);
				else if (values[x][y] == 73)
					tiles[x][y] = new NewsBlock(xPosition, yPosition, tileSize, tileset.getImage("newspaper"), this, 0);
				else if (values[x][y] == 74)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("conv_self1"), this);
				else if (values[x][y] == 75)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("conv_self2"), this);
				else if (values[x][y] == 76)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("conv_self3"), this);
				else if (values[x][y] == 77)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("mac_table1"), this);
				else if (values[x][y] == 78)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("mac_table2"), this);
				else if (values[x][y] == 79)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("mac_pot"), this);
				else if (values[x][y] == 80)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("refr1"), this);
				else if (values[x][y] == 81)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("refr2"), this);
				else if (values[x][y] == 82)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("sofa"), this);
				else if (values[x][y] == 83)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("chair"), this);
				else if (values[x][y] == 84)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("bookcase1"), this);
				else if (values[x][y] == 85)
					tiles[x][y] = new Brick(xPosition, yPosition, tileSize, tileset.getImage("bookcase2"), this);

			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}

		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;

		//initialize npcs
		shopNPCs = new ArrayList<>();
		if (Main.getCurrentLevelIndex() == 2) {
			List<Item> generalStoreItems = new ArrayList<>();
			// Add items to shop
			generalStoreItems.add(new Item("Water Bottle", "The basic necessity for human", "(+2% Satisfaction and +10 Health when consumed)", 1, 4.99, GameResources.water, 0, 0, 0, 0, Item.ItemType.FOOD, 10, 2, 0, 0));
			generalStoreItems.add(new Item("Cheap Bread",  "Economically Efficient!", "(+5% Satisfaction and +5 Health when consumed)", 1, 7.25, GameResources.bread, 0, 0, 0, 0, Item.ItemType.FOOD, 5, 5, 0, 0));
			generalStoreItems.add(new Item("Cheap Pen", 1, "This pen looks weaker than a sword", "(+10 Intelligence while in Inventory)", 8.99, GameResources.pen, 0, 0, 0, 0, 10, 0));
			generalStoreItems.add(new Item("Chips",  "Fun to eat!",  "(+10% Satisfaction when consumed)", 2, 3.99, GameResources.chips, 0, 0, 0, 0, Item.ItemType.FOOD, 0, 10, 0, 0));
			generalStoreItems.add(new Item("Peanut Butter", "Is this an inferior good?", "(+5% Satisfaction when consumed)", 2, 6.99, GameResources.peanut, 0, 0, 0, 0, Item.ItemType.FOOD, 0, 5, 0, 0));
			generalStoreItems.add(new Item("Frozen Salad", "Try to remain healthy!", "(+25 Health when consumed)", 2, 9.99, GameResources.salad, 0, 0, 0, 0, Item.ItemType.FOOD, 25, 0, 0, 0));
			generalStoreItems.add(new Item("Small Bag", 2, "Bringing more stuff", "(+3 Inventory slots while in Inventory [Max 10])", 29.99, GameResources.sBag, 0, 0, 0, 0, 0, 0));
			ShopNPC shopkeeper = new ShopNPC(780, 170, 180, 180, GameResources.npc1, "Convenience Store", generalStoreItems, "shop_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(shopkeeper);
			System.out.println("drawn");
		}
		if (Main.getCurrentLevelIndex() == 3) {
			List<Item> generalStoreItems = new ArrayList<>();
			// Add items to shop
			generalStoreItems.add(new Item("Cheap Book", 1, "If you drop something, it will fall to the ground.", "(+5 Intelligence while in Inventory)", 7.99, GameResources.book, 0, 0, 0, 0, 5, 0));
			generalStoreItems.add(new Item("Krugman Textbook", 2, "#Econ is life!", "(+15 Intelligence while in Inventory)", 24.49, GameResources.book, 0, 0, 0, 0, 15, 0));
			generalStoreItems.add(new Item("Calculus Textbook", 2, "Did you know that a buttload is a unit of measurement? It's equal to 126 gallons!", "(+25 Intelligence while in Inventory)", 39.99, GameResources.book, 0, 0, 0, 0, 25, 0));
			generalStoreItems.add(new Item("Book of Knowledge", 3, "Makes you think deeper into the meaning of life.", "(+35 Intelligence while in Inventory)", 64.49, GameResources.book, 0, 0, 0, 0, 35, 0));
			generalStoreItems.add(new Item("Holy Bible", 4, "", "(+10 Luck and +50 Intelligence while in Inventory)", 194.99, GameResources.bible, 0, 0, 0, 0, 50, 10));
			generalStoreItems.add(new Item("Luxury Pen", 3, "Shiny.",  "(+60 Intelligence while in Inventory)", 84.99, GameResources.pen, 0, 0, 0, 0, 60, 0));
			generalStoreItems.add(new Item("Elegant Pen", 4, "More Shiny.", "(+80 Intelligence while in Inventory)", 299.99, GameResources.pen, 0, 0, 0, 0, 80, 0));
			generalStoreItems.add(new Item("4 Leaf Clover", 3, "It's your lucky day!", "(+5 Luck while in Inventory)", 29.99, GameResources.clover, 0, 0, 0, 0, 0, 5));
			generalStoreItems.add(new Item("Large Bag", 3, "Bringing extra stuff", "(+5 Inventory slots while in Inventory [Max 10])", 69.99, GameResources.lBag, 0, 0, 0, 0, 0, 0));
			ShopNPC bookShopkeeper = new ShopNPC(720, 110, 180, 180, GameResources.npc3, "Book Store", generalStoreItems, "shop_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(bookShopkeeper);
			System.out.println("drawn");
		}
		if (Main.getCurrentLevelIndex() == 4) {
			List<Item> generalStoreItems = new ArrayList<>();
			// Add items to shop
			generalStoreItems.add(new Item("Cheap Burger", "Burger designed for Marginal Benefit.", "(+5 Satisfaction and -5 Health when consumed)", 1, 4.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -5, 5, 0, 0));
			generalStoreItems.add(new Item("HamBurger", "An ordinary Hamburger. How American!", "(+20 Satisfaction and -15 Health when consumed)", 2, 7.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -15, 20, 0, 0));
			generalStoreItems.add(new Item("CheeseBurger", "Cheese inside Burger!! Mmm tasty.", "(+25 Satisfaction and -15 Health when consumed)", 2, 9.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -15, 25, 0, 0));
			generalStoreItems.add(new Item("DoubleBurger", "Burger with extra fat!", "(+35 Satisfaction and -25 Health when consumed)", 2, 13.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -25, 35, 0, 0));
			generalStoreItems.add(new Item("MegaBurger", "Burger that can one-shot your cholesterol.", "(+50 Satisfaction and -150 Health when consumed)", 3, 27.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -150, 50, 0, 0));
			generalStoreItems.add(new Item("Burger of the End", "Should reconsider eating this thing.", "(+5000 Satisfaction and -1000 Health when consumed)", 4, 49.99, GameResources.burger, 0, 0, 0, 0, Item.ItemType.FOOD, -1000, 5000, 0, 0));
			generalStoreItems.add(new Item("IceCream", "Mmm tasty.", "(+15 Satisfaction and -5 Health when consumed)", 2, 6.99, GameResources.iceCream, 0, 0, 0, 0, Item.ItemType.FOOD, -5, 15, 0, 0));

			ShopNPC mckeeper = new ShopNPC(900, 110, 180, 180, GameResources.npc2, "McDonalds", generalStoreItems, "shop_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(mckeeper);
			System.out.println("drawn");
		}
		if (Main.getCurrentLevelIndex() == 5) {
			List<Item> generalStoreItems = new ArrayList<>();
			// Add items to shop
			generalStoreItems.add(new Item("Healing Water", "Water heals anything.", "(+50 Health when consumed)", 2, 27.99, GameResources.water, 0, 0, 0, 0, Item.ItemType.FOOD, 50, 0, 0, 0));
			generalStoreItems.add(new Item("Recovery Water", "A 21st century skilled water! The world has evolved!", "(+200 Health when consumed)", 3, 94.49, GameResources.water, 0, 0, 0, 0, Item.ItemType.FOOD, 200, 0, 0, 0));
			generalStoreItems.add(new Item("Reviving Water", "Bring things back to life!", "(+800 Health when consumed)", 4, 449.99, GameResources.water, 0, 0, 0, 0, Item.ItemType.FOOD, 800, 0, 0, 0));
			generalStoreItems.add(new Item("Flower of Health", 4, "A flower can save a life!", "(Pretty while in Inventory)", 25999.99, GameResources.flower, 0, 0, 0, 0, 0, 0));
			generalStoreItems.add(new Item("Flower of Truth", 4, "A flower can teach a life!", "(+400 Intelligence while in Inventory)", 34599.99, GameResources.flower, 0, 0, 0, 0, 400, 0));
			generalStoreItems.add(new Item("St. Patrick's Clover", 4, "A clover can make a luck!", "(+50 Luck while in Inventory)", 799.99, GameResources.clover, 0, 0, 0, 0, 0, 50));

			ShopNPC dockeeper = new ShopNPC(840, -20, 180, 180, GameResources.doc, "Hospital", generalStoreItems, "hos_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(dockeeper);
			System.out.println("drawn");
		}
		if (Main.getCurrentLevelIndex() == 6) {
			List<Item> generalStoreItems = new ArrayList<>();
			ShopNPC banker = new ShopNPC(760, 90, 180, 180, GameResources.npc1, "Bank", generalStoreItems, "bank_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(banker);
			System.out.println("drawn");


		}
		if (Main.getCurrentLevelIndex() == 7) {
			List<Item> generalStoreItems = new ArrayList<>();
			ShopNPC principal = new ShopNPC(1080, 570, 180, 180, GameResources.npc3, "School", generalStoreItems, "school_buy_menu", this.dialogueTriggerListener);
			shopNPCs.add(principal);
			System.out.println("drawn");
		}
	}

	public Camera getCamera() { return camera; }

	public void onPlayerDeath() {
		if (player.playerHP == 0) {
			active = false;
			playerDead = true;
			throwPlayerDieEvent();	
		}
		
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {

				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					player.playerHP -= 10;
					player.bounce();
					onPlayerDeath();
				}
			}
			// Check for single press

			// Update shop NPCs
			for (ShopNPC npc : shopNPCs) {
				 // Pass player and interact key state
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}

	public void addBuilding(Building b) {
		buildings.add(b);
	}

	public void drawBuildings(Graphics g, float camX, float camY) {
		for (Building b : buildings) {
			b.draw(g, camX, camY);
		}
	}

	public boolean isBlocked(Vector2D position, int width, int height) {
		for (Building b : buildings) {
			if (b.intersects(position, width, height)) {
				return true;
			}
		}
		return false;
	}






	public void draw(Graphics g, int mouseX, int mouseY, Graphics2D g2d) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }

		 drawBuildings(g, camera.getX(), camera.getY());

		//draw NPC
		for (ShopNPC npc : shopNPCs) {
			npc.draw(g, player, mouseX, mouseY, g2d);

		}


	   	 // Draw the player
	   	 player.draw(g);







	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void addDroppedItem(Item item, int x, int y) {
		droppedItems.add(new DroppedItem(item, x, y));
	}

	public void renderDroppedItems(Graphics g) {
		for (DroppedItem di : droppedItems) {
			di.render(g);
		}
	}

	public void checkPlayerPickup(Player player) {
		Iterator<DroppedItem> it = droppedItems.iterator();
		Rectangle playerBounds = player.getBounds();

		while (it.hasNext()) {
			DroppedItem di = it.next();
			if (playerBounds.intersects(di.getBounds())) {
				player.getInventory().addItem(di.getItem());
				it.remove();
			}
		}
	}

	public void clearDroppedItems() {
		droppedItems.clear();
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public void clearBuildings() {
		buildings.clear();
	}

	public ArrayList<ShopNPC> getShopNPCs() {
		return shopNPCs;
	}
}