package rpgn1;

import java.awt.Container;

import javax.swing.JFrame;


public class Rpg extends JFrame {
    public Rpg() {
        // title
        setTitle("sampleRPG");

        // 実行
        MainPanel panel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        setResizable(false);

        // リサイズ
        pack();
    }

    public static void main(String[] args) {
        Rpg frame = new Rpg();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}