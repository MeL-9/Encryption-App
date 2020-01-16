package com.lune.encipher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Character.UnicodeBlock;
import java.util.Map;

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

    private Button btCpy, btFil, btShortPoint, btLongPoint, btSpace, btBS;
    private EditText etStr, etN;
    private Spinner spnrEncry;
    private TextView tvCrypt, cross;
    private CompoundButton swMode;
    private RadioGroup rdLang;

    private AlertDialog.Builder dlg;
    private AlphaAnimation fadeIn, fadeOut;
    private Toast tst;
    private LinearLayout linearLayout, resultLayout;

    private String plainStr, cryptStr;
    private boolean mode;

    private int idCpy = R.id.bt_cpy, idFil = R.id.bt_fil,
            idShortpoint = R.id.bt_put_short, idLongPoint = R.id.bt_put_long, idSpace = R.id.bt_put_space,idBS = R.id.bt_bs;
    private int lang;

    private ArrayList<String> arrayHistory;
    private boolean flgCross, flgResult;    //表示非表示を切り替える要素のためのフラグ

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        swMode = findViewById(R.id.sw_mode);
        rdLang = findViewById(R.id.radio_lang);
        mode = false;
        lang = 0;

        btCpy.setOnClickListener(this);
        btFil.setOnClickListener(this);
        btShortPoint.setOnClickListener(this);
        btLongPoint.setOnClickListener(this);
        btSpace.setOnClickListener(this);
        btBS.setOnClickListener(this);

        rdLang.check(R.id.radio_jp);
        rdLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_jp)lang = 0;
                else if(checkedId == R.id.radio_eng)lang = 1;
                String tmp = etStr.getText().toString();
                etStr.setText(tmp);
            }
        });

        swMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mode = isChecked;
                String tmp = etStr.getText().toString();
                etStr.setText(tmp);
            }
        });

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
                if(plainStr.length() >= 1 && !flgCross){    //文字が入力されていればクリアボタンの表示をする
                    cross.startAnimation(fadeIn);
                    flgCross = true;
                }
                int item = spnrEncry.getSelectedItemPosition();    //選択されている方式を取得

                if(item == 1)  {        //換字式なら
                    if(mode){       //復号なら
                        if(etN.getText().toString().equals("")){
                            tst.setText(R.string.noKey);
                            tst.show();
                        }else{
                            int key = Integer.parseInt(etN.getText().toString());
                            Kaeji kj = new Kaeji(plainStr, key, 1, 0);
                            kj.encry();
                            tvCrypt.setTextSize(20);
                            cryptStr = kj.outPut();
                            tvCrypt.setText(cryptStr);
                            if(!flgResult){
                                resultLayout.startAnimation(fadeIn);
                                flgResult = true;
                            }
                            arrayHistory.add(plainStr + "→" + cryptStr);
                        }
                    }else{      //暗号化なら
                        if(etN.getText().toString().equals("")){        //鍵が空欄なら
                        tst.setText(R.string.noKey);
                        tst.show();
                        }else{
                            int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
                            Kaeji kj = new Kaeji(plainStr, key, 0, 0);     //暗号化するメソッドを持つクラスを定義
                            kj.encry();         //暗号化メソッド実行
                            tvCrypt.setTextSize(20);
                            cryptStr = kj.outPut();     //暗号化結果を取得
                            tvCrypt.setText(cryptStr);      //結果をTextViewにセット
                            if(!flgResult){     //結果フレームが表示されていなければFadeInさせる
                                resultLayout.startAnimation(fadeIn);
                                 flgResult = true;
                            }
                        arrayHistory.add(plainStr + "→" + cryptStr);    //履歴用のArrayListにadd
                        }
                    }

                }else if(item == 0){          //モールス信号なら
                    if(mode){       //復号なら
                        Morse morse = new Morse(plainStr, 1, lang);
                        morse.encry();
                        tvCrypt.setTextSize(20);
                        cryptStr = morse.outPut();
                        tvCrypt.setText(cryptStr);
                        if(!flgResult){
                            resultLayout.startAnimation(fadeIn);
                            flgResult = true;
                        }
                        arrayHistory.add(plainStr + "→" + cryptStr);
                    }else{      //暗号化なら
                        Morse morse = new Morse(plainStr, 0, lang);
                        morse.encry();
                        tvCrypt.setTextSize(14);        //モールス符号用にサイズを変更
                        cryptStr = morse.outPut();
                        tvCrypt.setText(cryptStr);
                        if(!flgResult){
                            resultLayout.startAnimation(fadeIn);
                            flgResult = true;
                        }
                        arrayHistory.add(plainStr + "→" + cryptStr);
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
                    etStr.setText(tmp);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
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
                etStr.setText(tmp);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    public void clearCross(View v){     //×を押したときの処理
        resultLayout = findViewById(R.id.result);
        etStr.setText("");
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

    public class Encode{
        String plainStr;
        StringBuilder crypt;
        int mode;
        int lang;

        Encode(){   //コンストラクタ
            this("",  0);
        }
        Encode(String plainStr, int mode){
            this.load(plainStr, mode, lang);
            crypt = new StringBuilder();
        }

        public void setplainStr(String plainStr){ this.plainStr = plainStr; }
        public void setMode(int mode){ this.mode = mode; }
        public void setLang(int lang){this.lang = lang;}    //lang: 0 => jp 1 => eng
        public String getplainStr(){ return plainStr; }

        void load(String plainStr, int mode, int lang){
            setplainStr(plainStr);
            setMode(mode);
            setLang(lang);
        }
        public String outPut(){ return this.crypt.toString(); }
        void encry(){ }
    }

    class Kaeji extends Encode{
        int key;

        Kaeji(){ this("", 0, 0, 0); }   //コンストラクタ
        Kaeji(String plainStr, int key, int mode, int lang){ load(plainStr, key, mode, lang); }

        public void setKey(int key){
            this.key = key;
        }

        void load(String plainStr, int key, int mode, int lang){
            super.load(plainStr, mode, lang);
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
        HashMap codeTable;

        Morse(String plainStr, int mode, int lang){ load(plainStr, mode, lang); }

        @Override
        public void load(String plainStr, int mode, int lang){
            super.load(plainStr, mode, lang);
            codeTable = new HashMap<String, String>();
            String[] plains, morses;
            if(lang == 0){      //ひらがななら
                plains = getResources().getStringArray(R.array.plainjp);     //モールス信号のコードリスト読み込み
                morses = getResources().getStringArray(R.array.morsejp);
                for(int i=0; i < morses.length; i++)
                    codeTable.put(plains[i], morses[i]);
            }else if(lang == 1){    //Alphabetなら
                plains = getResources().getStringArray(R.array.plaineng);
                morses = getResources().getStringArray(R.array.morseeng);
                for(int i=0; i < morses.length; i++)
                    codeTable.put(plains[i], morses[i]);
            }
            plains = getResources().getStringArray(R.array.plaincom);   //コードリストに言語共通のものを追加
            morses = getResources().getStringArray(R.array.morsecom);
            for(int i=0; i < plains.length; i++)
                codeTable.put(plains[i], morses[i]);

        }
        @Override
        public void encry(){                    /*暗号化復号メソッド*/
            crypt.setLength(0);
            char tmp;

            if(mode == 0) {     //暗号化なら
                for(int i=0; i < plainStr.length(); i++){
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする

                    if(tmp == '!')tmp = '！';    //言語に関わらず一部文字を例外的に置き換える                     /*特別な文字を置き換える部分*/
                    else if(tmp == '?')tmp = '？';
                    else if(tmp == '@')tmp = '＠';
                    if(lang == 0){      //jpの場合特有の処理
//                        if(tmp == '－')tmp = 'ー';     //'ー'はUnicodeBlock.KATAKANAなため例外的に処理
                        if(tmp == 'ー'){     //'ー'のみ先に処理しないとKATAKANAからの変換で別文字に置き換わる
                            crypt.append(codeTable.get("ー").toString() + " ");
                            continue;
                        }
                        if(Character.UnicodeBlock.of(tmp) == UnicodeBlock.KATAKANA)tmp -= 96;   //見ている文字がカタカナならひらがなへ変換
                    }else if(lang == 1){        //engの場合特有の処理
                        if(tmp >= 'a' && tmp <= 'z')tmp -= 32;      //小文字は大文字に変換
                    }

                    if (codeTable.keySet().contains(String.valueOf(tmp)))                                           /*変換部*/
                        crypt.append(codeTable.get(String.valueOf(tmp)) + " ");
                }
                if(crypt.length() > 0)
                    crypt.deleteCharAt(crypt.length() - 1);     //最後にも空白が入るので削除

            }else if(mode == 1){    //復号なら
                String sonant = "・・", pSound = "・・－－・";     //濁点半濁点
                List<String> plainList = Arrays.asList(plainStr.split(" ",0));  //空白で区切られている暗号を分割
                Iterator<Map.Entry<String, String>> itr;
                Map.Entry<String, String> entry;

                for(int i=0;  i < plainList.size(); i++){
                    if(plainList.get(i).equals(sonant) || plainList.get(i).equals(pSound)){
                        if(i > 0)
                            plainList.set(i - 1, plainList.get(i - 1) +  " " + plainList.get(i));
                    }
                }
                for(String s : plainList){
                    itr = codeTable.entrySet().iterator();
                    while(itr.hasNext()){
                        entry = itr.next();
                        if(entry.getValue().equals(s)){
                            crypt.append(entry.getKey());
                            break;
                        }
                    }
                }
            }
        }
    }
}