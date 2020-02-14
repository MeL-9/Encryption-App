package com.lune.encipher;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

public class MyPreference extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        setPreferencesFromResource(R.xml.preference, rootKey);
    }
    public static MyPreference newInstance(String rootKey){
        MyPreference fragment = new MyPreference();
        Bundle bundle = new Bundle();
        bundle.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootKey);
        fragment.setArguments(bundle);
        return fragment;
    }
}
