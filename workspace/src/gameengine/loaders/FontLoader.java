package gameengine.loaders;

import java.awt.Font;
import java.io.InputStream;
import java.io.File; // For fallback

public class FontLoader {

    public static Font loadFont(float size) {
        try {
            // Path to your font file relative to your project's resources
            // When running from a JAR, this path is inside the JAR
            // For example, if PokemonGB.ttf is in 'mygame/fonts/' in your JAR:
            InputStream is = FontLoader.class.getResourceAsStream("/fonts/PokemonGb.ttf");

            if (is == null) {
                // Fallback for running directly from IDE where resource paths might differ
                // This assumes "fonts/PokemonGB.ttf" is relative to your project root
                System.err.println("Font file not found in resources. Trying direct file path.");
                File fontFile = new File("/fonts/PokemonGb.ttf");
                if (fontFile.exists()) {
                    is = new java.io.FileInputStream(fontFile);
                } else {
                    System.err.println("Direct font file path also not found.");
                    // Still use fallback font
                }
            }

            if (is != null) {
                Font pokemonFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
                is.close();
                return pokemonFont;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("Error loading font. Falling back to Monospaced.");
        return new Font("Monospaced", Font.PLAIN, (int) size); // Fallback
    }
}
