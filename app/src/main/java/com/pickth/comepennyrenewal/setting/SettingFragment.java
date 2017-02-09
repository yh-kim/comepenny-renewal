package com.pickth.comepennyrenewal.setting;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.pickth.comepennyrenewal.R;

/**
 * Created by Kim on 2017-02-08.
 */

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setting);

        CheckBoxPreference checkNotice = (CheckBoxPreference) findPreference("useNotice");
        CheckBoxPreference checkPush = (CheckBoxPreference) findPreference("usePush");


        checkPush.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        checkPush.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return false;
            }
        });
    }
}
