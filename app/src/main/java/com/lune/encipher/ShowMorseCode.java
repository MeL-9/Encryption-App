package com.lune.encipher;

import android.os.Bundle;
import android.app.Activity;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;

//import java.util.HashMap;
//import java.util.Map;
//import java.util.Iterator;

public class ShowMorseCode extends Activity{
    private ScrollView sv;
    private LinearLayout llHead, llCont;
    private TextView tvHead, tvJp, tvEng, tvCom;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;

        sv = new ScrollView(this);
        sv.setLayoutParams(new ScrollView.LayoutParams(matchParent, matchParent));

        llHead = new LinearLayout(this);
        llHead.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent));
        llHead.setOrientation(LinearLayout.VERTICAL);
        llCont = new LinearLayout(this);
        llCont.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent));
        llCont.setOrientation(LinearLayout.HORIZONTAL);

        tvHead = new TextView(this);
        tvHead.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent));
        tvHead.setTextSize(22);
        tvHead.setText("モールス符号対応表\n");
        tvJp = new TextView(this);
        tvJp.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent, 1));
        tvJp.setPadding(15, 0, 0, 0);
        tvJp.setText(getString(R.string.radio_jp) + "\n");
        tvJp.setTextSize(16);
        tvEng = new TextView(this);
        tvEng.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent, 2));
        tvEng.setText(getString(R.string.radio_eng) + "\n");
        tvEng.setTextSize(16);
        tvCom = new TextView(this);
        tvCom.setLayoutParams(new LinearLayout.LayoutParams(matchParent, matchParent));
        tvCom.setPadding(20, 0, 0, 0);
        tvCom.setText(getString(R.string.common) + "\n");
        tvCom.setTextSize(16);

        String[] plain = getResources().getStringArray(R.array.plainjp);
        String[] morse = getResources().getStringArray(R.array.morsejp);
//        HashMap codeTable = new HashMap<String, String>();
//        Map.Entry entry;

//        for(i=0; i < plain.length; i++)
//            codeTable.put(plain[i], morse[i]);
//        Iterator<Map.Entry<String, String>> itr = codeTable.entrySet().iterator();

//        while(itr.hasNext()){
//            entry = itr.next();
//            tv.append(entry.getKey() + ": " + entry.getValue() + "\n");
//        }

        for(int i=0; i < morse.length; i++)
            tvJp.append(plain[i] + "： " + morse[i] + "\n");

        plain = getResources().getStringArray(R.array.plaineng);
        morse = getResources().getStringArray(R.array.morseeng);
        for(int i=0; i < morse.length; i++)
            tvEng.append(plain[i] + "： " + morse[i] + "\n");


        plain = getResources().getStringArray(R.array.plaincom);
        morse = getResources().getStringArray(R.array.morsecom);
        for(int i=0; i < morse.length; i++)
            tvCom.append(plain[i] + "： " + morse[i] + "\n");

        llCont.addView(tvJp);
        llCont.addView(tvEng);

        llHead.addView(tvHead);
        llHead.addView(llCont);
        llHead.addView(tvCom);

        sv.addView(llHead);
        setContentView(sv);
    }
}