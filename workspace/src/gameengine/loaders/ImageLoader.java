/*
 * */
package gameengine.loaders;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException; // Import IOException
import java.io.InputStream; // Import InputStream

import javax.imageio.ImageIO;

/**
 * * @author Paul Kappmeyer & Daniel Lucarz
 *
 */
public final class ImageLoader {

	/**
	 * Loads and returns a BufferedImage from a specified source (now from classpath for JAR compatibility)
	 * @param path The path to load the BufferedImage from (relative to the classpath, e.g., "images/my_image.png")
	 * @return The BufferedImage
	 * @throws Exception if the file could not be found or an I/O error occurs
	 */
	public static BufferedImage loadImage(String path) throws Exception {
		// Use ClassLoader to read the resource from the classpath (works when packaged in a JAR)
		
		InputStream inputStream = ImageLoader.class.getClassLoader().getResourceAsStream(path);

		if (inputStream == null) {
			// Resource not found on classpath
			throw new FileNotFoundException("Image file not found on classpath: " + path);
		}

		BufferedImage image = null;
		try {
			image = ImageIO.read(inputStream);
		} catch (IOException e) {
			// Handle other IO errors during image reading
			throw new IOException("Error reading image from stream: " + path, e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close(); // Ensure the stream is closed
				} catch (IOException e) {
					System.err.println("Error closing input stream for " + path + ": " + e.getMessage());
				}
			}
		}

		if (image == null) {
			throw new Exception("Could not read image: " + path + ". It might be corrupted or an unsupported format.");
		}

		return image;
	}
}