package snoozebattle.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import snoozebattle.R;

public class AlarmSetupSubFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_alarm_setup, rootKey);
    }
}
