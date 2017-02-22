package rpgn1;//パッケージはrpgn1

import java.awt.Container;//awt.Containerインポート

import javax.swing.JFrame;//javax.swing.JFrame jframeのインポート


public class Rpg extends JFrame {//jframeを継承したrpg実行クラス
    public Rpg() {//総括メソッド
        // titleをウィンドウに表示
        setTitle("sampleRPG");

        // 実行する場所
        MainPanel panel = new MainPanel();//mainpanelの使用
        Container contentPane = getContentPane();//ペインの作成
        contentPane.add(panel);//mainpanelのペインを追加

        setResizable(false);//フレームサイズのプレイヤー変更は不可とする

        pack();//windowに合わせた変更は可能とする
    }//RPGウィンドウについてのメソッドはおわり

    public static void main(String[] args) {//mainメソッド
        Rpg frame = new Rpg();//rpgをframeとしてインスタンス化
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//×ボタンで消せるように
        frame.setVisible(true);//フレームの可視化
    }//mainメソッド終了
}//rpgの実行クラス終了