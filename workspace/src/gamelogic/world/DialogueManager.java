package gamelogic.world;

import gamelogic.Main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;




// This class will define and hold all your game's dialogues
public class DialogueManager {
    private String item = "";
    private Double price = 0.0;



    private final Map<String, DialogueNode> dialogueNodes;

    public DialogueManager() {
        this.dialogueNodes = new HashMap<>();
        loadAllDialogues(); // Populate the dialogue tree
    }

    // This is where you define all your dialogue nodes and their connections
    private void loadAllDialogues() {
        // --- Tutorial ---
        DialogueNode introStart = new DialogueNode("intro_start", "Welcome to Economia! I am your assistant AI, chatABC. Do you wish to hear the tutorial?",
                Arrays.asList("Yes, please!", "No, I know everything."));
        introStart.setNextNodeForOption(0, "tutorial_part1");
        introStart.setNextNodeForOption(1, "game_start_no_tutorial");
        dialogueNodes.put(introStart.getId(), introStart);

        DialogueNode tutorialPart1 = new DialogueNode("tutorial_part1", "Excellent choice! The game is all about making money. The government provided you some money to start with!");
        tutorialPart1.setDefaultNextNodeId("tutorial_part2");
        dialogueNodes.put(tutorialPart1.getId(), tutorialPart1);

        DialogueNode tutorialPart2 = new DialogueNode("tutorial_part2", "You can interact with different things, just walk up to them and press Enter! You can also hold items in your inventory, and these will change your status.",
                Arrays.asList("Got it!", "Inventory?", "What are my status?", "How do I move again?"));
        tutorialPart2.setNextNodeForOption(0, "game_start_tutorial_done");
        tutorialPart2.setNextNodeForOption(1, "tutorial_inventory_explanation");
        tutorialPart2.setNextNodeForOption(2, "tutorial_status_explanation");
        tutorialPart2.setNextNodeForOption(3, "tutorial_move");
        dialogueNodes.put(tutorialPart2.getId(), tutorialPart2);

        DialogueNode tutorialMove = new DialogueNode("tutorial_move", "You can move yourself with W, A, S, D keys on your keyboard! Press Z to proceed the chat!");
        tutorialMove.setDefaultNextNodeId("tutorial_main");
        dialogueNodes.put(tutorialMove.getId(), tutorialMove);

        DialogueNode tutorialMain = new DialogueNode("tutorial_main", "Would you like to clarify anything else?",
                Arrays.asList("No, Let Me Play!", "Inventory?", "What are my status?", "How do I move?"));
        tutorialMain.setNextNodeForOption(0, "game_start_tutorial_done");
        tutorialMain.setNextNodeForOption(1, "tutorial_inventory_explanation");
        tutorialMain.setNextNodeForOption(2, "tutorial_status_explanation");
        tutorialPart2.setNextNodeForOption(3, "tutorial_move");
        dialogueNodes.put(tutorialMain.getId(), tutorialMain);

        DialogueNode tutorialInventoryExplanation = new DialogueNode("tutorial_inventory_explanation", "Inventory is your personal storage to hold items.");
        tutorialInventoryExplanation.setDefaultNextNodeId("tutorial_inventory_explanation2");
        dialogueNodes.put(tutorialInventoryExplanation.getId(), tutorialInventoryExplanation);

        DialogueNode tutorialInventoryExplanation2 = new DialogueNode("tutorial_inventory_explanation2", "You will first start with 2 slots, but these could improve later on. Press the 'I' key on your keyboard to open them!");
        tutorialInventoryExplanation2.setDefaultNextNodeId("tutorial_main");
        dialogueNodes.put(tutorialInventoryExplanation2.getId(), tutorialInventoryExplanation2);

        DialogueNode tutorialStatusExplanation = new DialogueNode("tutorial_status_explanation", "You can view your status by pressing L on your keyboard. There you can see various stats. Would you like to learn more?",
                Arrays.asList("I know what I'm doing!", "Health?", "Satisfaction?", "Luck?"));
        tutorialStatusExplanation.setNextNodeForOption(0, "game_start_tutorial_done");
        tutorialStatusExplanation.setNextNodeForOption(1, "health_tutorial");
        tutorialStatusExplanation.setNextNodeForOption(2, "sat_tutorial");
        tutorialStatusExplanation.setNextNodeForOption(3, "luck_tutorial");
        dialogueNodes.put(tutorialStatusExplanation.getId(), tutorialStatusExplanation);

        DialogueNode sta1 = new DialogueNode("health_tutorial", "Health is most important, even more than money! The game will end when your health goes really bad. Make sure to stay healthy!");
        sta1.setDefaultNextNodeId("tutorial_status_explanation");
        dialogueNodes.put(sta1.getId(), sta1);

        DialogueNode sta2 = new DialogueNode("sat_tutorial", "Your satisfaction protects you from stress. If you get too much stress, your satisfaction won't protect you, and your health will decline.");
        sta2.setDefaultNextNodeId("tutorial_status_explanation");
        dialogueNodes.put(sta2.getId(), sta2);

        DialogueNode sta3 = new DialogueNode("luck_tutorial", "Feeling lucky? Unfortunately, luck still has no direct impact in the game. Shame on the developer!");
        sta3.setDefaultNextNodeId("tutorial_status_explanation");
        dialogueNodes.put(sta3.getId(), sta3);

        DialogueNode tutorialProfitExplanation = new DialogueNode("tutorial_profit_explanation", "Profit is the money you make after selling an item, minus the cost of production. Make lots of it!");
        tutorialProfitExplanation.setDefaultNextNodeId("game_start_tutorial_done");
        dialogueNodes.put(tutorialProfitExplanation.getId(), tutorialProfitExplanation);

        DialogueNode noMoney = new DialogueNode("no_money", "You don't have enough money!");
        noMoney.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(noMoney.getId(), noMoney);

        DialogueNode purchased = new DialogueNode("purchased", "Paying...");
        purchased.setOnEnterAction(gameContext -> {
            item = Main.getInstance().lastPurchasedItem;
            price = Main.getInstance().lastPurchasedCost;
            System.out.println(item);
        });
        purchased.setDefaultNextNodeId("purchased2");
        dialogueNodes.put(purchased.getId(), purchased);

        DialogueNode purchased2 = new DialogueNode("purchased2", "You bought an item!");
        purchased2.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(purchased2.getId(), purchased2);


        DialogueNode invFull = new DialogueNode("inventory_full", "Your inventory is full!");
        invFull.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(invFull.getId(), invFull);

        DialogueNode gameStartNoTutorial = new DialogueNode("game_start_no_tutorial", "Very well then, proceed with caution. The world is full of takers!");
        gameStartNoTutorial.setDefaultNextNodeId("game_ready"); // Marks the end of this dialogue path
        dialogueNodes.put(gameStartNoTutorial.getId(), gameStartNoTutorial);

        DialogueNode gameStartTutorialDone = new DialogueNode("game_start_tutorial_done", "You're all set! I will be around for more support! Try looking around the buildings first!");
        gameStartTutorialDone.setDefaultNextNodeId("game_ready"); // Marks the end of this dialogue path
        dialogueNodes.put(gameStartTutorialDone.getId(), gameStartTutorialDone);

        // --- Post-tutorial Dialogue Example (New Custom Dialogue) ---
        DialogueNode npcQuestStart = new DialogueNode("npc1_quest_start", "Greetings, gentleman! I have a tale to tell.");
        npcQuestStart.setDefaultNextNodeId("npc1_quest_story");
        dialogueNodes.put(npcQuestStart.getId(), npcQuestStart);

        DialogueNode npcQuestStory = new DialogueNode("npc1_quest_story", "I was once a investor, now ended up in streets!");
        npcQuestStory.setDefaultNextNodeId("npc1_quest_ask_for_help");
        dialogueNodes.put(npcQuestStory.getId(), npcQuestStory);

        DialogueNode npcQuestAskForHelp = new DialogueNode("npc1_quest_ask_for_help", "But I'm a bit parched. Could you bring me some water?",
                Arrays.asList("Yes, I'll help!", "Sorry, I'm busy."));
        npcQuestAskForHelp.setNextNodeForOption(0, "npc1_quest_accepted");
        npcQuestAskForHelp.setNextNodeForOption(1, "npc1_quest_declined");
        // Example of an action to run when player accepts quest
        npcQuestAskForHelp.setOnEnterAction(gameContext -> {
            // This 'gameContext' would be your Main instance or a specific game state manager
            // You can cast it and set a quest flag, e.g., ((Main)gameContext).setQuestStatus("fetch_water", "started");
            System.out.println("NPC: Player asked for help with water.");
        });
        dialogueNodes.put(npcQuestAskForHelp.getId(), npcQuestAskForHelp);

        DialogueNode npcQuestAccepted = new DialogueNode("npc1_quest_accepted", "Thank you, gentleman! The water can be bought in some stores");
        npcQuestAccepted.setDefaultNextNodeId("game_ready"); // Dialogue ends, quest started
        // Example of an action to run when player accepts quest
        npcQuestAccepted.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
        });
        dialogueNodes.put(npcQuestAccepted.getId(), npcQuestAccepted);

        DialogueNode npcQuestDeclined = new DialogueNode("npc1_quest_declined", "Ah, what a pity. Perhaps another time, then.");
        npcQuestDeclined.setDefaultNextNodeId("game_ready"); // Dialogue ends
        dialogueNodes.put(npcQuestDeclined.getId(), npcQuestDeclined);

        // --- Other Custom Dialogues can go here ---
        DialogueNode shopkeeperGreeting = new DialogueNode("shop_greeting", "Welcome, customer! May I help you sir?",
                Arrays.asList("Buy items", "Work Part-time", "Never Mind"));
        shopkeeperGreeting.setNextNodeForOption(0, "shop_buy_menu");
        System.out.println(Main.workChance);
        shopkeeperGreeting.setNextNodeForOption(1, "shop_work");
        shopkeeperGreeting.setNextNodeForOption(2, "shop_farewell");
        dialogueNodes.put(shopkeeperGreeting.getId(), shopkeeperGreeting);

        DialogueNode mcGreeting = new DialogueNode("mc_greeting", "Welcome, customer! May I take your order?",
                Arrays.asList("Order", "Work Part-time", "Never Mind"));
        mcGreeting.setNextNodeForOption(0, "shop_buy_menu");
        mcGreeting.setNextNodeForOption(1, "mc_work");
        mcGreeting.setNextNodeForOption(2, "shop_farewell");
        dialogueNodes.put(mcGreeting.getId(), mcGreeting);

        DialogueNode mcWorkQuestion = new DialogueNode("mc_work_question", "");
        mcWorkQuestion.setDefaultNextNodeId("mc_work_confirmed");
        mcWorkQuestion.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
            Main.getInstance().canWork("mc");
        });
        dialogueNodes.put(mcWorkQuestion.getId(), mcWorkQuestion);

        DialogueNode shopWorkQuestion = new DialogueNode("shop_work_question", "");
        shopWorkQuestion.setDefaultNextNodeId("shop_work_confirmed");
        shopWorkQuestion.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
            Main.getInstance().canWork("shop");
        });
        dialogueNodes.put(shopWorkQuestion.getId(), shopWorkQuestion);

        DialogueNode mcWork = new DialogueNode("mc_work", "You will lose your current job. Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        mcWork.setNextNodeForOption(0, "mc_work_question");
        mcWork.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(mcWork.getId(), mcWork);

        DialogueNode bookKeeperGreeting = new DialogueNode("book_greeting", "Welcome, customer! May I help you sir?",
                Arrays.asList("Buy items", "Never Mind"));
        bookKeeperGreeting.setNextNodeForOption(0, "shop_buy_menu");
        bookKeeperGreeting.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(bookKeeperGreeting.getId(), bookKeeperGreeting);

        DialogueNode hosGreeting = new DialogueNode("hospital_greeting", "Hello patient, please be patient.",
                Arrays.asList("Buy items", "Sleep", "Never Mind"));
        hosGreeting.setNextNodeForOption(0, "shop_buy_menu");
        hosGreeting.setNextNodeForOption(1, "sleeper");
        hosGreeting.setNextNodeForOption(2, "shop_farewell");
        dialogueNodes.put(hosGreeting.getId(), hosGreeting);

        DialogueNode sleeper = new DialogueNode("sleeper", "Would you like to sleep now and move onto the next month?",
                Arrays.asList("Yes", "No"));
        sleeper.setNextNodeForOption(0, "sleep");
        sleeper.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(sleeper.getId(), sleeper);

        DialogueNode warQ = new DialogueNode("warQ", "The College Board has pushed us up to our town! Would you like to join the fight or move onto the next month?",
                Arrays.asList("Join War", "Sleep"));
        warQ.setNextNodeForOption(0, "war");
        warQ.setNextNodeForOption(1, "sleep");
        dialogueNodes.put(warQ.getId(), warQ);

        DialogueNode war = new DialogueNode("war", "Get Ready. For Rodmania!");
        war.setDefaultNextNodeId("game_ready");
        war.setOnEnterAction(gameContext -> {
            Main.getInstance().equipFireArm();

        });
        dialogueNodes.put(war.getId(), war);

        DialogueNode sleep = new DialogueNode("sleep", "You feel very sleepy...");
        sleep.setDefaultNextNodeId("game_ready");
        sleep.setOnEnterAction(gameContext -> {
            System.out.println("sleep!");
            long currentTime = System.currentTimeMillis();
            Main.getInstance().startFadeOut(0.5f, () -> {
                System.out.println("Transitioned to black!");
                Main.isSleeping = true;

                Main.timeUpdate();
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(0.2f, () -> {
                    System.out.println("Faded back into new scene!");
                    Main.isSleeping = false;
                });
            });
        });
        dialogueNodes.put(sleep.getId(), sleep);


        DialogueNode bankGreeting = new DialogueNode("bank_greeting", "Welcome, how may I help you?",
                Arrays.asList("Access Bank Account", "Never Mind"));
        bankGreeting.setNextNodeForOption(0, "bank_account");
        bankGreeting.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(bankGreeting.getId(), bankGreeting);

        DialogueNode bankAcc = new DialogueNode("bank_account", "Here is your account!"); // Set text to null as it's an action node
        bankAcc.setOnEnterAction(gameContext -> {
            // When this node is entered, try to open the shop GUI
            if (gameContext instanceof Main) {
                Main main = (Main) gameContext;
                main.lastInteractedShopNPC = main.getCurrentLevel().getShopNPCs().get(0);
                // Use the lastInteractedShopNPC to ensure the correct shop GUI is opened
                if (main.lastInteractedShopNPC != null) {
                    main.lastInteractedShopNPC.openBank(); // Call the NPC's method to open its specific GUI// Hide the dialogue box
                    // Keep game logic paused for chat, as the shop GUI might require it, or set a new state for shop.
                    // Main.java's isGameLogicPausedForChat will now also need to check if a shop GUI is open.
                    System.out.println("Dialogue: Triggered shop GUI for " + main.lastInteractedShopNPC.getShopGUI().getTitle());
                } else {
                    System.out.println("Dialogue: No last interacted NPC found to open shop.");
                }
            }
        });
        bankAcc.setDefaultNextNodeId("game_ready"); // After executing action, set to 'game_ready' to end dialogue
        dialogueNodes.put(bankAcc.getId(), bankAcc);

        DialogueNode shopFarewell = new DialogueNode("shop_farewell", "Alright! Let me know if you need anything!");
        shopFarewell.setDefaultNextNodeId("game_ready");
        shopFarewell.setOnEnterAction(gameContext -> {
            long currentTime = System.currentTimeMillis();
            Main.lastChatTriggerTime = currentTime;
        });// Return to game
        dialogueNodes.put(shopFarewell.getId(), shopFarewell);

        DialogueNode bankrupt = new DialogueNode("bankrupt", "Sorry, but our bank is out of money. Please come back next time.");
        bankrupt.setDefaultNextNodeId("game_ready"); // Return to game
        dialogueNodes.put(bankrupt.getId(), bankrupt);

        DialogueNode shopBuyMenu = new DialogueNode("shop_buy_menu", "Take a look at what we have!"); // Set text to null as it's an action node
        shopBuyMenu.setOnEnterAction(gameContext -> {
            // When this node is entered, try to open the shop GUI
            if (gameContext instanceof Main) {
                Main main = (Main) gameContext;
                main.lastInteractedShopNPC = main.getCurrentLevel().getShopNPCs().get(0);
                // Use the lastInteractedShopNPC to ensure the correct shop GUI is opened
                if (main.lastInteractedShopNPC != null) {
                    main.lastInteractedShopNPC.openShop(); // Call the NPC's method to open its specific GUI// Hide the dialogue box
                    // Keep game logic paused for chat, as the shop GUI might require it, or set a new state for shop.
                    // Main.java's isGameLogicPausedForChat will now also need to check if a shop GUI is open.
                    System.out.println("Dialogue: Triggered shop GUI for " + main.lastInteractedShopNPC.getShopGUI().getTitle());
                } else {
                    System.out.println("Dialogue: No last interacted NPC found to open shop.");
                }
            }
        });
        shopBuyMenu.setDefaultNextNodeId("game_ready"); // After executing action, set to 'game_ready' to end dialogue
        dialogueNodes.put(shopBuyMenu.getId(), shopBuyMenu);

        // Add more nodes as needed...

        DialogueNode shopWork = new DialogueNode("shop_work", "You will lose your current job. Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        shopWork.setNextNodeForOption(0, "shop_work_question");
        shopWork.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(shopWork.getId(), shopWork);

        DialogueNode mcWorkConfirm = new DialogueNode("mc_work_confirmed", "Well, then! You are hired! Get to work!");
        mcWorkConfirm.setOnEnterAction(gameContext -> {
            switch (Main.hasWorkChance()){
                case ("true") -> mcWorkConfirm.setDefaultNextNodeId("load_MC_Work");
                case ("false") -> mcWorkConfirm.setDefaultNextNodeId("no_work");
            }
        });
        dialogueNodes.put(mcWorkConfirm.getId(), mcWorkConfirm);

        DialogueNode loadMC = new DialogueNode("load_MC_Work", "Your Work Report is out.");
        loadMC.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
            Main.getInstance().startFadeOut(1.0f, () -> {
                // This code runs when the screen is completely black.
                // Good place to change levels, teleport player, load new assets, etc.
                System.out.println("Transitioned to black!");
                Main.labor(95, 40);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(1.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        loadMC.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(loadMC.getId(), loadMC);

        DialogueNode loadShop = new DialogueNode("load_shop_work", "Your Work Report is out.");
        loadShop.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
            Main.getInstance().startFadeOut(1.0f, () -> {
                // This code runs when the screen is completely black.
                // Good place to change levels, teleport player, load new assets, etc.
                System.out.println("Transitioned to black!");
                Main.labor(85, 30);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(1.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        loadShop.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(loadShop.getId(), loadShop);

        DialogueNode loadTeach = new DialogueNode("teacher_work_confirmed", "Your Work Report is out.");
        loadTeach.setOnEnterAction(gameContext -> {
            // Here you would actually start the quest in your game state
            // e.g., ((Main)gameContext).startQuest("fetch_water");
            System.out.println("NPC: Quest 'Fetch Water' accepted!");
            Main.getInstance().startFadeOut(1.0f, () -> {
                // This code runs when the screen is completely black.
                // Good place to change levels, teleport player, load new assets, etc.
                System.out.println("Transitioned to black!");
                Main.labor(185, 40);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(1.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        loadTeach.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(loadTeach.getId(), loadTeach);


        DialogueNode shopWorkConfirm = new DialogueNode("shop_work_confirmed", "Well, then! You are hired! Get to work!");
        shopWorkConfirm.setOnEnterAction(gameContext -> {
            switch (Main.hasWorkChance()){
                case ("true") -> shopWorkConfirm.setDefaultNextNodeId("load_shop_work");
                case ("false") -> shopWorkConfirm.setDefaultNextNodeId("no_work");
            }
        });
        dialogueNodes.put(shopWorkConfirm.getId(), shopWorkConfirm);

        DialogueNode teachWorkConfirm = new DialogueNode("teacher_pass", "Well, then! You are hired Mister! Now get to Work!");
        teachWorkConfirm.setOnEnterAction(gameContext -> {
            switch (Main.hasWorkChance()){
                case ("true") -> teachWorkConfirm.setDefaultNextNodeId("teacher_work_confirmed");
                case ("false") -> teachWorkConfirm.setDefaultNextNodeId("no_work");
            }
        });
        dialogueNodes.put(teachWorkConfirm.getId(), teachWorkConfirm);

        DialogueNode noWork = new DialogueNode("no_work", "You can't work anymore on this month! Come back next month!");
        noWork.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(noWork.getId(), noWork);

        DialogueNode schoolGreeting = new DialogueNode("school_greeting", "Hello! How can I help you?",
                Arrays.asList("Take Classes", "Work as a teacher", "Never Mind"));
        schoolGreeting.setNextNodeForOption(0, "classes_menu");
        schoolGreeting.setNextNodeForOption(1, "school_work");
        schoolGreeting.setNextNodeForOption(2, "shop_farewell");
        dialogueNodes.put(schoolGreeting.getId(), schoolGreeting);

        DialogueNode schoolWork = new DialogueNode("school_work", "You might lose your current job. Also, you need at least 150 intelligence to be a teacher. Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        schoolWork.setNextNodeForOption(0, "teacher_tryout");
        schoolWork.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(schoolWork.getId(), schoolWork);

        DialogueNode sorryJob = new DialogueNode("not_pass", "Sorry, but you are not applicable for this job.");
        sorryJob.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(sorryJob.getId(), sorryJob);

        DialogueNode teacherQ = new DialogueNode("teacher_tryout", "One moment please, checking your intelligence...");
        teacherQ.setOnEnterAction(gameContext -> {
            switch(Main.tryOut(150, "teacher")) {
                case("not_pass") -> teacherQ.setDefaultNextNodeId("not_pass");
                case("teacher_pass") -> teacherQ.setDefaultNextNodeId("teacher_pass");
                case("teacher_work_confirmed") -> teacherQ.setDefaultNextNodeId("teacher_work_confirmed");
                case("no_work") -> teacherQ.setDefaultNextNodeId("no_work");
            }
        });
        dialogueNodes.put(teacherQ.getId(), teacherQ);

        DialogueNode classMenu = new DialogueNode("classes_menu", "There are costs for classes, but they are worth it!",
                Arrays.asList("Take on-level class", "Take Honors class", "Take AP class", "Never Mind"));
        classMenu.setNextNodeForOption(0, "on_level_class_q");
        classMenu.setNextNodeForOption(1, "honors_class_q");
        classMenu.setNextNodeForOption(2, "ap_class_q");
        classMenu.setNextNodeForOption(3, "shop_farewell");
        dialogueNodes.put(classMenu.getId(), classMenu);

        DialogueNode c1q = new DialogueNode("on_level_class_q", "This adds 10 Intelligence permanently, but it will cost you $40! Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        c1q.setNextNodeForOption(0, "on_level_class");
        c1q.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(c1q.getId(), c1q);

        DialogueNode c2q = new DialogueNode("honors_class_q", "This adds 55 Intelligence permanently, but it will cost you $200! Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        c2q.setNextNodeForOption(0, "honors_class");
        c2q.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(c2q.getId(), c2q);

        DialogueNode c3q = new DialogueNode("ap_class_q", "This adds 275 Intelligence permanently, but it will cost you $1000! Do you still wish to proceed?",
                Arrays.asList("Yes!", "Never Mind"));
        c3q.setNextNodeForOption(0, "ap_class");
        c3q.setNextNodeForOption(1, "shop_farewell");
        dialogueNodes.put(c3q.getId(), c3q);

        DialogueNode c1 = new DialogueNode("on_level_class", "Class is over! Have a nice day!!");
        c1.setOnEnterAction(gameContext -> {
            System.out.println("took class!");
            Main.getInstance().startFadeOut(2.0f, () -> {
                System.out.println("Transitioned to black!");
                Main.learn(40, 20, 10);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(3.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        c1.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(c1.getId(), c1);

        DialogueNode c2 = new DialogueNode("honors_class", "Class is over! Have a nice day!!");
        c2.setOnEnterAction(gameContext -> {
            System.out.println("took class!");
            Main.getInstance().startFadeOut(2.0f, () -> {
                System.out.println("Transitioned to black!");
                Main.learn(200, 30, 55);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(3.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        c2.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(c2.getId(), c2);

        DialogueNode c3 = new DialogueNode("ap_class", "Class is over! Have a nice day!!");
        c3.setOnEnterAction(gameContext -> {
            System.out.println("took class!");
            Main.getInstance().startFadeOut(2.0f, () -> {
                System.out.println("Transitioned to black!");
                Main.learn(1000, 40, 275);
                // Then, maybe fade back in:
                Main.getInstance().startFadeIn(3.0f, () -> {
                    System.out.println("Faded back into new scene!");
                });
            });
        });
        c3.setDefaultNextNodeId("game_ready");
        dialogueNodes.put(c3.getId(), c3);
    }





    /**
     * Retrieves a dialogue node by its unique ID.
     * @param id The unique identifier of the dialogue node.
     * @return The DialogueNode object, or null if not found.
     */
    public DialogueNode getDialogueNode(String id) {
        return dialogueNodes.get(id);
    }
}