package com.lune.encipher;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, MyPreference.newInstance("preference_root"))
                .commit();
    }
    @Override
    public void onDestroy(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onDestroy();
    }
}
