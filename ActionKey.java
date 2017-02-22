package rpgn1;//パッケージはrpgn1


public class ActionKey {//主に主人公の行動のためなどのキー操作クラス
    public static final int NORMAL = 0;//なにもないときは0
    public static final int DETECT_INITIAL_PRESS_ONLY = 1;//キーが押されていることの監視する
    private static final int STATE_RELEASED = 0;//離されたら0になる
    private static final int STATE_PRESSED = 1;//押されていたら1になる
    private static final int STATE_WAITING_FOR_RELEASE = 2;//離されてからの待ち時間は2

    private int mode;//アクションキーのモード
    private int amount;//amount（押されたときに増やす数値）
    private int state;//state

    public ActionKey() {//actinokeyに値を渡さないなら
        this(NORMAL);//通常モード
    }//おわり
    public ActionKey(int mode) {//値をわたしたら
        this.mode = mode;//modeの値にする
        reset();//押されていない状態にリセット
    }//おわり
    public void reset() {//リセットメソッド
        state = STATE_RELEASED;//離された状態に変更する
        amount = 0;//amountを0に
    }


    public void press() {//pressメソッド

        if (state != STATE_WAITING_FOR_RELEASE) {//キーの開放町状態でなければ
            amount++;//amountを増やす
            state = STATE_PRESSED;//状態はおされている状態
        }//if文おわり
    }//pressメソッドおわり


    public void release() {//releaseメソッド
        state = STATE_RELEASED;//離された状態にする（値としては１
    }//おわり


    public boolean isPressed() {//押されているか（true or false
        if (amount != 0) {//amontが0でなければ
            if (state == STATE_RELEASED) {//離されている状態であれば
                amount = 0;//蓄積された値を0に
            } else if (mode == DETECT_INITIAL_PRESS_ONLY) {//まだ押されている状態であれば
                state = STATE_WAITING_FOR_RELEASE;//状態を開放まちにセット
                amount = 0;//蓄積された値を0に変更
            }//elseif文おわり

            return true;//trueを返す
        }//蓄積値がある状態である場合はおわり

        return false;//なければfalseを返す
    }//ispressedおわり
}//actionkeyクラスおわり