package rpgn1;


public abstract class Event {
    protected int x;
    protected int y;
    protected int chipNo;
    protected boolean isHit;
    
    /**

     */
    public Event(int x, int y, int chipNo, boolean isHit) {
        this.x = x;
        this.y = y;
        this.chipNo = chipNo;
        this.isHit = isHit;
    }
    

    public String toString() {
        return x + ":" + y + ":" + chipNo + ":" + isHit;
    }
}
