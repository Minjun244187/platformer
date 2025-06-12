package gameengine.loaders;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;       // New import
import java.io.InputStreamReader; // New import
// import java.io.FileReader; // Remove this import

public class TilesetLoader {

	public static Tileset loadTileset(String filePath, BufferedImage tilesetImage) throws Exception {
		// Use try-with-resources to ensure the BufferedReader is closed automatically
		try (InputStream is = TilesetLoader.class.getClassLoader().getResourceAsStream(filePath)) {
			if (is == null) {
				// If the resource is not found, throw an exception with a clear message
				throw new java.io.FileNotFoundException("Tileset data file not found on classpath: " + filePath);
			}
			// Wrap the InputStream in an InputStreamReader to read text, then in a BufferedReader
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

			bufferedReader.readLine(); //header
			int tileSize = Integer.parseInt(bufferedReader.readLine().split("=")[1]);

			Tileset tileset = new Tileset();

			String line = bufferedReader.readLine();
			while (line != null) {
				String[] values = line.split(",");
				if(!values[0].equals("!")) {
					int id = Integer.parseInt(values[0].trim());
					int x = Integer.parseInt(values[2].trim());
					int y = Integer.parseInt(values[3].trim());
					tileset.addImage(values[1], id, tilesetImage.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize));
				}
				line = bufferedReader.readLine();
			}

			// bufferedReader.close() is handled by try-with-resources
			return tileset;

		} // The 'is' (InputStream) will also be closed by try-with-resources when bufferedReader closes.
	}
}