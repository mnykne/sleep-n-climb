package snoozebattle.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import snoozebattle.R;

public class AlarmSetupFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.prefs_alarm_setup);
    }
}
