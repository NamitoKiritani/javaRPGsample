package rpgn1;//パッケージはrpgn1



public abstract class Event {//ほかのイベントクラスのスーパークラスとなるeventクラス
    protected int x;//protectedでサブクラスの参照を通してこの値にアクセスできるようになる
    protected int y;//yの値
    protected int chipNo;//チップの番号（いわゆる山とか海とかの種類
    protected boolean isHit;//あたり判定のあるなしについて


    public Event(int x, int y, int chipNo, boolean isHit) {//ほかのeventクラスで使うもののひな型
        this.x = x;//x座標
        this.y = y;//y座標y
        this.chipNo = chipNo;//チップの番号
        this.isHit = isHit;//あたり判定
    }


    public String toString() {//得られた文字列を返すメソッド
        return x + ":" + y + ":" + chipNo + ":" + isHit;//得られた情報を返す
    }//stringを返す
}//eventクラスおわり
