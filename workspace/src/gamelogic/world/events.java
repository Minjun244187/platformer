package gamelogic.world;

import java.util.ArrayList;

public class events {
    ArrayList<String> eventList = new ArrayList<>();
    ArrayList<String> warEventList = new ArrayList<>();
    

    public void resetEvents() {
        //add all events
        eventList.clear();
        eventList.add("increaseSpending"); //0
        eventList.add("decreaseSpending"); //1
        eventList.add("increaseTax"); //2
        eventList.add("decreaseTax"); //3
        eventList.add("increaseIR"); //4
        eventList.add("decreaseIR"); //5
        //bonuses
        eventList.add("bankrupt"); //6
        eventList.add("warDeclare"); //7
        eventList.add("invasion"); //8



        //add all war events
        warEventList.clear();
        warEventList.add("bombing");
        warEventList.add("battle");
        warEventList.add("weaponFirm");
        warEventList.add("forward");
        warEventList.add("surrender");
        warEventList.add("victory");

    }

    public String getRandomEvent() {
        int i = (int) (Math.random() * eventList.size());
        return eventList.get(i);
    }

    public String getRandomWarEvent() {
        int i = (int) (Math.random() * warEventList.size());
        return warEventList.get(i);
    }

    public static void triggerEvent(int eventIndex) {

    }

    

}
