package gamelogic.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream; // New import
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private Map<String, Clip> audioClips;

    public SoundManager() {
        audioClips = new HashMap<>();
    }

    /**
     * Loads an audio file into memory from the classpath.
     *
     * @param name A unique name to identify this clip (e.g., "backgroundMusic", "jumpSound").
     * @param filePath The path to the audio file relative to the classpath (e.g., "music/background.wav").
     * @return true if the clip was loaded successfully, false otherwise.
     */
    public boolean loadClip(String name, String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = SoundManager.class.getClassLoader().getResourceAsStream(filePath);

            if (inputStream == null) {
                System.err.println("Audio file not found on classpath: " + filePath);
                return false;
            }

            // Wrap the input stream in a BufferedInputStream to support mark/reset operations
            InputStream bufferedIn = new BufferedInputStream(inputStream);

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn); // Use the buffered stream
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            audioClips.put(name, clip);
            System.out.println("Loaded audio clip: " + name + " from " + filePath);
            return true;
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format for " + filePath + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error loading audio file " + filePath + ": " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred loading audio " + filePath + ": " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Ensure the original input stream is closed
                } catch (IOException e) {
                    System.err.println("Error closing input stream for " + filePath + ": " + e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * Plays a loaded audio clip once.
     *
     * @param name The unique name of the clip to play.
     */
    public void play(String name) {
        Clip clip = audioClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("Clip not found: " + name);
        }
    }

    /**
     * Loops a loaded audio clip continuously.
     *
     * @param name The unique name of the clip to loop.
     */
    public void loop(String name) {
        Clip clip = audioClips.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            System.err.println("Clip not found: " + name);
        }
    }

    /**
     * Stops a playing audio clip.
     *
     * @param name The unique name of the clip to stop.
     */
    public void stop(String name) {
        Clip clip = audioClips.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Stops all currently playing audio clips and closes them.
     * This should be called when the game exits to release resources.
     */
    public void stopAllAndCleanUp() {
        for (Clip clip : audioClips.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
        audioClips.clear();
        System.out.println("All audio clips stopped and cleaned up.");
    }
}