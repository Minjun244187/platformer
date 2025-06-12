package gamelogic.world;

public interface DialogueTriggerListener {
    /**
     * Called when a game entity wants to trigger a specific dialogue.
     * @param dialogueNodeId The ID of the dialogue node to start.
     * @param triggeringNPC The ShopNPC instance that triggered this dialogue.
     */
    void onTriggerDialogue(String dialogueNodeId, ShopNPC triggeringNPC); // ADDED 'ShopNPC triggeringNPC'

    /**
     * Checks if the game's logic is currently paused due to an active chat/dialogue.
     * @return true if a dialogue is active and pausing game logic, false otherwise.
     */
    boolean isGameLogicPausedForChat();
}