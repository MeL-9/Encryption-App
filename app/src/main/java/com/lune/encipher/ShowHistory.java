package com.lune.encipher;

import android.os.Bundle;
import android.app.Activity;

import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ScrollView;

public class ShowHistory extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new ScrollView.LayoutParams(matchParent, wrapContent));

        TextView tv = new TextView(this);
        tv.setLayoutParams(new ScrollView.LayoutParams(matchParent, wrapContent));
        int i = 0;
        char tmp;

        tv.setText("モールス符号対応表\n");
        for(String str: getResources().getStringArray(R.array.list_morse)){
            tmp = (char)('ぁ' + i);
            i++;

            tv.append(tmp + ": " + str + "\n");
        }
        sv.addView(tv);
        setContentView(sv);
    }
}
