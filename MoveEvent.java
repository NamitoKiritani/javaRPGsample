package rpgn1;//パッケージはrpgn1


public class MoveEvent extends Event {//親クラスをeventとしたmoveevent
    // 初期配置のマップNo
    public int destMapNo;
    // マップ移動時の初期位置(x)
    public int destX;
    // マップ移動時の初期位置(y
    public int destY;
    //map移動イベントにかんするメソッド
    public MoveEvent(int x, int y, int chipNo, int destMapNo, int destX, int destY) {

        super(x, y, chipNo, false);//スーパークラスの基本情報を入力
        this.destMapNo = destMapNo;//このクラスだけのマップナンバー入力
        this.destX = destX;//初期位置入力
        this.destY = destY;//初期位置入力
    }//map移動に関するイベントおわり

    public String toString() {//スーパークラスで必要な値とその他のmoveイベントで必要な値を返すメソッド
    	//このクラスで得た数値をまとめて返す
        return "MOVE:" + super.toString() + ":" + destMapNo + ":" + destX + ":" + destY;
    }//tostringおわり
}//moveイベントクラスおわり
