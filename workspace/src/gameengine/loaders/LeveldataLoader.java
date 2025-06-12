package gameengine.loaders;

import java.io.BufferedReader;
import java.io.FileNotFoundException; // Keep or add if removed
import java.io.IOException; // Add this import for general IO errors
import java.io.InputStream; // New import for InputStream
import java.io.InputStreamReader; // New import for InputStreamReader

import gamelogic.Main;
import gamelogic.level.LevelData;

public class LeveldataLoader {

	public static LevelData loadLeveldata(String filePath) throws Exception {
		BufferedReader bufferedReader = null; // Initialize to null
		try {
			// Use ClassLoader to read the resource from the classpath (works when packaged in a JAR)
			InputStream inputStream = LeveldataLoader.class.getClassLoader().getResourceAsStream(filePath);

			if (inputStream == null) {
				// Resource not found on classpath
				throw new FileNotFoundException("Level data file not found on classpath: " + filePath);
			}

			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			int width = Integer.parseInt(bufferedReader.readLine().split("=")[1]);
			int height = Integer.parseInt(bufferedReader.readLine().split("=")[1]);
			int tileSize = Integer.parseInt(bufferedReader.readLine().split("=")[1]) * 2;
			tileSize = tileSize < Main.SCREEN_HEIGHT / 10 ? tileSize : Main.SCREEN_HEIGHT / 10;
			int[][] values = new int[width][height];

			for (int y = 0; y < height; y++) {
				String[] valuesAsString = bufferedReader.readLine().split(",");
				for (int x = 0; x < width; x++) {
					values[x][y] = Integer.parseInt(valuesAsString[x]);
				}
			}
			String[] playerPos = bufferedReader.readLine().split("=")[1].split(",");
			int playerX = Integer.parseInt(playerPos[0]);
			int playerY = Integer.parseInt(playerPos[1]);

			// Assuming Mapdata and LevelData constructors are correct
			Mapdata mapdata = new Mapdata(width, height, tileSize, values);
			LevelData leveldata = new LevelData(mapdata, playerX, playerY);

			return leveldata;

		} catch (IOException e) {
			// Catch other potential IO errors during reading
			throw new IOException("Error reading level data from stream: " + filePath, e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close(); // Ensure the reader is closed
				} catch (IOException e) {
					System.err.println("Error closing BufferedReader for " + filePath + ": " + e.getMessage());
				}
			}
		}
	}
}