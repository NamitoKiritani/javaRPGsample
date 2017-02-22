package rpgn1;//パッケージはrpgn1

public class DoorEvent extends Event {//ドアの撤去イベントに使用する（スーパークラスはevent))


    public DoorEvent(int x, int y) {//ドアの座標取得
        super(x, y, 18, true);//スーパークラスから実装
    }//おわり


    public String toString() {//tostring（ここで得た値をまとめて返す
        return "DOOR:" + super.toString();//イベントタイプはドア、継承したtostringを返す
    }//tostringおわり
}//ドアイベントおわり
