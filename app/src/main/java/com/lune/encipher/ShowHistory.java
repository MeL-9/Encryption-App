package com.lune.encipher;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ScrollView;

import java.util.ArrayList;

public class ShowHistory extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        ArrayList<String> strHistory;
        strHistory = intent.getStringArrayListExtra("com.lune.encipher.arrayHistory");

        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ScrollView.LayoutParams(matchParent, wrapContent));

        TextView tv = new TextView(this);
        tv.setLayoutParams(new ScrollView.LayoutParams(matchParent, wrapContent));
        int i = 0;

        for(String str: strHistory){
            i++;
            tv.append(str + "\n");
        }
        sv.addView(tv);
        setContentView(sv);
    }
}
