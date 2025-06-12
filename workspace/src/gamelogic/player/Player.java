package gamelogic.player;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import gameengine.PhysicsObject;
import gameengine.hitbox.RectHitbox;
import gameengine.loaders.ImageLoader;
import gameengine.maths.Vector2D;
import gamelogic.Main;
import gamelogic.audio.SoundManager;
import gamelogic.level.Level;
import gamelogic.tiles.*;

public class Player extends PhysicsObject {
	public float walkSpeed = 120;
	public float jumpPower = 1750;
	public int playerHP = Main.playerHP;
	private int hpCap = 1000;
	private String direction = "Front";
	private double purse = Main.purse;
	private int luck = Main.luck;
	private int satisfiction = Main.satisfiction; //in percentage
	private int intelligence = Main.intelligence;
	private int intelligenceCap = 1000;
	private String job = Main.job;
	private String healthCondition = "";
	public Inventory inventory = new Inventory(2);
	public boolean inv;
	public boolean stat;
	private BufferedImage sprite;
	public static BufferedImage heart1;
	public static BufferedImage heart2;
	public static BufferedImage heart3;
	public static BufferedImage heart4;
	public static BufferedImage heart5;
	private long lastInventoryToggleTime = 0;
	private final long inventoryToggleCooldown = 200;
	private long lastStatToggleTime = 0;
	private final long statToggleCooldown = 350;
	private final long interactionCooldown = 400;

	private int film = 1;
	private final long filmCooldown = 60;
	private long lastFilmTime = 0;
	private SoundManager soundManager = new SoundManager();
	private Vector2D lastVector;

	//building NPCs
	private boolean conv;
	private boolean book;
	private boolean hospital;
	private boolean bank;
	private boolean mcdon;
	private boolean school;


	private boolean isJumping = false;

	public Player(float x, float y, Level level) {

		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
		soundManager.loadClip("walk", "music/gallop.wav");
		soundManager.loadClip("pageIn", "music/lowhoosh.wav");
		soundManager.loadClip("pageOut", "music/hiwhoosh.wav");
		soundManager.loadClip("doorBell", "music/doorbell.wav");

		setSprite();

	}

	private void setSprite() {
		try {
			try (InputStream is = Player.class.getClassLoader().getResourceAsStream("gfx/player/Rodman" + getDirection() + getFilm() + ".png")) {
				if (is == null) {
					System.err.println("Player sprite not found: gfx/player/Rodman" + getDirection() + getFilm() + ".png");
				} else {
					sprite = ImageIO.read(is);
				}
			}
		} catch (IOException e) {
			System.err.println("Error loading player sprite: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public BufferedImage getHeart() {
		try {
			// Load heart images from classpath
			// It's highly recommended to load these once (e.g., in GameResources)
			// and store them as static fields, rather than loading them every time getHeart() is called.
			// For now, fixing the path:
			try (InputStream is1 = Player.class.getClassLoader().getResourceAsStream("gfx/ui/heart1.png");
				 InputStream is2 = Player.class.getClassLoader().getResourceAsStream("gfx/ui/heart2.png");
				 InputStream is3 = Player.class.getClassLoader().getResourceAsStream("gfx/ui/heart3.png");
				 InputStream is4 = Player.class.getClassLoader().getResourceAsStream("gfx/ui/heart4.png");
				 InputStream is5 = Player.class.getClassLoader().getResourceAsStream("gfx/ui/heart5.png")) {

				if (is1 != null) heart1 = ImageIO.read(is1); else System.err.println("Heart1 image not found.");
				if (is2 != null) heart2 = ImageIO.read(is2); else System.err.println("Heart2 image not found.");
				if (is3 != null) heart3 = ImageIO.read(is3); else System.err.println("Heart3 image not found.");
				if (is4 != null) heart4 = ImageIO.read(is4); else System.err.println("Heart4 image not found.");
				if (is5 != null) heart5 = ImageIO.read(is5); else System.err.println("Heart5 image not found.");
			}


			if (playerHP > 800) {
				return heart1;
			} else if (playerHP > 600) {
				return heart2;
			} else if (playerHP > 400) {
				return heart3;
			} else if (playerHP > 200) {
				return heart4;
			} else if (playerHP > 0){
				return heart5;
			} else {
				declareDead();
				return heart5; // Still return heart5 even if dead
			}
		} catch (IOException e) {
			System.err.println("Error loading heart images: " + e.getMessage());
			e.printStackTrace();
			return heart5; // Fallback in case of error
		}
	}
	public void setCoord(Vector2D vector) {
		this.position = vector;
	}


	//purse
	public double getPurse() {
		purse = Main.purse;
		return purse;
	}

	public void setPurse(double amount) { Main.purse = amount; }

	public int getIntelligence() { return intelligence; }

	public void changeIntelligence(int i) {
		Main.intelligence += i;
		if (Main.intelligence >= intelligenceCap) {
			Main.intelligence = intelligenceCap;
		}
	}

	public void changePurse(double amount) {
		Main.purse += amount;
	}

	public void changeBalance(double amount) { Main.balance += amount; }

	public int getSat() { return satisfiction; }

	public void changeSat(int i) {
		Main.satisfiction += i;
		if (Main.satisfiction >= 100) {
			satisfiction = 100;
		}
	}

	public int getFilm() { return film; }

	public void sleep() {
		Main.timeUpdate();
		Main.playerHP += 100;
		Main.satisfiction += 5;
		Main.intelligence -= 25;
	}


	//job/occupation
	public String getJob() {

		job = Main.job;
		return job;
	}

	public int getLuck() {
		return luck;
	}

	public void changeLuck(int amount) {
		Main.luck += amount;
	}

	public void setJob(String job) {
		Main.job = job;
	}

	public String getDirection() {
		return direction;
	}

	public Inventory getInventory() {
        return inventory;
    }




	public String getCondition() {
		playerHP = Main.playerHP;
		if (Main.playerHP > hpCap) {
			Main.playerHP = hpCap;
		}

		if (playerHP > 800) {
			return "Healthy";
		} else if (playerHP > 600) {
			return "Fine";
		} else if (playerHP > 400) {
			return "Unhealthy";
		} else if (playerHP > 200) {
			return "Critical";
		} else if (playerHP > 0){
			return "Extreme";
		} else {
			declareDead();
			return "Dead";
		}

	}

	public int getConditionLevel() {

		if (playerHP >= hpCap) {
			playerHP = hpCap;
		}

		if (playerHP > 800) {
			return 1;
		} else if (playerHP > 600) {
			return 2;
		} else if (playerHP > 400) {
			return 3;
		} else if (playerHP > 200) {
			return 4;
		} else if (playerHP > 0){
			return 5;
		} else {
			declareDead();
			return 5;
		}

	}

	@Override
	public void update(float tslf) {

		if (Main.isDead) {
			return;
		}


		super.update(tslf);
		playerHP = Main.playerHP;
		luck = Main.luck;
		satisfiction = Main.satisfiction;
		job = Main.job;
		purse = Main.purse;
		intelligence = Main.intelligence;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastFilmTime >= filmCooldown) {
			film += 1;
			lastFilmTime = currentTime;

		}

		if (film == 5) {
			film = 1;
		} else if (film == 0) {
			film = 1;
		}

		movementVector.x = 0;
		movementVector.y = 0;
		if(PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
			direction = "Left";
			setSprite();

		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
			direction = "Right";
			setSprite();

		}
		if(PlayerInput.isUpKeyDown()) {
			movementVector.y = -walkSpeed;
			direction = "Back";
			setSprite();

		}
		if(PlayerInput.isDownKeyDown()) {
			movementVector.y = +walkSpeed;
			direction = "Front";
			setSprite();

		}
		if(PlayerInput.isJumpKeyDown() && !isJumping) {
			movementVector.y = -jumpPower;
			isJumping = true;
		}
		if(PlayerInput.isIKeyDown()) {
			if (currentTime - lastInventoryToggleTime >= inventoryToggleCooldown) {
           		inv = !inv;
            	lastInventoryToggleTime = currentTime;
				System.out.println("i");
				soundManager.play("pageIn");

			}

		}
		if(PlayerInput.isLKeyDown()) {
			if (currentTime - lastStatToggleTime >= statToggleCooldown) {
           		stat = !stat;
            	lastStatToggleTime = currentTime;
				System.out.println("s");
				soundManager.play("pageIn");
			}

		}
		if(PlayerInput.isZKeyDown()) {

			if (getCollisionMatrix()[PhysicsObject.RIG] instanceof Interaction) {
				if (currentTime - Main.lasInteractionTime >= interactionCooldown) {
					Main.lasInteractionTime = currentTime;
					interact(getCollisionMatrix()[PhysicsObject.RIG].getX(), getCollisionMatrix()[PhysicsObject.RIG].getY());

				}
				System.out.println(getCollisionMatrix()[PhysicsObject.RIG].getX());
				System.out.println(getCollisionMatrix()[PhysicsObject.RIG].getY());
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.TOP] instanceof Interaction) {
				if (currentTime - Main.lasInteractionTime >= interactionCooldown) {
					Main.lasInteractionTime = currentTime;
					interact(getCollisionMatrix()[PhysicsObject.TOP].getX(), getCollisionMatrix()[PhysicsObject.TOP].getY());

				}
				System.out.println(getCollisionMatrix()[PhysicsObject.TOP].getX());
				System.out.println(getCollisionMatrix()[PhysicsObject.TOP].getY());
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.BOT] instanceof Interaction) {
				if (currentTime - Main.lasInteractionTime >= interactionCooldown) {
					Main.lasInteractionTime = currentTime;
					interact(getCollisionMatrix()[PhysicsObject.BOT].getX(), getCollisionMatrix()[PhysicsObject.BOT].getY());

				}
				System.out.println(getCollisionMatrix()[PhysicsObject.BOT].getX());
				System.out.println(getCollisionMatrix()[PhysicsObject.BOT].getY());
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.LEF] instanceof Interaction) {
				if (currentTime - Main.lasInteractionTime >= interactionCooldown) {
					Main.lasInteractionTime = currentTime;
					interact(getCollisionMatrix()[PhysicsObject.LEF].getX(), getCollisionMatrix()[PhysicsObject.LEF].getY());

				}
				System.out.println(getCollisionMatrix()[PhysicsObject.LEF].getX());
				System.out.println(getCollisionMatrix()[PhysicsObject.LEF].getY());
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.RIG] instanceof Bed) {
				Main.sleep();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.TOP] instanceof Bed) {
				Main.sleep();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.LEF] instanceof Bed) {
				Main.sleep();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.RIG] instanceof NewsBlock) {
				Main.getInstance().openNews();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.TOP] instanceof NewsBlock) {
				Main.getInstance().openNews();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.LEF] instanceof NewsBlock) {
				Main.getInstance().openNews();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.RIG] instanceof Computa) {
				Main.getInstance().openComputa();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.TOP] instanceof Computa) {
				Main.getInstance().openComputa();
				lastVector = this.position;
			}
			if (getCollisionMatrix()[PhysicsObject.LEF] instanceof Computa) {
				Main.getInstance().openComputa();
				lastVector = this.position;
			}

		}

		isJumping = true;
		if(collisionMatrix[BOT] != null) isJumping = false;
		inventory = Main.getInventory();
	}

	public Vector2D getLastVector() { return lastVector; }

	private void interact(float x, float y) {
		long currentTime = System.currentTimeMillis();
		System.out.println(currentTime - Main.lasInteractionTime);
		System.out.println(interactionCooldown);

		if (!Main.inBuilding) {
			Main.playerLastVector = this.position;
			soundManager.play("doorBell");
		}
		if((x == 540 || x == 600) && y == 360) {
			Main.inBuilding = true;
			Main.setLevel(1);
		}
		if((x == 600 || x == 660) && y == 600) {
			Main.inBuilding = false;
			Main.setLevel(0);
		}
		if((x == 1260) && y == 300) {
			Main.inBuilding = true;
			Main.setLevel(2);
		}
		if((x == 2220) && y == 240) {
			Main.inBuilding = true;
			Main.setLevel(3);
		}
		if((x == 4200) && y == 360) {
			Main.inBuilding = true;
			Main.setLevel(6);
		}
		if((x == 4980) && y == 960) {
			Main.inBuilding = true;
			Main.setLevel(7);
		}
		if((x == 3480) && y == 960) {
			Main.inBuilding = true;
			Main.setLevel(5);
		}
		if((x == 2280) && y == 1020) {
			Main.inBuilding = true;
			Main.setLevel(4);
		}
		if ((x == 480 || x == 540) && y == 480) {
			Main.inBuilding = false;
			Main.setLevel(0);
		}
		if ((x == 720 || x == 660) && y == 540) {
			Main.inBuilding = false;
			Main.setLevel(0);
		}
		if ((x == 780 || x == 840) && y == 540) {
			Main.inBuilding = false;
			Main.setLevel(0);
		}
		if ((x == 780 || x == 840) && y == 1080) {
			Main.inBuilding = false;
			Main.setLevel(0);
		}
		if (x == 840 && y == 300) {
			conv = true;

		}
		if (x == 780 && y == 240) {
			book = true;

		}
		if (x == 900 && y == 120) {
			hospital = true;

		}
		if (x == 960 && y == 240) {
			mcdon = true;

		}
		if (x == 1140 && y == 720) {
			school = true;

		}
		if (x == 840 && y == 240) {
			bank = true;

		}


	}

	public boolean getConv() { return conv; }
	public boolean getBook() { return book; }
	public boolean getMcDon() { return mcdon; }
	public boolean getHospital() { return hospital; }
	public boolean getBank() { return bank; }
	public boolean getSchool() { return school; }
	public void setConv(boolean b) { conv = b; }
	public void setBook(boolean b) { book = b; }
	public void setMcDon(boolean b) { mcdon = b; }
	public void setHospital(boolean b) { hospital = b; }
	public void setBank(boolean b) { bank = b; }
	public void setSchool(boolean b) { school = b; }

	public void bounce() {
		if (direction == "Back") {
			movementVector.y = +walkSpeed * 15;

		} else if (direction == "Front") {
			movementVector.y = -walkSpeed * 15;

		} else if (direction == "Left") {
			movementVector.x = +walkSpeed * 15;

		} else if (direction == "Right") {
			movementVector.x = -walkSpeed * 15;

		}

	}

	public void declareDead() {

	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		if (sprite != null) {
			g.drawImage(sprite, (int)getX() - 64, (int)getY() - 90, width * 3, height * 3, null); // draws scaled
		} else {
			g.setColor(Color.BLUE); // fallback
			g.fillRect((int)getX(), (int)getY(), width, height);
		}

		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}

		hitbox.draw(g);
	}

	public Rectangle getBounds() {
		if (this.hitbox instanceof RectHitbox) {
			RectHitbox rect = this.hitbox;
			return new Rectangle((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
		}
		// Default fallback if hitbox is missing
		return new Rectangle(0, 0, 32, 32);
	}

	public Object getBalance() {
		return Main.balance;
	}

	public boolean consumeItem(Item item) {
		if (item.getType() == Item.ItemType.FOOD) {
			if (inventory.hasItem(item)) { // Check if player actually possesses the item
				// Apply effects
				this.setPlayerHP(Math.min(hpCap, getPlayerHP() + item.getHealthRestore()));
				this.changeSat(item.getSatisfactionBoost());// Luck might or might not have a cap
				System.out.println("Player consumed " + item.getName() + "!");
				return true;// Remove one instance of the item


			} else {
				System.out.println("Player tried to consume " + item.getName() + " but doesn't have it!");
				return false;
				// Optional: Display an error message
			}
		} else {
			System.out.println("Cannot consume " + item.getName() + " (not a food type).");
			return false;
			// Optional: Display an error message
		}
	}

	private int getPlayerHP() {
		return Main.playerHP;
	}
	private void setPlayerHP(int amount) {
		Main.playerHP = amount;
	}
}
