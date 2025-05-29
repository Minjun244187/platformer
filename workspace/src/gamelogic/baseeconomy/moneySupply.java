package gamelogic.baseeconomy;

public class moneySupply {
    public long mSupply;




    public void setMSupply(long amount) {
        mSupply = amount;
    }

    public void addMSupply(long amount) {
        mSupply += amount;
    }

    public void removeMSupply(long amount) {
        mSupply -= amount;
    }

}
