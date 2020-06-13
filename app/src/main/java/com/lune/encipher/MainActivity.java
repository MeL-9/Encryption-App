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
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.content.Intent;
import android.hardware.camera2.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

class CustomImageButton extends AppCompatImageButton {
    public CustomImageButton(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    @Override
    public boolean performClick(){
        super.performClick();
        return true;
    }
}

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton cross, btPlay;
    private CustomImageButton btPoint;
    private EditText etStr, etN;
    private Spinner spnrMethod;
    private TextView tvCrypt;
    private CompoundButton swMode;
    private RadioGroup rdLang;

    private AlertDialog.Builder dlg;
    private AlphaAnimation fadeIn, fadeOut;
    private Toast tst;
    private LinearLayout linearLayout, resultLayout;
    private CameraManager cameraManager;

    private AdView mAdView;

    private ArrayList<String> arrayHistory;
    private String plainStr, cryptStr, idCamera;
    private int idCpy = R.id.bt_cpy, idFil = R.id.bt_fil, idShortpoint = R.id.bt_put_short,
            idLongPoint = R.id.bt_put_long, idSpace = R.id.bt_put_space, idBS = R.id.bt_bs,
            idShare = R.id.bt_share, idPlay = R.id.bt_play;
    private int lang, wg; //lang: 言語判断, wg: スレッド終了待ち用
    private boolean mode, vibration, flash, volume, stopPlay;  //オンオフのある動作のフラグ
    private boolean flgCross, flgResult, twoButton;    //表示非表示を切り替える要素のためのフラグ


    public void switchMorse(){
        linearLayout = findViewById(R.id.putMorse);
        linearLayout.setVisibility(View.VISIBLE);
        etStr.setInputType(InputType.TYPE_NULL);
        try{
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState){   //裏に行ったあと、アクティビティを再生成する必要があるときに状態を保持しておく
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putString("PlainText", etStr.getText().toString());
        saveInstanceState.putBoolean("Vibration", vibration);
        saveInstanceState.putBoolean("Flash", flash);
        saveInstanceState.putBoolean("Volume", volume);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        btPlay = findViewById(R.id.bt_play);
        btPoint = findViewById(R.id.bt_point);
        etStr = findViewById(R.id.et_str);
        etN = findViewById(R.id.n_kaeji);
        spnrMethod = findViewById(R.id.spinner);
        tvCrypt = findViewById(R.id.tv_crypt);
        cross = findViewById(R.id.cross);
        swMode = findViewById(R.id.sw_mode);
        rdLang = findViewById(R.id.radio_lang);
        mode = false;
        twoButton = false;
        vibration = flash = volume = stopPlay = false;
        idCamera = null;
        lang = wg = 0;

        /*保存されているデータの読み込み*/
        if(savedInstanceState != null) {
            etStr.setText(savedInstanceState.getString("PlainText"));
            vibration = savedInstanceState.getBoolean("Vibration");
            flash = savedInstanceState.getBoolean("Flash");
            volume = savedInstanceState.getBoolean("Volume");
        }
        /*設定の読み込み*/
        SharedPreferences pref = getSharedPreferences("preference_root", Context.MODE_PRIVATE);
        twoButton = pref.getBoolean("twoButton", false);
        etStr.setText(pref.getString("PlainText", ""));
        vibration = pref.getBoolean("Vibration", false);
        flash = pref.getBoolean("Flash", false);
        volume = pref.getBoolean("Volume", false);

        if(twoButton){
            btPoint.setVisibility(View.GONE);
            linearLayout = findViewById(R.id.bt_two);
            linearLayout.setVisibility(View.VISIBLE);
        }

        /*buttonのリスナ登録*/
        btPlay.setOnClickListener(this);
        findViewById(idShare).setOnClickListener(this);
        findViewById(idCpy).setOnClickListener(this);
        findViewById(idFil).setOnClickListener(this);
        findViewById(idShortpoint).setOnClickListener(this);
        findViewById(idLongPoint).setOnClickListener(this);
        findViewById(idSpace).setOnClickListener(this);
        findViewById(idBS).setOnClickListener(this);

        /*FadeInとFadeOutのアニメーション設定*/
        fadeIn = new AlphaAnimation(0.0f, 1.0f);
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
        tst.setGravity(Gravity.CENTER, 0, -170);
        dlg = new AlertDialog.Builder(this);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        cameraManager.registerTorchCallback(new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                idCamera = cameraId;
            }
        }, new Handler());

        try{    //Intentから起動した場合
            Intent intent = getIntent();
            String action = intent.getAction();
            if(Intent.ACTION_SEND.equals(action))
                etStr.setText(intent.getExtras().getCharSequence(Intent.EXTRA_TEXT));
        }catch (NullPointerException e){
            etStr.setText("");
        }

        etStr.addTextChangedListener(new TextWatcher() {         //入力欄の変更を監視するリスナを追加
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                plainStr = s.toString();

                if (plainStr.equals("")) {
                    if (flgCross && flgResult) {
                        cross.startAnimation(fadeOut);
                        resultLayout.startAnimation(fadeOut);
                    }
                    flgCross = flgResult = false;
                    return;
                }
                if (plainStr.length() >= 1 && !flgCross) {    //文字が入力されていればクリアボタンの表示をする
                    cross.startAnimation(fadeIn);
                    flgCross = true;
                }
                int item = spnrMethod.getSelectedItemPosition();    //選択されている方式を取得

                if (item == 1) {        //換字式なら
                    if (mode) {       //復号なら
                        if (etN.getText().toString().equals("")) {
                            tst.setText(R.string.noKey);
                            tst.show();
                        } else {
                            int key = Integer.parseInt(etN.getText().toString());
                            Kaeji kj = new Kaeji(plainStr, key, 1, lang);
                            kj.encry();
                            tvCrypt.setTextSize(20);
                            cryptStr = kj.getCrypt();
                            tvCrypt.setText(cryptStr);
                            if (!flgResult) {
                                resultLayout.startAnimation(fadeIn);
                                flgResult = true;
                            }
                            arrayHistory.add(plainStr + "→" + cryptStr);
                        }
                    } else {      //暗号化なら
                        if (etN.getText().toString().equals("")) {        //鍵が空欄なら
                            tst.setText(R.string.noKey);
                            tst.show();
                        } else {
                            int key = Integer.parseInt(etN.getText().toString());    //ずらす数として鍵を読み込み
                            Kaeji kj = new Kaeji(plainStr, key, 0, lang);     //暗号化するメソッドを持つクラスを定義
                            kj.encry();         //暗号化メソッド実行
                            tvCrypt.setTextSize(20);
                            cryptStr = kj.getCrypt();     //暗号化結果を取得
                            tvCrypt.setText(cryptStr);      //結果をTextViewにセット
                            if (!flgResult) {     //結果フレームが表示されていなければFadeInさせる
                                resultLayout.startAnimation(fadeIn);
                                flgResult = true;
                            }
                        }
                    }

                } else if (item == 0){          //モールス信号なら
                    if (mode) {       //復号なら
                        Morse morse = new Morse(plainStr, 1, lang);
                        morse.encry();
                        tvCrypt.setTextSize(20);
                        cryptStr = morse.getCrypt();
                        tvCrypt.setText(cryptStr);
                        if (!flgResult) {
                            resultLayout.startAnimation(fadeIn);
                            flgResult = true;
                        }
                        arrayHistory.add(plainStr + "→" + cryptStr);
                    } else {      //暗号化なら
                        Morse morse = new Morse(plainStr, 0, lang);
                        morse.encry();
                        tvCrypt.setTextSize(14);        //モールス符号用にサイズを変更
                        cryptStr = morse.getCrypt();
                        tvCrypt.setText(cryptStr);
                        if (!flgResult) {
                            resultLayout.startAnimation(fadeIn);
                            flgResult = true;
                        }
                        arrayHistory.add(plainStr + "→" + cryptStr);
                    }
                }
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

        arrayHistory = new ArrayList<String>(){{
            add("変換履歴");
        }
        };

        //Spinnerに使うAdapterの作成
        ArrayAdapter adptEncry = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.list_encry));
        adptEncry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrMethod.setAdapter(adptEncry);  //AdapterをSpinnerにセット
        spnrMethod.setPromptId(R.string.sel_encry);
        spnrMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {     //SpinnerのItemが選ばれたとき
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Spinner spnr = (Spinner)parent;
                int num = spnr.getSelectedItemPosition();
                etStr.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                if(num == 1){                                    //換字式が選ばれているなら
                    linearLayout = findViewById(R.id.key_kaeji);
                    linearLayout.setVisibility(View.VISIBLE);    //ずらす数のEditTextをVisible
                }
                else {
                    linearLayout = findViewById(R.id.key_kaeji);
                    linearLayout.setVisibility(View.GONE);
                }

                if(num == 0){                                   //モールスが選ばれているなら
                    if(mode){   //復号モード
                        switchMorse();
                    }
                }else{
                    linearLayout = findViewById(R.id.putMorse);
                    linearLayout.setVisibility(View.GONE);
                }
                String tmp = etStr.getText().toString();
                etStr.setText(tmp);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        swMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mode = isChecked;
                if(mode){   //復号モードになったとき
                    if(spnrMethod.getSelectedItemPosition() == 0){  //スピナーでモールスが選ばれているなら
                        switchMorse();
                    }
                }else{  //暗号化モードになったとき
                    linearLayout = findViewById(R.id.putMorse);
                    linearLayout.setVisibility(View.GONE);
                    etStr.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }
                String tmp = etStr.getText().toString();
                etStr.setText(tmp);
            }
        });

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

        btPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long time = 0, ref = 200;

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        btPoint.setBackground(getResources().getDrawable(R.drawable.round_active));
                        return false;
                    case MotionEvent.ACTION_UP:
                        btPoint.setBackground(getResources().getDrawable(R.drawable.round_pointbutton));
                        time = event.getEventTime() - event.getDownTime();
                        if(time < ref){ etStr.append("･"); }
                        else{ etStr.append("－"); }
                        return true;
                }
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.option, menu);

        MenuItem item = menu.findItem(R.id.vibration);
        if(vibration)item.setIcon(R.drawable.vibration_on);
        else item.setIcon(R.drawable.vibration_off);

        item = menu.findItem(R.id.flash);
        if(flash)item.setIcon(R.drawable.flash_on);
        else item.setIcon(R.drawable.flash_off);

        item = menu.findItem(R.id.volume);
        if(volume)item.setIcon(R.drawable.volume_on);
        else item.setIcon(R.drawable.volume_off);

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem menuItem){
        Intent intent;
        SharedPreferences pref = getSharedPreferences("preference_root", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        switch (menuItem.getItemId()){
            case R.id.vibration:
                vibration = !vibration;
                editor.putBoolean("Vibration", vibration);
                editor.apply();
                invalidateOptionsMenu();
                break;
            case R.id.flash:
                flash = !flash;
                editor.putBoolean("Flash", flash);
                editor.apply();
                invalidateOptionsMenu();
                break;
            case R.id.volume:
                volume = !volume;
                editor.putBoolean("Volume", volume);
                editor.apply();
                invalidateOptionsMenu();
                break;
            case R.id.menu_code:
                intent = new Intent(this, ShowMorseCode.class);
                startActivity(intent);
                break;
            case R.id.menu_history:
                intent = new Intent(this, ShowHistory.class);
                intent.putExtra("com.lune.encipher.arrayHistory", arrayHistory);
                startActivity(intent);
                break;
            /*case R.id.menu_pref:  //設定表示
                dlg.setTitle(R.string.pref);
                dlg.setMessage(R.string.pref_alart);
                dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, Setting.class);
                        startActivity(intent);
                        finish();
                    }
                });
                dlg.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dlg.show();
                intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                break;*/
        }
        return true;
    }

    public void clearCross(View v){     //×を押したときの処理
        resultLayout = findViewById(R.id.result);
        etStr.setText("");
        tvCrypt.setText("");
        resultLayout.startAnimation(fadeOut);
        cross.startAnimation(fadeOut);
        flgCross = flgResult = false;
    }
    @Override
    public void onClick(View v){
        int id = v.getId();
        if (id == idPlay) {
            if (spnrMethod.getSelectedItemPosition() == 0 && !swMode.isChecked()) {   //モールス
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        playMorse();
                    }
                });
                if (wg == 0) {
                    wg++;
                    th.start();
                } else if (wg == 1) {
                    stopPlay = true;
                }
            } else {
                tst.setText(R.string.onlyMorse);
                tst.show();
            }
        }
        else if (id == idShare) {
            String[] shareTypes = new String[2];

            shareTypes[0] = "結果のみ";
            shareTypes[1] = "[元の文] を [結果] に変換しました！";

            dlg.setTitle("共有する内容");
            dlg.setItems(shareTypes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String shareText;
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    switch(which){
                        case 0:
                            shareText = tvCrypt.getText().toString();
                            break;
                        case 1:
                            shareText = etStr.getText() + " を " + tvCrypt.getText() +
                                    " に変換しました！";
                            break;
                        default:
                            shareText = "";
                    }
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(intent);
                }
            });
            dlg.show();
        }
        else if(id == idCpy){     //ｸﾘｯﾌﾟﾎﾞｰﾄﾞにｺﾋﾟｰボタンなら
            ClipboardManager cbm = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
            if (cbm == null) {
                tst = Toast.makeText(this, "Copy failed.", Toast.LENGTH_SHORT);
                tst.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
                tst.show();
            } else {
                cbm.setPrimaryClip(ClipData.newPlainText("", tvCrypt.getText().toString()));
                tst = Toast.makeText(this, "Copied.", Toast.LENGTH_SHORT);
                tst.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
                tst.show();
            }
        }
        else if (id == idFil) {
            etStr.setText(tvCrypt.getText());
            swMode.setChecked(!swMode.isChecked());
        }
        else if (id == idShortpoint) { etStr.append("･"); }
        else if (id == idLongPoint) { etStr.append("－");}
        else if (id == idSpace) { etStr.append(" ");}
        else if (id == idBS) {
            if (etStr.length() > 0) {
                etStr.setText(etStr.getText().toString().substring(0, etStr.length()-1));
                etStr.setSelection(etStr.length());
            }
        }
    }

    private void waitTime(int time){    //時間待機
        try{
            Thread.sleep(time);
        }catch (InterruptedException e){e.printStackTrace();}
    }

    /*振動再生メソッド*/
    public void playVibration(){
        String morse = tvCrypt.getText().toString();
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        for (int i = 0; i < morse.length(); i++) {
            if (stopPlay) break;
            char currentLetter = morse.charAt(i);
            if(currentLetter == '･'){   //'・'なら0.1秒間再生
                vibrator.vibrate(1000);
                waitTime(100);
                vibrator.cancel();
            }else if(currentLetter == '－'){
                vibrator.vibrate(1000);
                waitTime(300);
                vibrator.cancel();
            }else{
                waitTime(300);  //空白は0.3秒間待機
            }
            waitTime(100);  //各要素の間隔0.1秒
        }
    }
    /*点滅再生メソッド*/
    public void playFlash(){
        String morse = tvCrypt.getText().toString();
        try {
            for (int i = 0; i < morse.length(); i++) {
                if (stopPlay) break;
                char currentLetter = morse.charAt(i);
                if (currentLetter == '･') {   //'・'なら0.1秒間再生
                    cameraManager.setTorchMode(idCamera, true);
                    waitTime(100);
                    cameraManager.setTorchMode(idCamera, false);
                } else if (currentLetter == '－') {
                    cameraManager.setTorchMode(idCamera, true);
                    waitTime(300);
                    cameraManager.setTorchMode(idCamera, false);
                } else {
                    waitTime(300);  //空白は0.3秒間待機
                }
                waitTime(100);  //各要素の間隔0.1秒
            }
        } catch (CameraAccessException e) {
            tst.setText("err in flash");
            tst.show();
        }
    }
    /*音声再生メソッド*/
    public void playSound(){
        String morse = tvCrypt.getText().toString();
        int sampleRate = 8000;
        int hz = 720;
        int duration = 16000;   //大体1000msになる

        double[] shortSamples = new double[duration];
        short[] shortBuffer = new short[duration];

        for (int i = 0; i < duration; i++) {    //正弦波生成
            shortSamples[i] = Math.sin(2 * Math.PI * i * hz / sampleRate);
            shortBuffer[i] = (short)(shortSamples[i] * Short.MAX_VALUE);
        }
        AudioTrack shortAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, shortBuffer.length,
                AudioTrack.MODE_STATIC);
        shortAudioTrack.write(shortBuffer, 0, shortBuffer.length);

        for (int i = 0; i < morse.length(); i++) {
            if (stopPlay) break;
            char currentLetter = morse.charAt(i);
            if (currentLetter == '･') {   //'・'なら0.1秒間再生
                shortAudioTrack.play();
                waitTime(100);
                shortAudioTrack.stop();
            } else if (currentLetter == '－') {
                shortAudioTrack.play(); //'－'なら0.3秒間再生
                waitTime(300);
                shortAudioTrack.stop();
            } else {
                waitTime(300);  //空白は0.3秒間待機
            }
            waitTime(100);  //各要素の間隔0.1秒
        }
    }
    /*モールス再生メソッド*/
    Thread vibTh, flashTh, soundTh;
    public void playMorse() {
        btPlay.setImageDrawable(getDrawable(R.drawable.stop));
        vibTh = new Thread(new Runnable() {
            @Override
            public void run() {
                playVibration();
            }
        });
        flashTh = new Thread(new Runnable() {
            @Override
            public void run() {
                playFlash();
            }
        });
        soundTh = new Thread(new Runnable() {
            @Override
            public void run() {
                playSound();
            }
        });
        if (vibration)
            vibTh.start();
        if (flash)
            flashTh.start();
        if (volume)
            soundTh.start();
        try {
            vibTh.join();
            flashTh.join();
            soundTh.join();
            stopPlay = false;
            btPlay.setImageDrawable(getDrawable(R.drawable.play));
            wg--;
        } catch (InterruptedException e) {
            tst.setText("err in playMorse");
            tst.show();
        }
    }

    class Encode{
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

        void setplainStr(String plainStr){ this.plainStr = plainStr; }
        void setMode(int mode){ this.mode = mode; }
        void setLang(int lang){this.lang = lang;}    //lang: 0 => jp 1 => eng
        String getplainStr(){ return plainStr; }

        void load(String plainStr, int mode, int lang){
            setplainStr(plainStr);
            setMode(mode);
            setLang(lang);
        }
        String getCrypt(){ return this.crypt.toString(); }
        void encry(){ }
    }

    class Kaeji extends Encode{
        int key;
        List<String> listTable;

        Kaeji(){ this("", 0, 0, 0); }   //コンストラクタ
        Kaeji(String plainStr, int key, int mode, int lang){ load(plainStr, key, mode, lang); }

        void setKey(int key){
            this.key = key;
        }

        void load(String plainStr, int key, int mode, int lang){
            super.load(plainStr, mode, lang);
            setKey(key);
            String[] plain = null;
            String[] com = null;
            if(lang == 0)plain = getResources().getStringArray(R.array.plainjp);
            else /*if(lang == 1)*/plain = getResources().getStringArray(R.array.plaineng);
            com = getResources().getStringArray(R.array.plaincom);

            String[] table = new String[plain.length + com.length ];
            System.arraycopy(plain, 0, table, 0, plain.length);
            System.arraycopy(com, 0, table, plain.length, com.length);

            listTable = Arrays.asList(table);
        }

        @Override
        void encry() {
            crypt.setLength(0);     //暗号文の初期化
            char tmp = 0;
            int x = 0;
            int index = 0;

            while(key > listTable.size() + 1)    //余計に大きいnを範囲内までカット //'ぁ'　== (char)12353. 'ん' == (char)12435
                key -= listTable.size() + 1;

            if(mode == 0){     //暗号化なら
                for(int i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする

                    if(tmp == '!')tmp = '！';    //言語に関わらず一部文字を例外的に置き換える                     /*特別な文字を置き換える部分*/
                    else if(tmp == '?')tmp = '？';
                    else if(tmp == '@')tmp = '＠';
                    if(lang == 0){      //jpの場合特有の処理
                        if(tmp != 'ー' && Character.UnicodeBlock.of(tmp) == UnicodeBlock.KATAKANA)tmp -= 96;   //見ている文字がカタカナならひらがなへ変換
                    }else if(lang == 1){        //engの場合特有の処理
                        if(tmp >= 'a' && tmp <= 'z')tmp -= 32;      //小文字は大文字に変換
                    }

                    x = key;                                                                                    /*変換部*/
                    if(listTable.contains(String.valueOf(tmp))) {     //見ている文字が含まれているなら
                        index = listTable.indexOf(String.valueOf(tmp));
                        if(index + x < listTable.size())
                            index += x;
                        else {                     //ずらすとリストの最後を通り越す場合
                            x -= listTable.size() - index;
                            index = 0;
                            index += x;
                        }
                        crypt.append(listTable.get(index));
                    }              //対象でないならそのまま
                    else crypt.append(tmp);
                }
            }
            else if(mode == 1){    //復号なら
                for(int i = 0; i < plainStr.length(); i++){    //文字列の長さ分ループ
                    tmp = plainStr.charAt(i);   //文字列を1文字ずつチェックする
                    x = key;
                    if(listTable.contains(String.valueOf(tmp))) {     //見ている文字が含まれているなら
                        index = listTable.indexOf(String.valueOf(tmp));
                        if(index - x >= 0)
                            index -= x;
                        else {                     //ずらすと'ぁ'を通り越す場合
                            x = x - index - 1;
                            index = listTable.size() - 1;
                            index -= x;
                        }
                        crypt.append(listTable.get(index));
                    }              //対象でないならそのまま
                    else crypt.append(tmp);
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
        public void encry() {                    /*暗号化復号メソッド*/
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
                String sonant = "･･", pSound = "･･－－･";     //濁点半濁点
                List<String> plainList = Arrays.asList(plainStr.split(" ",0));  //空白で区切られている暗号を分割
                Iterator<Map.Entry<String, String>> itr;
                Map.Entry<String, String> entry;

                for(int i=0;  i < plainList.size(); i++)    //濁点、半濁点を前の文字と結合
                    if(i > 0 && (plainList.get(i).equals(sonant) || plainList.get(i).equals(pSound)))
                        plainList.set(i - 1, plainList.get(i - 1) + " " + plainList.get(i));

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