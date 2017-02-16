package rpgn1;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

//map,messagewindowなどを纏め上げるクラス
class MainPanel extends JPanel implements KeyListener, Runnable, Common {
    // ウィンドウの大きさ
    public static final int WIDTH = 480;//幅の大きさ
    public static final int HEIGHT = 480;//高さの設定
    public static int mode =-1;//戦闘モードとの切り替えに使用する
    // マップ読み込み
    private Map[] maps;
    // マップナンバー読み込み
    private int mapNo;
    // キャラクターのステータス
    private Chara hero;
    // キーの動き
    private ActionKey leftKey;//actionkeyクラスから左キーを押すことをあらわす
    private ActionKey rightKey;//右キーを押すこと
    private ActionKey upKey;//上キーを押すこと
    private ActionKey downKey;//下キーを押すこと
    private ActionKey spaceKey;//スペースキーを押すこと


    // ゲームループに関するスレッド
    private Thread gameLoop;
    // ランダム関数
    private Random rand = new Random();

    // メッセージウィンドウ系を使用する
    private MessageWindow messageWindow;
    // メッセージウィンドウの高さ調整
    private static Rectangle WND_RECT =
        new Rectangle(62, 324, 356, 140);//メッセージウィンドウの場所指定

    public MainPanel() {//主人公の動き、maploadに関する総括
        // コンポーネントの適切な大きさを定める。
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // キーリスナー
        setFocusable(true);//ゲームwindowをfocusしているか
        addKeyListener(this);//キーリスナーを使えるようにする

        // 主人公を動かすことなどに使うキー
        leftKey = new ActionKey();//左キー
        rightKey = new ActionKey();//右キー
        upKey = new ActionKey();//上キー
        downKey = new ActionKey();//下キー
        spaceKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
        //spacekeyを押し続けても一回の反応として処理するdetect~

        // マップは二つ、これを3とかにすればもっと増やせる
        maps = new Map[2];
        // mapfile、eventfileから王様の部屋を取り込む（詳細はMapclass
        maps[0] = new Map("king_room.map", "king_room.evt", this);
        // フィールドマップを取り込む
        maps[1] = new Map("field.map", "field.evt", this);

        // 最初は王様マップから
        mapNo = 0;

        //キャラの初期位置は4,4向きは下向き
        hero = new Chara(4, 4, 0, DOWN, 0, maps[mapNo]);

        //キャラクラスをmap経由でを使えるようにする。
        maps[mapNo].addChara(hero);

        // messagewindowを使用可能に（ついでにrectの大きさも指定してる
        messageWindow = new MessageWindow(WND_RECT);

        //ゲームループに関するスレッド
        gameLoop = new Thread(this);
        //ゲームループを開始する。
        gameLoop.start();
    }//mainpanel関数おしまい

    public void paintComponent(Graphics g) {//draw系の関数はここでまとめて行う
    	//親クラスからpaintcomponentをもってくる
        super.paintComponent(g);

        // offsetで基準点からの距離をつくる　簡単に言えば画面のスクロール
        int offsetX = MainPanel.WIDTH / 2 - hero.getPx();
        // offsetに関する設定
        offsetX = Math.min(offsetX, 0);//最小は0
        //最大はウィンドウからマップの幅を引いたもの
        offsetX = Math.max(offsetX, MainPanel.WIDTH - maps[mapNo].getWidth());

        // 上と同じ要領でｙについて設定
        int offsetY = MainPanel.HEIGHT / 2 - hero.getPy();
        offsetY = Math.min(offsetY, 0);//最小は0
        //最大はウィンドウからマップの高さを引いたもの
        offsetY = Math.max(offsetY, MainPanel.HEIGHT - maps[mapNo].getHeight());
        //マップを描画する。
         maps[mapNo].draw(g, offsetX, offsetY);


         messageWindow.drawbattle(g);//battleの描画
         messageWindow.draw(g);//テキストウィンドウの描画
         messageWindow.drawteki(g);//敵の描画



    }//paintcomponentおわり

    public void run() {//プログラムの開始を実行するメソッド
        while (true) {//trueである限り走り続ける

            if (messageWindow.isVisible()) {  // windowのdrawの可視化に関する部分
                messageWindowCheckInput();//hide（戦闘やメッセージからフィールドにもどる部分）に関する部分
            } else {  // それ以外ならmain~(主人公の動きに関するメソッド）を動かす
                mainWindowCheckInput();//主人公に関するメソッド
            }//if文終わり
            if (!messageWindow.isVisible()) {//メッセージウィンドウが可視化されていないなら
                // 主人公が動けるようにする
                heroMove();
                // もぶキャラたちが動けるようにする
                charaMove();
            }//if文おわり
            repaint();//repaint（再描画）


            try {//ゲームループに関する部分
                Thread.sleep(20);//インターバル(20)
            } catch (InterruptedException e) {//割り込みが発生したときの修正
                e.printStackTrace();//エラーの出力を担当
            }//try部分終了
        }//whileループに関する記述おわり
    }//runに関する記述おしまい。

    private void mainWindowCheckInput() {//主人公の動きに関する関数（バトル画面もここに置いた）

     switch(mode){//modeで戦闘画面との切り替えを行う
        case -1://case-1;
        if (leftKey.isPressed()) { //左のキーが押されたら
            if (!hero.isMoving()) {       // もしheromovingが許容されていれば
                hero.setDirection(LEFT);  // 左向きを向く
                hero.setMoving(true);     // 主人公の移動を左にtrueにして移動完了
            }//if文（左キー）終了
        }//if文（左キー)終了
        if (rightKey.isPressed()) { // 上に同じ（右バージョン）
            if (!hero.isMoving()) {//上に同じ
                hero.setDirection(RIGHT);//うえに同じ（右バージョン）
                hero.setMoving(true);//上に同じ
            }//if文終了
        }//if文（右）終了
        if (upKey.isPressed()) { // 上に同じ（上バージョン）
            if (!hero.isMoving()) {//上に同じ
                hero.setDirection(UP);//上に同じ(上バージョン）
                hero.setMoving(true);//上に同じ
            }//if文終了
        }//if文終了
        if (downKey.isPressed()) { // 下キーが押されたら
            int randmob =rand.nextInt(50);//rand関数で50までの乱数を生成する
            if(randmob==1){//もし値が１であれば
            	if(mapNo==1){//そしてフィールドマップであれば
            	  mode=-2;//戦闘モードに行こう
            	}//if文終了

            }//if文終了
            if (!hero.isMoving()) {//ここは上の主人公の移動と同じ（下バージョン）
                hero.setDirection(DOWN);//下向きに方向をセット
                hero.setMoving(true);//主人公の下移動を完了させて終了
            }//if文終了
        }//if文終了
        if (spaceKey.isPressed()) {  // スペースキーが押されたら
            //
            if (hero.isMoving()) return;//ヒーローが動いてるなら何もしない


            TreasureEvent treasure = hero.search();//お宝イベントがあれば、主人公の目の前をサーチ
            if (treasure != null) {//お宝があるならば
                //～を手に入れたというログを流す
                messageWindow.setMessage(treasure.getItemName() + "　を　てにいれた");
                // メッセージウィンドウを可視化する
                messageWindow.show();
                //マップよりそのイベント対象を除去する。
                maps[mapNo].removeEvent(treasure);
                return;  // なにも返さないで終わり
            }//if文（宝関連）終了

            // doorイベントに関する関数
            DoorEvent door = hero.open();
            if (door != null) {//もしdoorイベントをすれば
                // doorをあける（撤去する）
                maps[mapNo].removeEvent(door);

                return;//なにもかえさない
            }//ドアイベントのifを終了


            if (!messageWindow.isVisible()) {  // めっせーじウィンドウが見えない状態で
                Chara chara = hero.talkWith();//スぺ―スキーによる主人公の会話設定
                if (chara != null) {//charaがいなければ
                    // キャラごとのセリフをメッセージウィンドウに裏に出力
                    messageWindow.setMessage(chara.getMessage());
                    // メッセージウィンドウを表示する
                    messageWindow.show();
                } else {//なにもいなければ
                	//定型文を返す
                    messageWindow.setMessage("そのほうこうにはだれもいないようだ");
                    messageWindow.show();//メッセージウィンドウを表示
                }//メッセージウィンドウ系おわり
             }//メッせージウィンドウ系の設定（主に表示する系統出）を終了
        }//スペースキーに関する操作を終了する
            break;//ブレイク
        case -2://case2（戦闘画面）

           	if (spaceKey.isPressed()) {//スペースキーを押すことで戦闘画面に移行
               if (!messageWindow.isVisible()) {  // メッセージウィンドウが表れていなければ
                  messageWindow.showbat();//バトルウィンドウへ移行
                  messageWindow.setMessage("やばいやつ　が　あらわれた");//モンスター出現ログ
                  messageWindow.show();//メッセージウィンドウを表示
                  messageWindow.showteki();//敵を表示
                  //なぜかスペースキーを押した中に入れないと戦闘画面への移行ができなかった
                  //これについて2週間ほど悩んだが結論は出なかった。

               }//if文おわり
               mode=-1;//モード１（非戦闘モードに移行
            }//if文（スペース系終了）

       }//switch関数終わり

    }//移動系、バトル系に関するものはここでおわり


    private void messageWindowCheckInput() {//描画したものを隠す関数
        if (spaceKey.isPressed()) {//もしスペースキーが押されたら
            if (messageWindow.nextMessage()) {  // nextmessageがtrue（最後のページ）をかえしたら
                messageWindow.hide();  // メッセージウィンドウを隠す（描画をbreak
                messageWindow.hidebat();//battle画面の破棄
                messageWindow.hideteki();//敵の破棄
                messageWindow.hidewin();//戦闘ウィンドウの破棄
            }//if文終わり
        }//スペースキーに関するifを終了
    }


    private void heroMove() {//主人公の動きについてのもの
        // もし主人公が動いているのであれば（true or false
        if (hero.isMoving()) {
            if (hero.move()) {  // 主人公を動かせるのであれば（true or false
            	//イベントについての設定（主人公の場所を取得
                Event event = maps[mapNo].eventCheck(hero.getX(), hero.getY());
                if (event instanceof MoveEvent) {  //moveeventがeventをインターフェースとしているならture
                    MoveEvent m = (MoveEvent)event;//moveeventをMとする
                    //heroをマップから除去(マップ移動時に必要
                    maps[mapNo].removeChara(hero);
                    //mapNo指定
                    mapNo = m.destMapNo;
                    //主人公データを入手
                    hero = new Chara(m.destX, m.destY, 0, DOWN, 0, maps[mapNo]);
                    //mapに主人公をマップに出す
                    maps[mapNo].addChara(hero);
                }//if文終了
            }//if文終了
        }//if文終了
    }//主人公についての動きのclassを終了


    private void charaMove() {//chara（モブたち）の移動について
        //モブを可変配列のする（量産可能に
        Vector charas = maps[mapNo].getCharas();
        for (int i=0; i<charas.size(); i++) {//chara（モブ）の量を取得
            Chara chara = (Chara)charas.get(i);//charaの情報を取得
            if (chara.getMoveType() == 1) {  //charaが動くタイプであれば
                if (chara.isMoving()) {  //もしisMovingがture
                    chara.move();  //キャラが動けるようにする
                } else if (rand.nextDouble() < Chara.PROB_MOVE) {//それ以外なら乱数発生
                    chara.setDirection(rand.nextInt(4));//確率で向きを変えれるようにする
                    chara.setMoving(true);//つぎに移動をtrueにして動けるようにする
                }//elesif文おわり
            }//if文おわり
        }//if文おわり
    }//charamoveおわり


    public void keyPressed(KeyEvent e) {//keyが押されたときのメソッド
        int keyCode = e.getKeyCode();//keyeventを参照

        if (keyCode == KeyEvent.VK_LEFT) {//左キーに関して(vk_leftは左キー用の定数
            leftKey.press();//l左キーをおした判定
        }//if文おわり
        if (keyCode == KeyEvent.VK_RIGHT) {//上記と同じく右
            rightKey.press();//右
        }//おわり
        if (keyCode == KeyEvent.VK_UP) {//上
            upKey.press();//上
        }//おわり
        if (keyCode == KeyEvent.VK_DOWN) {//下
            downKey.press();//下
        }//おわり
        if (keyCode == KeyEvent.VK_SPACE) {//スペース
            spaceKey.press();//スペース
        }//おわり
    }//keyが押された時にかんするメソッドおわり


    public void keyReleased(KeyEvent e) {//keyが離されたときのメソッド
        int keyCode = e.getKeyCode();//keyevent参照

        if (keyCode == KeyEvent.VK_LEFT) {//左キーに関して
            leftKey.release();//左キーを離した判定
        }//if文おわり
        if (keyCode == KeyEvent.VK_RIGHT) {//右キーに関して
            rightKey.release();//右キーを離した判定
        }//おわり
        if (keyCode == KeyEvent.VK_UP) {//上
            upKey.release();//上
        }//おわり
        if (keyCode == KeyEvent.VK_DOWN) {//下
            downKey.release();//下
        }//おわり
        if (keyCode == KeyEvent.VK_SPACE) {//スペースキー
            spaceKey.release();//スペースキー
        }//おわり
    }//キーが離された時のメソッドおわり

    public void keyTyped(KeyEvent e) {//実装しないとエラーが起きるので一応実装したメソッド
    }//特にコメントはなし
}//mainpanelクラスに関するメソッドはここで終了
