package com.lune.encipher;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.lang.Character.UnicodeBlock;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    private Button btEncry, btDecry, btClear, btCpy, btFil, btShortPoint, btLongPoint, btSpace, btBS;
    private EditText etStr, etN;
    private Spinner spnrEncry;
    private TextView tvCrypt, cross;
    private AlertDialog.Builder dlg;

    private String plainStr, cryptStr;
    private RadioGroup radioMode;
    private AlphaAnimation fadeIn, fadeOut;
    private Toast tst;
    private LinearLayout linearLayout, resultLayout;

//    private int idEncry = R.id.bt_encry, idDecry = R.id.bt_decry, idClear = R.id.bt_clear;    //暗号化、復号、クリアボタンは廃止
    private int idCpy = R.id.bt_cpy, idFil = R.id.bt_fil,
            idShortpoint = R.id.bt_put_short, idLongPoint = R.id.bt_put_long, idSpace = R.id.bt_put_space,idBS = R.id.bt_bs;

    private ArrayList<String> arrayHistory;
    private boolean flgCross, flgResult;    //表示非表示を切り替える要素のためのフラグ

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btEncry = findViewById(idEncry);
//        btDecry = findViewById(idDecry);
//        btClear = findViewById(idClear);
        btCpy = findViewById(idCpy);
        btFil = findViewById(idFil);
        btShortPoint = findViewById(idShortpoint);
        btLongPoint = findViewById(idLongPoint);
        btSpace = findViewById(idSpace);
        btBS = findViewById(idBS);
        etStr = findViewById(R.id.et_str);
        etN = findViewById(R.id.n_kaeji);
        spnrEncry = findViewById(R.id.spinner);
        tvCrypt = findViewById(R.id.tv_crypt);
        cross = findViewById(R.id.cross);
        radioMode = findViewById(R.id.radioMode);

//        btEncry.setOnClickListener(this);
//        btDecry.setOnClickListener(this);
//        btClear.setOnClickListener(this);
        btCpy.setOnClickListener(this);
        btFil.setOnClickListener(this);
        btShortPoint.setOnClickListener(this);
        btLongPoint.setOnClickListener(this);
        btSpace.setOnClickListener(this);
        btBS.setOnClickListener(this);

        fadeIn = new AlphaAnimation(0.0f, 1.0f);        //FadeInとFadeOutのアニメーション設定
        fadeIn.setDuration(300);
        fadeIn.setFillAfter(true);
        fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(10);
        fadeOut.setFillAfter(true);

        cross.startAnimation(fadeOut);
        resultLayout = findViewById(R.id.result);
        resultLayout.startAnimation(fadeOut);
        fadeOut.setDuration(300);

        flgCross = flgResult = false;

        radioMode.check(R.id.radioEncry);

        tst = Toast.makeText(this, " ", Toast.LENGTH_SHORT);
        tst.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);

        etStr.addTextChangedListener(new TextWatcher() {         //入力欄の変更を監視するリスナを追加
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                plainStr = s.toString();
                if(plainStr.equals("")){
                    cross.startAnimation(fadeOut);
                    resultLayout.startAnimation(fadeOut);
                    flgCross = flgResult = false;
                    return;
                }
                if(plainStr.length() >= 1 && flgCross == false){    //文字が入力されていればクリアボタンの表示をする
                    cross.startAnimation(fadeIn);
                    flgCross = true;
                }
                int item = spnrEncry.getSelectedItemPosition();    //選択されている方式を取得

                if(item == 1)  {        //換字式なら
                    switch (radioMode.getCheckedRadioButtonId()){       //ラジオボタンで動作を確定
                        case R.id.radioEncry:     //暗号化が選ばれているなら
                            if(etN.getText().toString().equals("")){        //鍵が空欄なら
                                tst.setText(R.string.noKey);
                                tst.show();
                                break;
                            }else{
                                int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
                                Kaeji kj = new Kaeji(plainStr, key, 0);     //暗号化するメソッドを持つクラスを定義
                                kj.encry();         //暗号化メソッド実行
                                tvCrypt.setTextSize(20);
                                cryptStr = kj.outPut();     //暗号化結果を取得
                                tvCrypt.setText(cryptStr);      //結果をTexiViewにセット
                                if(flgResult == false){     //結果フレームが表示されていなければFadeInさせる
                                    resultLayout.startAnimation(fadeIn);
                                    flgResult = true;
                                }
//                                tvCrypt.startAnimation(fadeIn);     //結果をFadeIn
                                arrayHistory.add(plainStr + "→" + cryptStr);    //履歴用のArrayListにadd
//                                btCpy.setVisibility(View.VISIBLE);        //コピーと上に入れるボタンを表示 -> 結果フレームごと隠すようにしたため不要
//                                btFil.setVisibility(View.VISIBLE);
                                break;
                            }

                        case R.id.radioDecry:     //復号が選ばれているなら
                            if(etN.getText().toString().equals("")){
                                tst.setText(R.string.noKey);
                                tst.show();
                                break;
                            }else{
                                int key = Integer.parseInt(etN.getText().toString());
                                Kaeji kj = new Kaeji(plainStr, key, 1);
                                kj.encry();
                                tvCrypt.setTextSize(20);
                                cryptStr = kj.outPut();
                                tvCrypt.setText(cryptStr);
                                if(flgResult == false){
                                    resultLayout.startAnimation(fadeIn);
                                    flgResult = true;
                                }
//                                tvCrypt.startAnimation(fadeIn);
                                arrayHistory.add(plainStr + "→" + cryptStr);
                                break;
                            }
                    }
                }else if(item == 0){          //モールス信号なら
                    switch (radioMode.getCheckedRadioButtonId()){
                        case R.id.radioEncry:     //暗号化が選ばれているなら
                            Morse morse = new Morse(plainStr, 0);
                            morse.encry();
                            tvCrypt.setTextSize(14);        //モールス符号用にサイズを変更
                            cryptStr = morse.outPut();
                            tvCrypt.setText(cryptStr);
                            if(flgResult == false){
                                resultLayout.startAnimation(fadeIn);
                                flgResult = true;
                            }
//                            tvCrypt.startAnimation(fadeIn);
                            arrayHistory.add(plainStr + "→" + cryptStr);
                            break;

                        case R.id.radioDecry:     //復号が選ばれているなら
                            morse = new Morse(plainStr, 1);
                            morse.encry();
                            tvCrypt.setTextSize(20);
                            cryptStr = morse.outPut();
                            tvCrypt.setText(cryptStr);
                            if(flgResult == false){
                                resultLayout.startAnimation(fadeIn);
                                flgResult = true;
                            }
//                            tvCrypt.startAnimation(fadeIn);
                            arrayHistory.add(plainStr + "→" + cryptStr);
                            break;
                    }
                }
                return;
            }
        });
        etN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() >= 10){
                    tst.setText(R.string.over);
                    tst.show();
                }else{      //鍵が変更されたら文字列を変化させる
                    String tmp = etStr.getText().toString();
                    etStr.setText("");
                    etStr.setText(tmp);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        radioMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {     //ラジオグループの変更を監視するリスナを設定
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String tmp = etStr.getText().toString();
                etStr.setText("");
                etStr.setText(tmp);
            }
        });


        dlg = new AlertDialog.Builder(this);

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
                if(num == 1){
                    linearLayout = findViewById(R.id.key_kaeji);
                    linearLayout.setVisibility(View.VISIBLE);    //換字式が選ばれているならずらす数のEditTextをVisible
                }
                else {
                    linearLayout = findViewById(R.id.key_kaeji);
                    linearLayout.setVisibility(View.GONE);
                }

                if(num == 0){
                    linearLayout = findViewById(R.id.putMorse);
                    linearLayout.setVisibility(View.VISIBLE);
                }else{
                    linearLayout = findViewById(R.id.putMorse);
                    linearLayout.setVisibility(View.INVISIBLE);
                }
                String tmp = etStr.getText().toString();
                etStr.setText("");
                etStr.setText(tmp);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }
    public void clearCross(View v){     //×を押したときの処理
        resultLayout = findViewById(R.id.result);
        etStr.setText("");
//        etN.setText("");
        tvCrypt.setText("");
        resultLayout.startAnimation(fadeOut);
        cross.startAnimation(fadeOut);
        flgCross = flgResult = false;
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

//        if(id == idEncry){     //Clickされたのが暗号化ボタンなら
//            plainStr = etStr.getText().toString();    //入力された文字列を取得
//            int item = spnrEncry.getSelectedItemPosition();    //選択されている方式を取得
//
//            if(item == 0){      //換字式なら
//                if(plainStr.equals("") && etN.getText().toString().equals("")) {                //入力と鍵がないなら
////                    tvCrypt.setText(R.string.noStringAndKey);
//                    tst.setText(R.string.noStringAndKey);
//                    tst.show();
//                } else if(plainStr.equals("")) {                 //入力がないなら
////                    tvCrypt.setText(R.string.noString);
//                    tst.setText(R.string.noString);
//                    tst.show();
//                }else if(etN.getText().toString().equals("")){    //鍵が空欄なら
////                    tvCrypt.setText(R.string.noKey);
//                    tst.setText(R.string.noKey);
//                    tst.show();
//                } else{
//                    int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
//                    Kaeji kj = new Kaeji(plainStr, 0, key);
//                    kj.encry();
//                    tvCrypt.setTextSize(20);
//                    cryptStr = kj.outPut();
//                    tvCrypt.setText(cryptStr);
//                    tvCrypt.startAnimation(fadeIn);
//                    arrayHistory.add(plainStr + "→" + cryptStr);
////                tvCrypt.setText(kaeji(plainStr, mode));
//                    btCpy.setVisibility(View.VISIBLE);
//                    btFil.setVisibility(View.VISIBLE);
//                }
//            }
//            else if(item == 1) {        //モールス信号なら
//                if(plainStr.equals("")) {                 //入力がないなら
////                    tvCrypt.setText(R.string.noString);
//                    tst.setText(R.string.noString);
//                    tst.show();
//                }else{
//                    Morse morse = new Morse(plainStr, 0);
//                    morse.encry();
//                    tvCrypt.setTextSize(14);
//                    cryptStr = morse.outPut();
//                    tvCrypt.setText(cryptStr);
//                    tvCrypt.startAnimation(fadeIn);
//                    arrayHistory.add(plainStr + "→" + cryptStr);
//                    btCpy.setVisibility(View.VISIBLE);
//                    btFil.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//        else if(id == idDecry){ //Clickされたのが復号ボタンなら
//            plainStr = etStr.getText().toString();    //入力された文字列を取得
//            int item = spnrEncry.getSelectedItemPosition();
//
//            if(item == 0){      //換字式なら
//                if(plainStr.equals("") && etN.getText().toString().equals("")) {        //入力と鍵がないなら
////                    tvCrypt.setText((R.string.noStringAndKey));
//                    tst.setText(R.string.noStringAndKey);
//                    tst.show();
//                } else if(plainStr.equals("")) {                 //入力がないなら
////                    tvCrypt.setText(R.string.noString);
//                    tst.setText(R.string.noString);
//                    tst.show();
//                }else if(etN.getText().toString().equals("")){    //鍵が空欄なら
////                    tvCrypt.setText(R.string.noKey);
//                    tst.setText(R.string.noKey);
//                    tst.show();
//                } else{
//                    int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
//                    Kaeji kj = new Kaeji(plainStr, 1, key);
//                    kj.encry();
//                    tvCrypt.setTextSize(20);
//                    cryptStr = kj.outPut();
//                    tvCrypt.setText(cryptStr);
//                    tvCrypt.startAnimation(fadeIn);
//                    arrayHistory.add(plainStr + "→" + cryptStr);
////                tvCrypt.setText(kaeji(plainStr, mode));
//                    btCpy.setVisibility(View.VISIBLE);
//                    btFil.setVisibility(View.VISIBLE);
//                }
//            }
//            else if(item == 1) {        //モールス信号なら
//                if(plainStr.equals("")) {                 //入力がないなら
////                    tvCrypt.setText(R.string.noMorse);
//                    tst.setText(R.string.noMorse);
//                    tst.show();
//                }else{
//                    Morse morse = new Morse(plainStr, 1);
//                    morse.encry();
//                    tvCrypt.setTextSize(20);
//                    cryptStr = morse.outPut();
//                    tvCrypt.setText(cryptStr);
//                    tvCrypt.startAnimation(fadeIn);
//                    arrayHistory.add(plainStr + "→" + cryptStr);
//                    btCpy.setVisibility(View.VISIBLE);
//                    btFil.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//        if(id == idClear){ etStr.setText("");}
        if(id == idCpy){     //ｸﾘｯﾌﾟﾎﾞｰﾄﾞにｺﾋﾟｰボタンなら
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
        else if(id == idBS){
            if(etStr.length() > 0){
                etStr.setText(etStr.getText().toString().substring(0, etStr.length()-1));
                etStr.setSelection(etStr.length());
            }
        }
    }

//    private  void tvFadein(TextView tv){
//        AlphaAnimation alphaFadein = new AlphaAnimation(0.0f, 1.0f);
//        alphaFadein.setDuration(700);
//        alphaFadein.setFillAfter(true);
//        tv.startAnimation(alphaFadein);
//    }

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

        void load(String plainStr, int key, int mode){
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
                    if(tmp >= 'ァ' && tmp <= 'ン')tmp -= 96;       //見ている文字がカタカナならひらがなへ変換
                    if(tmp == 'ー'){
                        crypt.append(listMorses.get(83).toString() + " ");
                        continue;
                    }
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

