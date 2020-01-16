package com.lune.encipher;

import android.os.Bundle;
import android.app.Activity;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.RadioGroup;
import android.widget.RadioButton;

//import java.util.HashMap;
//import java.util.Map;
//import java.util.Iterator;

public class ShowMorseCode extends Activity{
    private ScrollView sv;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;

        sv = new ScrollView(this);
        sv.setLayoutParams(new ScrollView.LayoutParams(matchParent, matchParent));

        tv = new TextView(this);
        tv.setLayoutParams(new ScrollView.LayoutParams(matchParent, wrapContent));

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
        tv.setText("モールス符号対応表\n");
        for(int i=0; i < morse.length; i++)
            tv.append(plain[i] + ": " + morse[i] + "\n");

        plain = getResources().getStringArray(R.array.plaincom);
        morse = getResources().getStringArray(R.array.morsecom);
        for(int i=0; i < morse.length; i++)
            tv.append(plain[i] + ": " + morse[i] + "\n");

        sv.addView(tv);
        setContentView(sv);
    }
}