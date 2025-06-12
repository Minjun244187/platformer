package gamelogic.world; // Create a new 'dialogue' package inside 'gamelogic'

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gamelogic.audio.SoundManager;// For optional actions

public class DialogueNode {
    private final String id; // Unique ID for this node (e.g., "intro_start", "quest_accepted")
    private final String message;
    private final List<String> options; // Null if no options
    private final Map<Integer, String> nextNodeIdsForOptions; // Maps option index to next node ID
    private String defaultNextNodeId; // For simple text advancement (e.g., press Z to continue)
    private Consumer<Object> onEnterAction; // Optional action to run when entering this node
    private Consumer<Object> onExitAction;  // Optional action to run when exiting this node
    private SoundManager sm;
    /**
     * Constructor for a simple dialogue node (text only, no options).
     * @param id A unique identifier for this dialogue node.
     * @param message The message to display.
     */
    public DialogueNode(String id, String message) {
        this(id, message, null);
    }

    /**
     * Constructor for a dialogue node with options.
     * @param id A unique identifier for this dialogue node.
     * @param message The message to display.
     * @param options A list of strings representing the choices the player can make.
     */
    public DialogueNode(String id, String message, List<String> options) {
        this.id = id;
        this.message = message;
        this.options = options != null ? Collections.unmodifiableList(options) : null;
        this.nextNodeIdsForOptions = options != null ? new HashMap<>() : null;


    }

    public String getId() {
        return id;
    }

    public String getMessage() {

        return message;

    }

    public List<String> getOptions() {
        return options;
    }

    public boolean hasOptions() {
        return options != null && !options.isEmpty();
    }

    /**
     * Sets the next dialogue node ID for a specific option.
     * Only relevant if this node has options.
     * @param optionIndex The 0-based index of the option.
     * @param nextNodeId The ID of the next dialogue node.
     */
    public void setNextNodeForOption(int optionIndex, String nextNodeId) {
        if (hasOptions() && optionIndex >= 0 && optionIndex < options.size()) {
            nextNodeIdsForOptions.put(optionIndex, nextNodeId);

        } else if (!hasOptions()) {
            System.err.println("Warning: Attempted to set option-specific next node on a node with no options: " + id);
        } else {
            System.err.println("Warning: Option index out of bounds for node " + id + ": " + optionIndex);
        }
    }

    /**
     * Gets the ID of the next dialogue node based on the selected option.
     * @param optionIndex The 0-based index of the selected option.
     * @return The ID of the next dialogue node, or null if no mapping exists for this option.
     */
    public String getNextNodeIdForOption(int optionIndex) {
        if (hasOptions()) {

            return nextNodeIdsForOptions.get(optionIndex);
        }
        return null;
    }

    /**
     * Sets the default next dialogue node ID for simple text advancement.
     * Only relevant if this node has no options.
     * @param defaultNextNodeId The ID of the next dialogue node.
     */
    public void setDefaultNextNodeId(String defaultNextNodeId) {
        if (!hasOptions()) {
            this.defaultNextNodeId = defaultNextNodeId;

        } else {
            System.err.println("Warning: Attempted to set default next node on a node with options: " + id);
        }
    }

    /**
     * Gets the default next dialogue node ID.
     * @return The ID of the next dialogue node, or null if not set.
     */
    public String getDefaultNextNodeId() {

        return defaultNextNodeId;
    }

    /**
     * Sets an action to be performed when this dialogue node is entered.
     * The `Object` parameter can be used to pass a reference to your `Main` class
     * or a context object if the action needs to interact with game state.
     * @param action A Consumer that takes an Object (e.g., your Main instance).
     */
    public void setOnEnterAction(Consumer<Object> action) {
        this.onEnterAction = action;
    }

    public Consumer<Object> getOnEnterAction() {
        return onEnterAction;
    }

    /**
     * Sets an action to be performed when this dialogue node is exited (either by advancing or selecting an option).
     * @param action A Consumer that takes an Object (e.g., your Main instance).
     */
    public void setOnExitAction(Consumer<Object> action) {
        this.onExitAction = action;
    }

    public Consumer<Object> getOnExitAction() {
        return onExitAction;
    }
}