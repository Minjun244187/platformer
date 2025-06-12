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
        eventList.add("decreaseIR");
        eventList.add("healthyBurger");
        eventList.add("boycott");
        eventList.add("forestBurn");
        eventList.add("iceMelt");
        eventList.add("goldRush");
        eventList.add("construction");

        //bonuses
        eventList.add("bankrupt"); //6
        eventList.add("warDeclare"); //7
        eventList.add("invasion"); //8



        //add all war events
        warEventList.clear();
        warEventList.add("bombing");
        warEventList.add("weaponFirm");
        warEventList.add("forward");
        warEventList.add("backward");
        warEventList.add("increaseTax");
        warEventList.add("increaseTax");



        //Only activate when battles take place
        warEventList.add("surrender");
        warEventList.add("victory");


    }

    public String getRandomEvent() {
        int i = (int) (Math.random() * (eventList.size() - 3));
        return eventList.get(i);
    }

    public String getRandomWarEvent() {
        int i = (int) (Math.random() * (warEventList.size() - 2));
        return warEventList.get(i);
    }

    public static void triggerEvent(int eventIndex) {

    }

    

}
