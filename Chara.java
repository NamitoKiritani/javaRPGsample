package rpgn1;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;


public class Chara implements Common {//chara（モブや主人公）に関するクラス
    //charaの動く速度の数値設定
    private static final int SPEED = 4;

    //ランダム関数（壁衝突時の向き変更）のための数値設定
    public static final double PROB_MOVE = 0.02;
    //キャラの画像のための設定
    private static Image charaImage;

    // charaの種類のための設定
    private int charaNo;

    private int x, y;   // キャラの位置（座標）
    private int px, py; // キャラの位置（パネル単位）

    //right left up down（向き）
    private int direction;
    //足踏みにつかう数値
    private int count;
    // 動いてるかの判別(true or false)
    private boolean isMoving;
    //動いた長さ（内容は後述）
    private int movingLength;
    //movetype（キャラが勝手に動けるか動けないかの判定）
    private int moveType;
    //キャラの文字列取得のためのstring
    private String message;
    //動くアニメーションについてのスレッド
    private Thread threadAnime;
    //mapclassの使用
    private Map map;
    //charaについての設定用メソッド
    public Chara(int x, int y, int charaNo, int direction, int moveType, Map map) {
        this.x = x;//キャラのx座標
        this.y = y;//キャラのy座標

        px = x * CS;//キャラのx座標（チップ単位版）
        py = y * CS;//キャラのy座標（チップ単位版）

        this.charaNo = charaNo;//キャラの種類設定
        this.direction = direction;//キャラの方向設定
        count = 0;//スムーズに動くためのcount
        this.moveType = moveType;//動くタイプが動かないタイプの設定
        this.map = map;//mapについての設定（そのキャラがいるマップ）

        // キャラの画像がなければ
        if (charaImage == null) {
            loadImage();//imageをロード
        }//if文おわり

        //移動アニメーションのスレッド
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();//開始
    }//キャラ設定についてのメソッドおわり

    public void draw(Graphics g, int offsetX, int offsetY) {//charaの描画についてのメソッド
        int cx = (charaNo % 8) * (CS * 2);//キャラNoによるキャラの描画
        int cy = (charaNo / 8) * (CS * 4);//キャラNoによるキャラの描画
        //cx,cyは向きをキャラ画像の中から取り出すための数値である
        //それを使用して、キャラの場所向きその他を描画する
        g.drawImage(charaImage, px + offsetX, py + offsetY, px + offsetX + CS, py + offsetY + CS,
            cx + count * CS, cy + direction * CS, cx + CS + count * CS, cy + direction * CS + CS, null);
    }//charaの描おわり



    public boolean move() {//move(ture or false
        switch (direction) {//向きにについて
            case LEFT://directionがLEFTであれば(左）が選択されれば
                if (moveLeft()) {//moveleft(内容については後述）がtrue
                    return true;//trueを返す
                }//左向きについてはおわり
                break;//break
            case RIGHT://上記したものの右版
                if (moveRight()) {//右
                    return true;//trueを返す
                }//おわり
                break;//break
            case UP://上バージョン
                if (moveUp()) {//上
                    return true;//tureを返す
                }//おわり
                break;//break
            case DOWN://下バージョン
                if (moveDown()) {//下
                    return true;//tureを返す
                }//終わり
                break;//break
        }//switch関数おわり
        return false;//それ以外ならfalseを返す
    }//moveメソッドおわり



    //moveleft moveright moveup movedown
    //内容はほぼ同じである
    //違う点についてmoveright moveup movedownはコメントをする
    private boolean moveLeft() {//moveleftメソッド（返り値はtrue or false
        //
        int nextX = x - 1;//左に１動く(チップ単位）
        int nextY = y;//上下は関係なし
        if (nextX < 0) {//その次のx座標が0よりちいさければ
            nextX = 0;//xは０
        }//if文おわり

        if (!map.isHit(nextX, nextY)) {//ishitがnextx、nextyでfalseなら
            // 	キャラの移動速度調整
            px -= Chara.SPEED;//キャラのスピードを
            if (px < 0) {//pxが0以下なら
                px = 0;//px=0
            }//if文おわり

            movingLength += Chara.SPEED;//動く長さはキャラのスピードを決める

            if (movingLength >= CS) {//動く長さがチップサイズより長くなったら
                x--;//xを減らして
                px = x * CS;//pxをチップ上に固定
                isMoving = false;//移動をfalseにすつ
                return true;//tureで返す
            }//if文おわり
        } else {//それ以外なら
            isMoving = false;//移動をfalseにする
            px = x * CS;//px座標の取得（チップ単位ではないほうの座標
            py = y * CS;//py座標の取得
        }//elseおわり

        return false;//falseを返す
    }//左についてのメソッド終了


    private boolean moveRight() {//右移動についてのメソッド

        int nextX = x + 1;//xのチップ単位で+1（右に移動）
        int nextY = y;//上下の移動なし
        if (nextX > map.getCol() - 1) {//moveleftの右バージョン
            nextX = map.getCol() - 1;//上に同じ
        }//if文おわり

        if (!map.isHit(nextX, nextY)) {//同じ

            px += Chara.SPEED;//x（座標）の+側にスピード分足す
            if (px > map.getWidth() - CS) {//mapの横幅-チップサイズよりおおきければ
                px = map.getWidth() - CS;//mapの横幅-チップサイズにpxを固定
            }//ifおわり

            movingLength += Chara.SPEED;//上と同じ

            if (movingLength >= CS) {//上と同じ

                x++;//上とおなじ
                px = x * CS;//上と同じ

                isMoving = false;//ismovingをfalseに変更
                return true;//trueを返す
            }//おわり
        } else {//それ以外なら
            isMoving = false;//ismovingをfalseで返す
            px = x * CS;//上に同じ
            py = y * CS;//上に同じ
        }//上に同じ

        return false;//上に同じ
    }//みぎについてのメソッドおわり


    private boolean moveUp() {//上移動について

        int nextX = x;//x座標は変更なし
        int nextY = y - 1;//y座標（パネル単位）で-1移動。
        if (nextY < 0) {//Yが0より小さくなったら
            nextY = 0;//0固定
        }//おわり

        if (!map.isHit(nextX, nextY)) {//上に同じ
            py -= Chara.SPEED;//speed分pyを減らす
            if (py < 0) py = 0;//上に同じ
            movingLength += Chara.SPEED;//上に同じ
            if (movingLength >= CS) {//上に同じ
                y--;//yを減らす（チップ数分）
                py = y * CS;//上に同じ
                isMoving = false;//上に同じ
                return true;//上に同じ
            }//上に同じ
        } else {//上に同じ
            isMoving = false;//上に同じ
            px = x * CS;//上に同じ
            py = y * CS;//上に同じ
        }//上に同じ

        return false;//上に同じ
    }//上移動おわり


    private boolean moveDown() {//下移動について

        int nextX = x;///x座標は変わらず
        int nextY = y + 1;//y座標（チップパネル文）１移動する
        if (nextY > map.getRow() - 1) {//移動先のyが行以上なら
            nextY = map.getRow() - 1;//行に戻す
        }//おわり
        if (!map.isHit(nextX, nextY)) {//上に同じ
            py += Chara.SPEED;//pyにスピード文を足す
            if (py > map.getHeight() - CS) {//マップの高さーチップサイズがpyよりしたなら
                py = map.getHeight() - CS;//pyの高さをマップの高さーチップサイズに固定
            }//おわり
            movingLength += Chara.SPEED;//上に同じ
            if (movingLength >= CS) {//上に同じ
                y++;//yを足す
                py = y * CS;//y（座標）に戻す
                isMoving = false;//上に同じ
                return true;//上に同じ
            }//上に同じ
        } else {//上に同じ
            isMoving = false;//上に同じ
            px = x * CS;//上に同じ
            py = y * CS;//上に同じ
        }//上に同じ

        return false;//上に同じ
    }//下移動おわり


    public Chara talkWith() {//モブとの会話設定
        int nextX = 0;//次のxを0に
        int nextY = 0;//次のyを0に
        switch (direction) {//向きについて
            case LEFT://左なら
                nextX = x - 1;//次のxを-1する
                nextY = y;//変化なし
                break;//breal;
            case RIGHT://右なら
                nextX = x + 1;//次のxを+1する
                nextY = y;//変化なし
                break;//break
            case UP://上向きなら
                nextX = x;//x座標変化なし
                nextY = y - 1;//つぎのyを+1する
                break;//break
            case DOWN://下なら
                nextX = x;//xは変化なし
                nextY = y + 1;//次のyは+1する
                break;//break
        }//おわり

        Chara chara;//chaarをつくり
        chara = map.charaCheck(nextX, nextY);//mapのcharaチェックとする
        if (chara != null) {//キャラがいるのなら
            switch (direction) {//主人公の向きによるswicth関数
                case LEFT://左なら
                    chara.setDirection(RIGHT);//モブの向きを右に向きを固定
                    break;//break;
                case RIGHT://右なら
                    chara.setDirection(LEFT);//モブの向きを左
                    break;//break;
                case UP://上なら
                    chara.setDirection(DOWN);//上
                    break;//break;
                case DOWN://下なら
                    chara.setDirection(UP);//下
                    break;//break;
            }//swicth関数おわり
        }//もぶの向きおわり

        return chara;//charaを返して
    }//モブとの会話設定おわり

    public TreasureEvent search() {//宝箱イベントについて
        Event event = map.eventCheck(x, y);//マップクラスののチェック機能を使用
        if (event instanceof TreasureEvent) {//eventのなかなtresureeventがインターフェースとしてつかっているなら
            return (TreasureEvent)event;//宝箱イベントを返す
        }//ifおわり

        return null;//そうじゃなければなにも返さない
    }//おわり


    public DoorEvent open() {//ドアを開くイベントについてのメソッド
        int nextX = 0;//初期化
        int nextY = 0;//初期化
        switch (direction) {//主人公の方向が
            case LEFT://左なら
                nextX = x - 1;//xを-1にする
                nextY = y;//yは変わらず
                break;//break
            case RIGHT://右なら
                nextX = x + 1;//xを+1する
                nextY = y;//ｙは変化なし
                break;//break
            case UP://上なら
                nextX = x;//xは変化なし
                nextY = y - 1;//yは-1する
                break;//break
            case DOWN://下なら
                nextX = x;//xは変化なし
                nextY = y + 1;//yは+1する
                break;//break
        }//swicthおわり
        Event event = map.eventCheck(nextX, nextY);//mapクラスのイベントチェック
        if (event instanceof DoorEvent) {//dooeeventが選択されているなら
            return (DoorEvent)event;//ドアイベントを返す
        }//if文終わり

        return null;//それ以外はなにも返さない
    }//ドアイベントのメソッド終了

    private void loadImage() {//キャラのイメージをloadする関数

        ImageIcon icon = new ImageIcon(getClass().getResource("chara.gif"));//キャラのイメージをロード
        charaImage = icon.getImage();//charaimageにイメージを渡す
    }//おわり


    public int getX() {//チップパネルの列の番号を渡す
        return x;//xを返す
    }//おわり

    public int getY() {//チップパネルの行の番号を渡す
        return y;//yを返す
    }//おわり

    public int getPx() {//x座標を渡す
        return px;//pxを返す
    }//おわり

    public int getPy() {//y座標を渡す
        return py;//pyを返す
    }//おわり

    public void setDirection(int dir) {//向きを渡す
        direction = dir;//向きをdirectionに渡す
    }//おわり

    public boolean isMoving() {//動いているか？(true or false 
        return isMoving;//移動しているか？
    }//おわり

    public void setMoving(boolean flag) {//移動のフラグをset
        isMoving = flag;//移動中のフラグをismovingに渡す

        movingLength = 0;//移動の長さを0に
    }//setmovingおわり


    public String getMessage() {///getmessage（セリフなどを入手）
        return message;//messageを返す
    }//おわり


    public void setMessage(String message) {//セットメッセージ（ファイルから読み込む
        this.message = message;//得た文字列をmessageに渡す（このクラスだけのmessageに
    }//おわり

    public int getMoveType() {//移動するかしないかの情報を手に入れる
        return moveType;//movetypeで返す（0 or 1)
    }//おわり


    private class AnimationThread extends Thread {//キャラの足踏み移動のためのスレッド
        public void run() {//run関数（スレッド)
            while (true) {//trueであるかぎり

            	//ようはこの関数は主人公が足踏みするための関数
                if (count == 0) {//カウントが0であれば
                    count = 1;//1に変更
                } else if (count == 1) {//1であれば
                    count = 0;//0に変更
                }//おわり


                try {//例外処理のためのtry
                    Thread.sleep(300);//300ミリ秒ごとに切り替える
                } catch (InterruptedException e) {//例外処理が発生すれば
                    e.printStackTrace();//エラーを吐く
                }//例外処理系統はおわり
            }//whileループおわり
        }//run関数おわり
    }//threadについておわり
}//charaクラスおわり
