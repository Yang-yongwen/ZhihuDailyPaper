package com.yangyongwen.zhihudailypaper.setting;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import com.yangyongwen.zhihudailypaper.R;

/**
 * Created by yangyongwen on 16/3/30.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_setting);
    }




}
