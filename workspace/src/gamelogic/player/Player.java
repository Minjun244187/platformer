package gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gamelogic.Main;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;

public class Player extends PhysicsObject{
	public float walkSpeed = 240;
	public float jumpPower = 1750;
	public int playerHP = 1000;
	private String direction;
	private double purse = 0.0;
	private int luck = 0;
	private String job = "";
	private String healthCondition = "";
	public Inventory inventory = new Inventory(2);
	public boolean inv;
	public boolean stat;
	private long lastInventoryToggleTime = 0;
	private final long inventoryToggleCooldown = 200;
	private long lastStatToggleTime = 0;
	private final long statToggleCooldown = 350;

	private boolean isJumping = false;

	public Player(float x, float y, Level level) {
	
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
	}


	//purse
	public double getPurse() {
		return purse;
	}

	public void changePurse(double amount) {
		purse += amount;
	}


	//job/occupation
	public String getJob() {
		return job;
	}

	public int getLuck() {
		return luck;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getDirection() {
		return direction;
	}

	public Inventory getInventory() {
        return inventory;
    }


	public String getCondition() {
		if (playerHP > 800) {
			return "Healthy";
		} else if (playerHP > 600) {
			return "Fine";
		} else if (playerHP > 400) {
			return "Unhealthy";
		} else if (playerHP > 200) {
			return "Critical";
		} else {
			return "Extreme";
		}
	}

	@Override
	public void update(float tslf) {
		super.update(tslf);
		long currentTime = System.currentTimeMillis();
		
		movementVector.x = 0;
		movementVector.y = 0;
		if(PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
			direction = "west";
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
			direction = "east";
		}
		if(PlayerInput.isUpKeyDown()) {
			movementVector.y = -walkSpeed;
			direction = "north";
		}
		if(PlayerInput.isDownKeyDown()) {
			movementVector.y = +walkSpeed;
			direction = "south";
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
			}

		}
		if(PlayerInput.isLKeyDown()) {
			
			if (currentTime - lastStatToggleTime >= statToggleCooldown) {
           		stat = !stat;
            	lastStatToggleTime = currentTime;
				System.out.println("s");
			}

		}
		
		isJumping = true;
		if(collisionMatrix[BOT] != null) isJumping = false;
	}
	public void bounce() {
		if (direction == "north") {
			movementVector.y = +walkSpeed * 15;

		} else if (direction == "south") {
			movementVector.y = -walkSpeed * 15;

		} else if (direction == "west") {
			movementVector.x = +walkSpeed *15;

		} else if (direction == "east") {
			movementVector.x = -walkSpeed *15;

		}
		
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(Color.YELLOW);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		
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
}
