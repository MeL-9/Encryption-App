package com.lune.encipher;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.lang.Character.UnicodeBlock;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btEncry, btDecry, btClear, btCpy, btFil, btShortPoint, btLongPoint, btSpace;
    private EditText etStr, etN;
    private Spinner spnrEncry;
    private TextView tvCrypt;
    private AlertDialog.Builder dlg;

    private String plainStr, cryptStr;
    private Toast tst;

    private int idEncry = R.id.bt_encry, idDecry = R.id.bt_decry, idCpy = R.id.bt_cpy, idFil = R.id.bt_fil,
            idShortpoint = R.id.bt_put_short, idLongPoint = R.id.bt_put_long, idSpace = R.id.bt_put_space,
            idClear = R.id.bt_clear;

    private ArrayList<String> arrayHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btEncry = findViewById(idEncry);
        btDecry = findViewById(idDecry);
        btClear = findViewById(idClear);
        btCpy = findViewById(idCpy);
        btFil = findViewById(idFil);
        btShortPoint = findViewById(idShortpoint);
        btLongPoint = findViewById(idLongPoint);
        btSpace  = findViewById(idSpace);

        btEncry.setOnClickListener(this);
        btDecry.setOnClickListener(this);
        btClear.setOnClickListener(this);
        btCpy.setOnClickListener(this);
        btFil.setOnClickListener(this);
        btShortPoint.setOnClickListener(this);
        btLongPoint.setOnClickListener(this);
        btSpace.setOnClickListener(this);

        etStr = findViewById(R.id.et_str);
        etN = findViewById(R.id.n_kaeji);
        spnrEncry = findViewById(R.id.spinner);
        tvCrypt = findViewById(R.id.tv_crypt);

        dlg = new AlertDialog.Builder(this);

        tst = Toast.makeText(this, " ", Toast.LENGTH_SHORT);
        tst.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);

        arrayHistory = new ArrayList<String>(){{
                add("変換履歴");
            }
        };

        //Spinnerに使うAdapterの作成
        ArrayAdapter adptEncry = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.list_encry));
        adptEncry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrEncry.setAdapter(adptEncry);  //AdapterをSpinnerにセット
        spnrEncry.setPromptId(R.string.sel_encry);
        spnrEncry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {     //SpinnerのItemが選ばれたとき
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Spinner spnr = (Spinner)parent;
                int num = spnr.getSelectedItemPosition();

                if(num == 0)etN.setVisibility(View.VISIBLE);    //換字式が選ばれているならずらす数のEditTextをVisible
                else etN.setVisibility(View.GONE);

                if(num == 1){
                    btShortPoint.setVisibility(View.VISIBLE);
                    btLongPoint.setVisibility(View.VISIBLE);
                    btSpace.setVisibility(View.VISIBLE);
                }else{
                    btShortPoint.setVisibility(View.INVISIBLE);
                    btLongPoint.setVisibility(View.INVISIBLE);
                    btSpace.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent intent;

        switch (menuItem.getItemId()){
            case R.id.menu_hiragana:
                dlg.setTitle(R.string.available);
                dlg.setMessage(R.string.hiragana);
                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int which) {
                    }
                });
                dlg.show();
                break;

            case R.id.menu_code:
                intent = new Intent(this, ShowMorseCode.class);
                startActivity(intent);
                break;

            case R.id.menu_morse:
                dlg.setTitle(R.string.about_morse);
                dlg.setMessage(R.string.explain_morse);
                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dlg.show();
                break;

            case R.id.menu_history:
                intent = new Intent(this, ShowHistory.class);
                intent.putExtra("com.lune.encipher.arrayHistory", arrayHistory);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onClick(View v){
        int id = v.getId();

        if(id == idEncry){     //Clickされたのが暗号化ボタンなら
            plainStr = etStr.getText().toString();    //入力された文字列を取得
            int item = spnrEncry.getSelectedItemPosition();    //選択されている方式を取得

            if(item == 0){      //換字式なら
                if(plainStr.equals("") && etN.getText().toString().equals("")) {                //入力と鍵がないなら
//                    tvCrypt.setText(R.string.noStringAndKey);
                    tst.setText(R.string.noStringAndKey);
                    tst.show();
                } else if(plainStr.equals("")) {                 //入力がないなら
//                    tvCrypt.setText(R.string.noString);
                    tst.setText(R.string.noString);
                    tst.show();
                }else if(etN.getText().toString().equals("")){    //鍵が空欄なら
//                    tvCrypt.setText(R.string.noKey);
                    tst.setText(R.string.noKey);
                    tst.show();
                } else{
                    int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
                    Kaeji kj = new Kaeji(plainStr, 0, key);
                    kj.encry();
                    tvCrypt.setTextSize(20);
                    cryptStr = kj.outPut();
                    tvCrypt.setText(cryptStr);
                    tvFadein(tvCrypt);
                    arrayHistory.add(plainStr + "→" + cryptStr);
//                tvCrypt.setText(kaeji(plainStr, mode));
                    btCpy.setVisibility(View.VISIBLE);
                    btFil.setVisibility(View.VISIBLE);
                }
            }
            else if(item == 1) {        //モールス信号なら
                if(plainStr.equals("")) {                 //入力がないなら
//                    tvCrypt.setText(R.string.noString);
                    tst.setText(R.string.noString);
                    tst.show();
                }else{
                    Morse morse = new Morse(plainStr, 0);
                    morse.encry();
                    tvCrypt.setTextSize(14);
                    cryptStr = morse.outPut();
                    tvCrypt.setText(cryptStr);
                    tvFadein(tvCrypt);
                    arrayHistory.add(plainStr + "→" + cryptStr);
                    btCpy.setVisibility(View.VISIBLE);
                    btFil.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(id == idDecry){ //Clickされたのが復号ボタンなら
            plainStr = etStr.getText().toString();    //入力された文字列を取得
            int item = spnrEncry.getSelectedItemPosition();

            if(item == 0){      //換字式なら
                if(plainStr.equals("") && etN.getText().toString().equals("")) {        //入力と鍵がないなら
//                    tvCrypt.setText((R.string.noStringAndKey));
                    tst.setText(R.string.noStringAndKey);
                    tst.show();
                } else if(plainStr.equals("")) {                 //入力がないなら
//                    tvCrypt.setText(R.string.noString);
                    tst.setText(R.string.noString);
                    tst.show();
                }else if(etN.getText().toString().equals("")){    //鍵が空欄なら
//                    tvCrypt.setText(R.string.noKey);
                    tst.setText(R.string.noKey);
                    tst.show();
                } else{
                    int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
                    Kaeji kj = new Kaeji(plainStr, 1, key);
                    kj.encry();
                    tvCrypt.setTextSize(20);
                    cryptStr = kj.outPut();
                    tvCrypt.setText(cryptStr);
                    tvFadein(tvCrypt);
                    arrayHistory.add(plainStr + "→" + cryptStr);
//                tvCrypt.setText(kaeji(plainStr, mode));
                    btCpy.setVisibility(View.VISIBLE);
                    btFil.setVisibility(View.VISIBLE);
                }
            }
            else if(item == 1) {        //モールス信号なら
                if(plainStr.equals("")) {                 //入力がないなら
//                    tvCrypt.setText(R.string.noMorse);
                    tst.setText(R.string.noMorse);
                    tst.show();
                }else{
                    Morse morse = new Morse(plainStr, 1);
                    morse.encry();
                    tvCrypt.setTextSize(20);
                    cryptStr = morse.outPut();
                    tvCrypt.setText(cryptStr);
                    tvFadein(tvCrypt);
                    arrayHistory.add(plainStr + "→" + cryptStr);
                    btCpy.setVisibility(View.VISIBLE);
                    btFil.setVisibility(View.VISIBLE);
                }
            }
        }
        else if(id == idClear){ etStr.setText("");}
        else if(id == idCpy){     //ｸﾘｯﾌﾟﾎﾞｰﾄﾞにｺﾋﾟｰボタンなら
            ClipboardManager cbm = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
            if(cbm == null){
                tst = Toast.makeText(this, "Copy failed.", Toast.LENGTH_SHORT);
                tst.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
                tst.show();
            }else{
                cbm.setPrimaryClip(ClipData.newPlainText("", tvCrypt.getText().toString()));
                tst = Toast.makeText(this, "Copied.", Toast.LENGTH_SHORT);
                tst.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
                tst.show();
            }
        }
        else if(id == idFil){ etStr.setText(tvCrypt.getText());}
        else if(id == idShortpoint){ etStr.append("・"); }
        else if(id == idLongPoint){ etStr.append("－");}
        else if(id == idSpace){ etStr.append(" ");}
    }

    private  void tvFadein(TextView tv){
        AlphaAnimation alphaFadein = new AlphaAnimation(0.0f, 1.0f);
        alphaFadein.setDuration(700);
        alphaFadein.setFillAfter(true);
        tv.startAnimation(alphaFadein);
    }

    public class Encode{
        String plainStr;
        StringBuilder crypt;
        int mode;

        Encode(){   //コンストラクタ
            this("", 0, 0);
        }
        Encode(String plainStr, int key, int mode){
            this.load(plainStr, mode);
            crypt = new StringBuilder();
        }

        public void setplainStr(String plainStr){ this.plainStr = plainStr; }
        public void setMode(int mode){ this.mode = mode; }
        public String getplainStr(){ return plainStr; }

        void load(String plainStr, int mode){
            setplainStr(plainStr);
            setMode(mode);
        }
        public String outPut(){ return this.crypt.toString(); }
        void encry(){ }
    }

    class Kaeji extends Encode{
        int key;

        Kaeji(){ this("", 0, 0); }   //コンストラクタ
        Kaeji(String plainStr, int key, int mode){ load(plainStr, key, mode); }

        public void setKey(int key){
            this.key = key;
        }

        void load(String plainStr, int mode, int key){
            super.load(plainStr, mode);
            setKey(key);
        }

        @Override
        void encry(){
            crypt.setLength(0);     //暗号文の初期化
            char tmp = 0;
            int x = 0;

            while(key > 'ん' - 'ぁ' + 1)    //余計に大きいnを範囲内までカット //'ぁ'　== (char)12353. 'ん' == (char)12435
                key -= 'ん' - 'ぁ' + 1;

            if(mode == 0){     //暗号化なら
                for(int i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする
                    x = key;
                    if(Character.UnicodeBlock.of(tmp) == Character.UnicodeBlock.HIRAGANA) {     //見ている文字がひらがななら
                        if(tmp + x <= 'ん')
                            tmp += x;
                        else {                     //ずらすと'ん'を通り越す場合
                            x -= 'ん' - tmp + 1;
                            tmp = 'ぁ';
                            tmp += x;
                        }
                    }              //ひらがなでないならそのまま
                    crypt.append(tmp);
                }
            }
            else if(mode == 1){    //復号なら
                for(int i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする
                    x = key;
                    if(Character.UnicodeBlock.of(tmp) == Character.UnicodeBlock.HIRAGANA) {     //見ている文字がひらがななら
                        if(tmp - x >= 'ぁ')
                            tmp -= x;
                        else {                     //ずらすと'ぁ'を通り越す場合
                            x -= tmp - 'ぁ' + 1;
                            tmp = 'ん';
                            tmp -= x;
                        }
                    }              //ひらがなでないならそのまま
                    crypt.append(tmp);
                }
            }
        }
    }

    class Morse extends Encode{
        List listMorses = new ArrayList<String>();

        Morse(String plainStr, int mode){ load(plainStr, mode); }

        public void load(String plainStr, int mode){
            super.load(plainStr, mode);
            String[] morses = getResources().getStringArray(R.array.list_morse);     //モールス信号のコードリスト読み込み
            listMorses = Arrays.asList(morses);
        }
        @Override
        public void encry(){
            crypt.setLength(0);     //暗号文の初期化
            char tmp;
            String afterConverting = "", sonant = "・・", pSound = "・・－－・";
            int index;

            if(mode == 0){     //暗号化なら
                for(int i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする
                    if(Character.UnicodeBlock.of(tmp) == Character.UnicodeBlock.HIRAGANA) {     //見ている文字がひらがななら
                        index = tmp - 'ぁ';      //tmpが何番目の文字なのか
                        afterConverting = listMorses.get(index).toString();     //コードリストのindex番目を代入
                        crypt.append(afterConverting + " ");        //変換後と空白を追加
                    }else {              //ひらがなでないならそのまま
                        crypt.append(tmp);
                    }
                }
                crypt.deleteCharAt(crypt.length() - 1);     //最後にも空白が入るので削除
            }
            else if(mode == 1){    //復号なら
                crypt.setLength(0);
                StringBuilder check = new StringBuilder();
                int i;
                List<String> strMorses = new ArrayList<String>();       //モールス文をリスト化

                for(i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする
                    if(tmp == '－' || tmp == '・') {     //見ている文字が'－'または'・'なら
                        check.append(tmp);
                    }else if(tmp == ' '){          //' 'ならリストに追加する
                        strMorses.add(check.toString());
                        check.setLength(0);     //checkの中身をクリア

                    }
                }
                strMorses.add(check.toString());        //文のリストに追加

                for(i=0;i < strMorses.size();i++){      //濁点、半濁点の検出＆結合
                    String currentStr = strMorses.get(i);
                    if(currentStr.equals(sonant) || currentStr.equals(pSound)){
                        if(i > 0){
                            strMorses.set(i - 1, strMorses.get(i - 1) + " " + currentStr);
                            strMorses.remove(i);
                            i--;
                        }else{
                            crypt.append("不適切な値");
                            return;
                        }

                    }
                }
                for(String str: strMorses){
                    if(listMorses.contains(str)){       //コードリストにある文字列ならその文字を追加
                        crypt.append((char)('ぁ' + listMorses.indexOf(str)));
//                    crypt.append(str + ", ");     //デバッグ用、strMorseの要素ごとに出力
                    }else{      //なければ"？"を追加
                        crypt.append("？");
                    }

                }
            }
        }
    }

//    public String kaeji(String plain, int id){  //換字式暗号化メソッド
//        char tmp = 0;
//        StringBuilder crypt = new StringBuilder();
//
//        int n = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵nを読み込み
//        int x = 0;
//
//        while(n > 'ん' - 'ぁ' + 1)    //余計に大きいnを範囲内までカット
//            n -= 'ん' - 'ぁ' + 1;
//
//        if(id == 0){     //暗号化なら
//            for(int i = 0; i < plain.length(); i++){    //文字列の長さ分ループ
//                tmp = plain.charAt(i);   //文字列を1文字ずつチェックする
//                x = n;
//                if(UnicodeBlock.of(tmp) == UnicodeBlock.HIRAGANA) {     //見ている文字がひらがななら
//                    if(tmp + x <= 'ん')
//                        tmp += x;
//                    else {                     //ずらすと'ん'を通り越す場合
//                        x -= 'ん' - tmp + 1;
//                        tmp = 'ぁ';
//                        tmp += x;
//                    }
//                }              //ひらがなでないならそのまま
//                crypt.append(tmp);
//            }
//        }
//        else if(id == 1){    //復号なら
//            for(int i = 0; i < plain.length(); i++){    //文字列の長さ分ループ
//                tmp = plain.charAt(i);   //文字列を1文字ずつチェックする
//                x = n;
//                if(UnicodeBlock.of(tmp) == UnicodeBlock.HIRAGANA) {     //見ている文字がひらがななら
//                    if(tmp - x >= 'ぁ')
//                        tmp -= x;
//                    else {                     //ずらすと'ぁ'を通り越す場合
//                        x -= tmp - 'ぁ' + 1;
//                        tmp = 'ん';
//                        tmp -= x;
//                    }
//                }              //ひらがなでないならそのまま
//                crypt.append(tmp);
//            }
//        }
//        return crypt.toString();
//    }
}

