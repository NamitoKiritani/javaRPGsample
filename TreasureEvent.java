package rpgn1;//パッケージはrpgn1


public class TreasureEvent extends Event {//eventをスーパークラスとした宝箱イベント用のクラス
    private String itemName;//itemの名前を文字列としてitemnameに渡す


    public TreasureEvent(int x, int y, String itemName) {//宝箱イベントのクラス
        //
        super(x, y, 17, false);//値を入れる
        this.itemName = itemName;//このクラスのみのitemname
    }//おわり


    public String getItemName() {//手に入れたアイテムの名前を返す
        return itemName;//itemnameを返す
    }//おわり


    public String toString() {//event型と親クラスのtostringと　itemnameを返す
        return "TREASURE:" + super.toString() + ":" + itemName;
    }//おわり
}//宝箱イベントおわり
