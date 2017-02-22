package rpgn1;//パッケージはrpgn1

import java.awt.Graphics;//java.awt.Graphicsをインポート
import java.awt.Image;//awt.Imageをインポート
import java.io.BufferedReader;//io.BufferedReaderをインポート
import java.io.InputStreamReader;//io.InputStreamReaderをインポート
import java.util.StringTokenizer;//util.StringTokenizerをインポート
import java.util.Vector;//util.Vectorをインポート

import javax.swing.ImageIcon;//swing.ImageIconをインポート


public class Map implements Common {//mapに関するクラス（フィールドや王城など
    private int[][] map;//mapの座標の数値設定用
    private int row;//マップ行数
    private int col;//マップ列数
    private int width;//幅の設定
    private int height;//高さの設定

    private static Image chipImage;//配置する地形の種類を指定する際に使用

    //
    private Vector charas = new Vector();//オブジェクトの可変長配列を実装（キャラについて
    //
    private Vector events = new Vector();//オブジェクトの可変長配列を実装（イベントについて

    //
    private MainPanel panel;//mainpanelを使用する

    public Map(String mapFile, String eventFile, MainPanel panel) {//mapメソッド(イベントやマップなどのロード担当
        // mapfileロード
        load(mapFile);

        // eventfileのロード
        loadEvent(eventFile);

        // チップイメージがなければ
        if (chipImage == null) {
            loadImage();//chipimageをロードする
        }//if文終了
    }//メソッド終了

    public void draw(Graphics g, int offsetX, int offsetY) {//drawメソッド（マップ描画する）

        int firstTileX = pixelsToTiles(-offsetX);//画面のoffsetをチップサイズで割ったものに
        //mainpanelにあるウィンドウの幅をチップサイズで割ったもの（つまりtileの最大量
        int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;
        //lasttileXはlasttile or colのどちらか小さいほうをとる
        lastTileX = Math.min(lastTileX, col);

        int firstTileY = pixelsToTiles(-offsetY);//画面の余白部分からの描画に使用
        //mainpanelにあるウィンドウの幅をチップサイズで割ったもの（つまりtileの最大量
        int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;
        //lasttileYはlasttileYかrowのどちらか小さいほうをとる
        lastTileY = Math.min(lastTileY, row);

        //iが最初のタイル以上最後のタイル以下であれば（Y座標
        for (int i = firstTileY; i < lastTileY; i++) {
        	//jが最初のタイル以上最後のタイルの数以下であれば（x座標
            for (int j = firstTileX; j < lastTileX; j++) {
            	//これで最大のマップチップの数を数を取得
                int mapChipNo = map[i][j];
                int cx = (mapChipNo % 8) * CS;//chipsize(x)を使用してチップの場所を取得
                int cy = (mapChipNo / 8) * CS;//chipsize(y)を使用してチップの場所を取得
                //chipを所定の場所にdrawする
                g.drawImage(chipImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY,
                        tilesToPixels(j) + offsetX + CS, tilesToPixels(i) + offsetY + CS,
                        cx, cy, cx + CS, cy + CS, panel);

                // eventの取得
                for (int n=0; n<events.size(); n++) {
                    Event event = (Event)events.get(n);//eventの取得
                    //eventの場所がi,jであれば
                    if (event.x == j && event.y == i) {
                        mapChipNo = event.chipNo;//chipNoを取得（宝箱などの種類
                        cx = (mapChipNo % 8) * CS;//チップをdrawする場所を指定
                        cy = (mapChipNo / 8) * CS;//チップをdrawする場所を指定
                        //eventの場所を指定
                        g.drawImage(chipImage, tilesToPixels(j) + offsetX, tilesToPixels(i) + offsetY,
                                tilesToPixels(j) + offsetX + CS, tilesToPixels(i) + offsetY + CS,
                                cx, cy, cx + CS, cy + CS, panel);
                    }//if文おわり
                }//for文おわり
            }//タイルを置く場所に関する記述おわり
        }//同じくタイルを置く場所に関する記述おわり

        //charaの場所についての記述
        for (int n=0; n<charas.size(); n++) {
            Chara chara = (Chara)charas.get(n);//mapで指定されたキャラを増やす
            chara.draw(g, offsetX, offsetY);//キャラの場所を指定
        }//終わり


    }//描画関係についての記述おわり


    //衝突にかんする記述
    public boolean isHit(int x, int y) {
        // mapchipNoが1,2,5,19なら
        if (map[y][x] == 1 || map[y][x] == 2 || map[y][x] == 5 || map[y][x] == 19) {
            return true;//あたり判定あり
        }

        // charaのあたり判定についての調査(for文をしよう
        for (int i = 0; i < charas.size(); i++) {
            Chara chara = (Chara) charas.get(i);//特定のキャラの位置情報取得
            if (chara.getX() == x && chara.getY() == y) {//キャラの位置とあたり判定のある壁がおなじ座標なら
                return true;//あたり判定あり
            }//if文おわり
        }//for文おわり

        //eventについての
        for (int i = 0; i < events.size(); i++) {//イベントの数を取得するif
            Event event = (Event)events.get(i);//i番目のイベントを取得
            if (event.x == x && event.y == y) {//座標を確認（同じであるか
                return event.isHit;//イベントとの衝突を返す
            }//if文おわり
        }//for文おわり

        //それ以外ならfalseを返す
        return false;
    }//booleanおわり


    public void addChara(Chara chara) {//キャラを増やすメソッド
        charas.add(chara);//キャラを増やす:add参照
    }//おわり


    public void removeChara(Chara chara) {//キャラ撤去
        charas.remove(chara);//remove参照
    }//おわり


    public Chara charaCheck(int x, int y) {//charaの位置チェックのメソッド
        for (int i=0; i<charas.size(); i++) {//charaの数だけforを回す
            Chara chara = (Chara)charas.get(i);//キャラの位置を取得
            if (chara.getX() == x && chara.getY() == y) {//位置が同じであれば
                return chara;//いればキャラを返す
            }//if文終了
        }//for文終了

        return null;//なければnulを返す
    }//checkメソッド終了


    public Event eventCheck(int x, int y) {//上のメソッドのイベント版
        for (int i=0; i<events.size(); i++) {//同じ
            Event event = (Event)events.get(i);//同じ
            if (event.x == x && event.y == y) {//同じ
                return event;//eventを返す
            }//if終わり
        }//for終わり

        return null;//なにもなければnullを返す
    }//eventcheckおわり


    public void removeEvent(Event event) {//event撤去メソッド
        events.remove(event);//eventオブジェクトの撤去
    }//撤去メソッドおわり

 static int pixelsToTiles(double pixels) {//offsetによるマップチップの数
        return (int)Math.floor(pixels / CS);//マップチップ数を計算して返す
    }//おわり


    public static int tilesToPixels(int tiles) {//マップチップの数をpixel数に変えるメソッド
        return tiles * CS;//pixel数を返す
    }//おわり

    public int getRow() {//rowを返すメソッド
        return row;//チップの行の数を返す
    }//おわり

    public int getCol() {//colを返すメソッド
        return col;//チップの列の数を返す
    }//おわり

    public int getWidth() {//マップの幅を返す
        return width;//widthを返す
    }//おわり

    public int getHeight() {//マップの高さを返す
        return height;//heightを返す
    }//おわり

    public Vector getCharas() {//charaの可変長配列についてのメソッド
        return charas;//charasを返す（長さを変えられるメソッド
    }//おわり

    //loadmapchip　マップチップのロード(mapfileからロードする
    private void load(String filename) {
        try {//例外処理が発生しているか確認するtry
            BufferedReader br = new BufferedReader(//要はfileから文字列読み込むための準備
            //fileをよみこむ(inputstreamReaderでバイトを文字列に変換している）
                new InputStreamReader(getClass().getResourceAsStream(filename)));
            //bufferedReaderのreadlineを使用する準備
            String line = br.readLine();
            //マップチップの行数をmapfileからインストール
            row = Integer.parseInt(line);
            line = br.readLine();//
            // マップチップの列数をインストール
            col = Integer.parseInt(line);
            width = col * CS;//マップの幅はcolとチップサイズの掛け算
            height = row * CS;//マップの高さはrowとチップサイズの掛け算
            // mapサイズ設定
            map = new int[row][col];//マップ（配列）を行と列で形成
            for (int i=0; i<row; i++) {//マップチップの行数でforを回す
                line = br.readLine();//i番目の行を読みこむ
                for (int j=0; j<col; j++) {//マップチップの列数でforを回す
                    map[i][j] = Integer.parseInt(line.charAt(j) + "");//これはチップの種類を表す
                }//fot文おわり
            }//for文おわり
            show();//読み込んだmap(数字)をコンソールに出す
        } catch (Exception e) {//例外処理が発生した場合
            e.printStackTrace();//エラーを吐くようにする
        }//catch文おわり
    }//mapチップなどのロードおわり

    //loadEvent　イベント（王様のセリフなど）を取得
    private void loadEvent(String filename) {
        try {//例外処理が発生しているか確認するtry
        	//上に同じく読み込むための準備
            BufferedReader br = new BufferedReader(new InputStreamReader(
            		//今回はUTF-8の文字コードでセリフを読み込む
                    getClass().getResourceAsStream(filename), "UTF-8"));
            String line;//lineを文字列とする
            while ((line = br.readLine()) != null) {//なにも読み込むものがない状態になるまで
                // 空行は読み飛ばす
                if (line.equals("")) continue;
                // コメントは読み飛ばす
                if (line.startsWith("#")) continue;
                //要は文字列を要所要所で切り分けるためのもの
                StringTokenizer st = new StringTokenizer(line, ",");
                //イベントごとにイベントタイプ取得
                //イベント情報を取得
                String eventType = st.nextToken();
                if (eventType.equals("CHARA")) {  // イベントタイプ：キャラなら
                    makeCharacter(st);//切り分けられた部分をキャラとする
                } else if (eventType.equals("TREASURE")) {  // ：宝箱なら
                    makeTreasure(st);//切り分けられた部分を宝箱メソッドに
                } else if (eventType.equals("DOOR")) {  //　：ドアなら
                    makeDoor(st);//切り分けられた部分をドアメソッドに
                } else if (eventType.equals("MOVE")) {  // ：動作になら
                    makeMove(st);//切り分けられた部分を動作メソッドに
                }//if文おわり
            }//while文おわり
        } catch (Exception e) {//例外処理が発生した場合
            e.printStackTrace();//例外が起きた部分をエラーとして吐く
        }//catchおわり
    }//loadeventおわり

    private void loadImage() {//loadimageメソッド
        //マップチップをロード（海とか山とか
        ImageIcon icon = new ImageIcon(getClass().getResource("mapchip.gif"));
        chipImage = icon.getImage();//chipimageにこの画像を渡す
    }//おわり


    private void makeCharacter(StringTokenizer st) {//モブとかをつくる関数

    	//Integer.parseIntは構文解析の関数
    	//文字列として取得したevent chara mapの情報を数値に変換するためのものである
    	//stringtokenizer st=new stringtokenizer(line)で取得したものがここにはいる

        int x = Integer.parseInt(st.nextToken());//上に説明したものをつかい、切り分けたchara情報を読み取る
        int y = Integer.parseInt(st.nextToken());//これはy座標の取得
        //キャラの種類（王様とか）を取得
        int charaNo = Integer.parseInt(st.nextToken());
        //通り抜けられるか否か
        int dir = Integer.parseInt(st.nextToken());
        //動くか、動き回らないかを決める
        int moveType = Integer.parseInt(st.nextToken());
        //セリフを読み取る
        String message = st.nextToken();
        //chara cに上の情報を入れる
        Chara c = new Chara(x, y, charaNo, dir, moveType, this);
        //セリフをを準備しておく
        c.setMessage(message);
        //キャラをゲーム上に追加する
        charas.add(c);
    }//makecharacterおわり


    private void makeTreasure(StringTokenizer st) {//宝箱を作る関数

        int x = Integer.parseInt(st.nextToken());//宝箱のx座標
        int y = Integer.parseInt(st.nextToken());//y座標
        String itemName = st.nextToken();//itemの名前を取得する
        TreasureEvent t = new TreasureEvent(x, y, itemName);//tresureに上で得た情報を入れる
        events.add(t);//ゲーム上に追加
    }//maketresureおわり


    private void makeDoor(StringTokenizer st) {//ドアをつくる（上と同様）
        int x = Integer.parseInt(st.nextToken());//ドアのx座標
        int y = Integer.parseInt(st.nextToken());//ドアのy座標
        DoorEvent d = new DoorEvent(x, y);//上で取得したドアの座標を入力
        events.add(d);//ドアを追加
    }//makedoor終了」


    private void makeMove(StringTokenizer st) {//マップ移動について（上と同様

        int x = Integer.parseInt(st.nextToken());//主人公の初期座標ｘ
        int y = Integer.parseInt(st.nextToken());//同様のｙ
        //チップNoはどれか
        int chipNo = Integer.parseInt(st.nextToken());
        //最初のマップNoは？
        int destMapNo = Integer.parseInt(st.nextToken());
        //主人公がマップ移動した際の移動先
        int destX = Integer.parseInt(st.nextToken());
        //そのy座標
        int destY = Integer.parseInt(st.nextToken());
        //moveeventをmとして上の情報を入力
        MoveEvent m = new MoveEvent(x, y, chipNo, destMapNo, destX, destY);
        events.add(m);//イベントとして取り込む
    }//主人公のマップ移動についての読み込み関連おしまい


    public void show() {//mapをコンソールに書き出すメソッド
        for (int i=0; i<row; i++) {//行数でforを回す
            for (int j=0; j<col; j++) {//列数でforを回す
                System.out.print(map[i][j]);//mapchipの種類を書き出す
            }//forおわり
            System.out.println();//次の行へ
        }//forおわり（map書き出し終了)
    }//showおわり
}//mapに関するクラスおわり

