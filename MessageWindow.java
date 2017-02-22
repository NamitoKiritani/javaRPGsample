package rpgn1;//パッケージはrpgn1

import java.awt.Color;//awt.Colorをインポート
import java.awt.Graphics;//awt.Graphicsをインポート
import java.awt.Image;//awt.Imageをインポート
import java.awt.Rectangle;//awt.Rectangleをインポート
import java.util.Timer;//util.Timerをインポート
import java.util.TimerTask;//util.TimerTaskをインポート

import javax.swing.ImageIcon;//javax.swing.ImageIconをインポート

public class MessageWindow {//メッセージウィンドウに関するクラス（敵クラスもここにまとめてしまいました）

    private static final int EDGE_WIDTH = 2;//メッセージログの白い外枠の幅
    protected static final int LINE_HEIGHT = 8;//メッセージログにおける高さ
    private static final int MAX_CHAR_IN_LINE = 20;//各行における最大文字数
    private static final int MAX_LINES = 3;//メッセージログの最大行数
    private static final int MAX_CHAR_IN_PAGE = MAX_CHAR_IN_LINE * MAX_LINES;//各ログの最大文字数
    private Rectangle rect;//ログウィンドウの内側の大きさ
    private Rectangle innerRect;//ログウィンドウの外側の大きさ
    private Rectangle textRect;//テキストの幅
    private boolean isVisible = false;//テキストの表示をここでする
    private boolean isVisibleBattle = false;//バトル画面の表示をここでする
    private Image cursorImage;//テキストイメージのダウンロードに使用
    private char[] text = new char[128 * MAX_CHAR_IN_LINE];//textの設定
    private int maxPage;//最大ページ量
    private int curPage = 0;//ページめくり用
    private int curPos;//ページめくり用
    private boolean nextFlag = false;//つぎのページにいくフラグ
    private static Image tekiImage;//敵イメージのダウンロードに使用
    private boolean isVisibleteki=false;//次のページにいくフラグ
    private boolean isVisiblewin=false;//バトルウィンドウを隠すフラグ
    private MessageEngine messageEngine;//メッセージエンジンクラスを使用
    private Timer timer;    //メッセージの流れる時間
    private TimerTask task;//タイマータスクによる時間管理に使用

    public MessageWindow(Rectangle rect) {//メッセージウィンドウの大きさなど指定
        this.rect = rect;
         //ここでメッセージウィンドウの大きさを指定
        innerRect = new Rectangle(//メッセージウィンドウの内枠の幅指定
                rect.x + EDGE_WIDTH,//左上の頂点のx座標
                rect.y + EDGE_WIDTH,//左上の頂点のy座標
                rect.width - EDGE_WIDTH * 2,//外枠の横幅
                rect.height - EDGE_WIDTH * 2);//外枠の高さ

        textRect = new Rectangle(//テキストの大きさ、最初の位置指定
                innerRect.x + 16,//外幅の頂点から16右にずらす
                innerRect.y + 16,//外幅の頂点から16下にずらす
                320,//一文字320の幅
                120);//一文字120の高さ

        messageEngine = new MessageEngine();//メッセージエンジンクラスを使用（文字を引っ張ってくる）
        if (tekiImage == null) {//敵イメージがないなら
            tekiloadImage();//敵イメージのロードを行う
        }//if文おわり
        ImageIcon icon = new ImageIcon(getClass().getResource("cursor.gif"));//流れるメッセージを作るために使用する
        cursorImage = icon.getImage();//イメージを作成

        timer = new Timer();//timerタスクの使用
    }//各種関数指定とロードおわり

    public void draw(Graphics g) {//graphics g	を使った描画
        if (isVisible == false) return;//isvisibleがfalseなら描画しない（移動中などに描画しないため）

        g.setColor(Color.WHITE);//白色を用意します
        g.fillRect(rect.x, rect.y, rect.width, rect.height);//まず指定範囲を白く塗りつぶします

        g.setColor(Color.BLACK);//次に黒色を用意します
        g.fillRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);//白色の上から内側を黒く塗りつぶします
        //これでメッセージウィンドウの大枠が完成。fillrectが塗りつぶす関数であることを使用

        for (int i=0; i<curPos; i++) {//今の文字の位置について
            char c = text[curPage * MAX_CHAR_IN_PAGE + i];//次の文字を描画する位置を指定
            int dx = textRect.x + MessageEngine.FONT_WIDTH * (i % MAX_CHAR_IN_LINE);//次の文字の描画する幅を指定
            //ここでラインの高さについても調整している
            int dy = textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * (i / MAX_CHAR_IN_LINE);
            //次の文字の高さについての調整。
            messageEngine.drawCharacter(dx, dy, c, g);//文字の描画をする。
        }//次の文字についての位置調整おわり
        if (curPage < maxPage && nextFlag) {//いまのページが最後のページででないなら
            int dx = textRect.x + (MAX_CHAR_IN_LINE / 2) * MessageEngine.FONT_WIDTH - 8;//初期化

            int dy = textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * 3;//初期化
            g.drawImage(cursorImage, dx, dy, null);//文字を見えなくする
        }//if文おわり
    }//draw関数おわり
    public void drawbattle(Graphics g){//バトル画面についての描画
    	if(isVisibleBattle == false) return;//isvisiblebattleがFalseならこの画面を描画しない
        g.setColor(Color.BLACK);//画面をとりあえず黒く塗りつぶす
        g.fillRect(0,0,480,480);//塗りつぶす範囲指定
    }//drawbattleおわり
    public void drawteki(Graphics g){//敵の描画開始
    	if(isVisibleteki == false) return;//tisvisibletekiがfalseなら描画しない
    	g.drawImage(tekiImage, 130, 100, 300, 300, 0, 0, 300, 300, null);//敵のイメージを指定の場所に移す
    }//描画にかんする関数おわり
    public void drawbattlewin(Graphics g){//バトルウィンドウに関する設定（最終的にここは実装できかった。
        if (isVisiblewin == false) return;//isvisibleblewinがfalseなら描画しない

        g.setColor(Color.WHITE);//grahics gで白色を用意
        g.fillRect(350,350,100,100);//指定範囲をぬりつぶす。

        g.setColor(Color.BLACK);//つぎに黒色を用意
        g.fillRect(355,355,90,90);//指定範囲を塗りつぶす

        for (int i=0; i<curPos; i++) {//今の文字の位置について
            char c = text[curPage * MAX_CHAR_IN_PAGE + i];//次の文字を描画する位置を指定
            int dx = textRect.x + MessageEngine.FONT_WIDTH * (i % MAX_CHAR_IN_LINE);//次の文字の描画する幅を指定
            //ここでラインの高さについても調整している
            int dy = textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * (i / MAX_CHAR_IN_LINE);
            //次の文字の高さについての調整。
            messageEngine.drawCharacter(dx, dy, c, g);//文字の描画をする。
        }//次の文字についての位置調整おわり
        if (curPage < maxPage && nextFlag) {//いまのページが最後のページででないなら
            int dx = textRect.x + (MAX_CHAR_IN_LINE / 2) * MessageEngine.FONT_WIDTH - 8;//初期化

            int dy = textRect.y + (LINE_HEIGHT + MessageEngine.FONT_HEIGHT) * 3;//初期化
            g.drawImage(cursorImage, dx, dy, null);//文字を見えなくする
        }//ページ調整おわり
    }//バトルウィンドウに関する関数おわり

    public void setMessage(String msg) {//セットメッセージ（ここに直接文章を書きこむ
        curPos = 0;//最初の位置の設定
        curPage = 0;//最初のページの設定
        nextFlag = false;//次のフラグがなし
        for (int i=0; i<text.length; i++) {//空白がなくなるまで飛ばすループ

        	//　ここになにかあるかも

        	text[i] = '　';//空白があれば飛ばす
        }//ループ終了

        int p = 0;// p==0（ページ数）
        for (int i=0; i<msg.length(); i++) {//iがメッセージの長さ以下なら
            char c = msg.charAt(i);//つぎの文字の数字
            if (c == '\\') {//\\が文字としてくるとき
                i++;//次の文字が
                if (msg.charAt(i) == 'n') { //nであるならば
                    p += MAX_CHAR_IN_LINE;//pを最大文字数にの数値にする
                    p = (p / MAX_CHAR_IN_LINE) * MAX_CHAR_IN_LINE;//pの値を最大文字数にする
                } else if (msg.charAt(i) == 'f') { //fがきた場合
                    p += MAX_CHAR_IN_PAGE;//つぎのぺージに行く文字数に変更
                    p = (p / MAX_CHAR_IN_PAGE) * MAX_CHAR_IN_PAGE;//次のページに行く文字数にする
                }//elseifおわり
            } else {//それ以外なら
                text[p++] = c;//ｐに一をたして次の文字へ
            }//\\がでてきたときのif文終わり
        }//forループおわり

        maxPage = p / MAX_CHAR_IN_PAGE;//最大ページ数はpによる

        task = new DrawingMessageTask();//taskをdrawmsgtaskに
        timer.schedule(task, 0L, 20L);//timer関数の設定
    }//セットメッセージおわり


    public boolean nextMessage() {
    	//いまのページが最後なら停止するやつ
        if (curPage == maxPage) {//いまのぺージが最後のページ
            task.cancel();//taskを終了
            task = null;  //貼り付けた文字を消す。
            return true;//nextmessageをtureで返す
        }//if文おわり
        if (nextFlag) {//次のフラグがtureなら
            curPage++;//いまのページに+1をする
            curPos = 0;//文字の位置を初期位置に戻す
            nextFlag = false;//ネクストフラグをfalseにもどす
        }//if文終わり
        return false;//nextmessageをfalseで返す
    }//timertaskを動かし続けると危ないので停止する。

    public void show() {//メッセージウィンドウを見れるようにする関数
        isVisible = true;//trueにして可視化
    }//おわり


    public void hide() {//メッセーウィンドウを隠す関数
        isVisible = false;//falseにしてdrawをキャンセル
    }//おわり

    public boolean isVisible() {//booleanでtureとfalseに関連する値に設定
        return isVisible;//isvisivleを返す
    }//おわり
    public void showwin() {//showwinでおなじことを繰り返す
        isVisiblewin = true;//isVisiblewinをtureにして可視化
    }//おわり


    public void hidewin() {//隠す関数
        isVisiblewin = false;//falseでdrawするのをキャンセル
    }//おわり

    public boolean isVisiblewin() {//trueとfalseに関連する関数とする
        return isVisiblewin;//isvisiblewinを返す
    }//おわり
    public void showbat() {//おなじことを繰り返す
        isVisibleBattle = true;//trueにする
    }//おわり


    public void hidebat() {//おなじことをdrawbattleでもする
        isVisibleBattle = false;//falseを返す
    }//おわり

    public boolean isVisibleBattle() {//ture,falseの関数にする
        return isVisibleBattle;//isvisiblebattleを返す
    }//おわり

    private void tekiloadImage() {//敵のイメージをダウンロードする。

        ImageIcon tekiicon = new ImageIcon(getClass().getResource("teki01.gif"));
        //teki01のgifを取り込む
        tekiImage = tekiicon.getImage();//このイメージをtekiIMageとして結びつける
    }//おわり
    public void showteki() {//drawtekiに関しても同じことを繰り返す
        isVisibleteki = true;//tekiを可視化する（描画する
    }//おわり


    public void hideteki() {//繰り返し
        isVisibleteki = false;//描画しない
    }//おわり

    public boolean isVisibleteki() {//isvisivletekiをtrue,falseにする
        return isVisibleteki;//値を返す
    }//おわり
    // 時間ごとに文字を増やしていく関数
    class DrawingMessageTask extends TimerTask {
        public void run() {//run関数でプログラムを走らせる(timer関数に寄る）
            if (!nextFlag) {//nextflagでないなら
                curPos++;  // 一文字増やす
                if (curPos % MAX_CHAR_IN_PAGE == 0) {//文字数が制限数までいったら
                    nextFlag = true;//つぎのぺージのフラグを立てる
                }//if（文字数制限）おわり
            }//if（次のページにいくふらぐがたっていない）おわり
        }//run関数おわり
    }//drawingmessagetaskおわり
}//messagewindowクラスおわり
