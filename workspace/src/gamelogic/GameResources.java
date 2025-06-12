package gamelogic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import gameengine.loaders.ImageLoader;
import gameengine.loaders.Tileset;
import gameengine.loaders.TilesetLoader;

public final class GameResources {
	// Graphics
	public static Tileset tileset;
	public static BufferedImage enemy;
	public static BufferedImage logo;
	public static BufferedImage house;
	public static BufferedImage hospital;
	public static BufferedImage bank;
	public static BufferedImage mcdon;
	public static BufferedImage conv;
	public static BufferedImage bookStore;
	public static BufferedImage emptyStore;
	public static BufferedImage board;
	public static BufferedImage school;
	public static BufferedImage luxury;
	public static BufferedImage luxury2;
	public static BufferedImage calendar;
	public static BufferedImage heart1;
	public static BufferedImage heart2;
	public static BufferedImage heart3;
	public static BufferedImage heart4;
	public static BufferedImage heart5;
	public static BufferedImage switcherOff;
	public static BufferedImage switcherOn;
	public static BufferedImage info;
	public static BufferedImage cash;
	public static BufferedImage book;
	public static BufferedImage water;
	public static BufferedImage bread;
	public static BufferedImage bible;
	public static BufferedImage burger;
	public static BufferedImage check;
	public static BufferedImage chips;
	public static BufferedImage clover;
	public static BufferedImage flower;
	public static BufferedImage gold;
	public static BufferedImage iceCream;
	public static BufferedImage lBag;
	public static BufferedImage sBag;
	public static BufferedImage peanut;
	public static BufferedImage pen;
	public static BufferedImage phone;
	public static BufferedImage salad;
	public static BufferedImage npc1;
	public static BufferedImage npc2;
	public static BufferedImage npc3;
	public static BufferedImage doc;
	public static BufferedImage newsPresident;
	public static BufferedImage newsRise;
	public static BufferedImage newsFall;


	public static BufferedImage titleBackground;


	// Fonts
	private static Map<String, Font> fonts = new HashMap<>();

	public static void load() {
		try {
			// Existing loads - Corrected paths to use forward slashes and no leading '.\'
			tileset = TilesetLoader.loadTileset("gfx/tileset.txt", ImageLoader.loadImage("gfx/tileset.png"));
			enemy = ImageLoader.loadImage("gfx/Enemy.png");
			logo = ImageLoader.loadImage("gfx/ui/logo.png");

			// Buildings
			house = ImageLoader.loadImage("gfx/buildings/b_house.png");
			hospital = ImageLoader.loadImage("gfx/buildings/b_hospital.png");
			conv = ImageLoader.loadImage("gfx/buildings/b_convstore.png");
			mcdon = ImageLoader.loadImage("gfx/buildings/b_mcdonalds.png");
			bank = ImageLoader.loadImage("gfx/buildings/b_bank.png");
			bookStore = ImageLoader.loadImage("gfx/buildings/b_bookstore.png");
			board = ImageLoader.loadImage("gfx/buildings/b_billboard.png");
			emptyStore = ImageLoader.loadImage("gfx/buildings/b_empty.png");
			school = ImageLoader.loadImage("gfx/buildings/b_school.png");
			luxury = ImageLoader.loadImage("gfx/buildings/b_luxuryapartment.png");
			luxury2 = ImageLoader.loadImage("gfx/buildings/b_luxuryapartment2.png");

			//UI
			calendar = ImageLoader.loadImage("gfx/ui/calendar.png");
			cash = ImageLoader.loadImage("gfx/ui/cash_symbol.png");
			heart1 = ImageLoader.loadImage("gfx/ui/heart1.png");
			heart2 = ImageLoader.loadImage("gfx/ui/heart2.png");
			heart3 = ImageLoader.loadImage("gfx/ui/heart3.png");
			heart4 = ImageLoader.loadImage("gfx/ui/heart4.png");
			heart5 = ImageLoader.loadImage("gfx/ui/heart5.png");
			info = ImageLoader.loadImage("gfx/ui/info.png");
			switcherOff = ImageLoader.loadImage("gfx/ui/switch_off.png");
			// Corrected the duplicate line: was 'switcherOff = ImageLoader.loadImage(".\\gfx\\ui\\switch_on.png");'
			switcherOn = ImageLoader.loadImage("gfx/ui/switch_on.png");
			water = ImageLoader.loadImage("gfx/items/item_newbottle.png");
			book = ImageLoader.loadImage("gfx/items/item_book.png");
			bread = ImageLoader.loadImage("gfx/items/item_bread.png");
			bible = ImageLoader.loadImage("gfx/items/item_bible.png");
			burger = ImageLoader.loadImage("gfx/items/item_burger.png");
			check = ImageLoader.loadImage("gfx/items/item_check.png");
			chips = ImageLoader.loadImage("gfx/items/item_chips.png");
			clover = ImageLoader.loadImage("gfx/items/item_clover.png");
			flower = ImageLoader.loadImage("gfx/items/item_flower.png");
			gold = ImageLoader.loadImage("gfx/items/item_gold.png");
			iceCream = ImageLoader.loadImage("gfx/items/item_icecream.png");
			lBag = ImageLoader.loadImage("gfx/items/item_lgbag.png");
			sBag = ImageLoader.loadImage("gfx/items/item_smbag.png");
			peanut = ImageLoader.loadImage("gfx/items/item_newpeanut.png");
			salad = ImageLoader.loadImage("gfx/items/item_salad.png");
			pen = ImageLoader.loadImage("gfx/items/item_pen.png");
			npc1 = ImageLoader.loadImage("gfx/npc/npc1.png");
			npc2 = ImageLoader.loadImage("gfx/npc/npc2.png");
			npc3 = ImageLoader.loadImage("gfx/npc/npc3.png");

			doc = ImageLoader.loadImage("gfx/npc/npcDoc.png");
			newsFall = ImageLoader.loadImage("gfx/news/news_fall.png");
			// Added missing news images based on naming convention
			newsPresident = ImageLoader.loadImage("gfx/news/news_president.png");
			newsRise = ImageLoader.loadImage("gfx/news/news_rise.png");


		} catch (Exception e) {
			e.printStackTrace();
			// Create fallback logo
			logo = createTextImage("ECONOMIA", 400, 100, Color.WHITE);
		}
	}


	// Utility methods


	private static BufferedImage createTextImage(String text, int width, int height, Color color) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(color);
		g2d.setFont(new Font("Arial", Font.BOLD, 48));

		FontMetrics fm = g2d.getFontMetrics();
		int x = (width - fm.stringWidth(text)) / 2;
		int y = ((height - fm.getHeight()) / 2) + fm.getAscent();

		g2d.drawString(text, x, y);
		g2d.dispose();
		return img;
	}


}