package gamelogic;

import java.awt.Color;
import java.awt.Graphics;

import gamelogic.player.Player;
import gamelogic.tiledMap.Map;

public class HealthBar {

	private int x;
	private int y;
	private int width;
	private int height;

	private Player player;

	private float hp; //in percent

	public HealthBar(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.player = player;
	}

	public void update(float tslf) {
		Map map = player.getLevel().getMap();
		if(map != null) {
			this.hp = (player.getX() / map.getFullWidth());
		}
	}

	public void draw(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
		
		g.fillOval((int)(x + hp * width), y, height, height);
	}

}
