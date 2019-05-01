package sleepnclimb.fragments;

import android.app.TimePickerDialog;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sleepnclimb.R;
import sleepnclimb.activities.AlarmSetupActivity;
import sleepnclimb.models.Alarm;

public class AlarmSetupSubFragment extends PreferenceFragmentCompat {

    Alarm getAlarm() {
        return ((AlarmSetupActivity) getActivity()).getAlarm();
    }

    void initSound() {
        final ListPreference prefAlarmTone =
                (ListPreference) findPreference("pref_alarm_tone");
        final SeekBarPreference prefAlarmVolume =
                (SeekBarPreference) findPreference("pref_alarm_volume");
        prefAlarmTone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String tone = (String) o;
                setAlarmTone(prefAlarmTone, tone);
                return false;
            }
        });
        prefAlarmVolume.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                int volume = (int) o;
                setAlarmVolume(prefAlarmVolume, volume);
                return false;
            }
        });
        RingtoneManager manager = new RingtoneManager(getContext());
        List<String> ringToneTitles = new ArrayList<>();
        List<String> ringToneUris = new ArrayList<>();
        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                    + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            ringToneTitles.add(title);
            ringToneUris.add(uri);
        }
        prefAlarmTone.setEntries(ringToneTitles.toArray(new CharSequence[0]));
        prefAlarmTone.setEntryValues(ringToneUris.toArray(new CharSequence[0]));
        setAlarmTone(prefAlarmTone, getAlarm().alarmTone);
        setAlarmVolume(prefAlarmVolume, getAlarm().alarmVolume);
    }

    void setAlarmTone(ListPreference pref, String tone) {
        getAlarm().alarmTone = tone;
        pref.setValue(tone);
        pref.setSummary(pref.getEntry());
    }

    void setAlarmVolume(SeekBarPreference pref, int volume) {
        getAlarm().alarmVolume = volume;
        pref.setValue(volume);
    }

    void setSnoozeInterval(ListPreference pref, String interval) {
        getAlarm().snoozeInterval = Integer.valueOf(interval);
        pref.setValue(interval);
        pref.setSummary(pref.getEntry());
    }

    void setSnoozeInterval(ListPreference pref, int interval) {
        setSnoozeInterval(pref, String.valueOf(interval));
    }

    void setTime(Preference pref, int hour, int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        getAlarm().alarmTime = alarmTime.getTimeInMillis();
        pref.setSummary(sdf.format(alarmTime.getTimeInMillis()));
    }

    void setTime(Preference pref, long time) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.setTimeInMillis(time);
        int hour = alarmTime.get(Calendar.HOUR_OF_DAY);
        int minute = alarmTime.get(Calendar.MINUTE);
        setTime(pref, hour, minute);
    }

    void setDismissMethod(ListPreference pref, Preference group, String method) {
        getAlarm().dismissMethod = method;
        if ("photo".equals(method)) {
            getPreferenceScreen().addPreference(group);
        } else {
            getPreferenceScreen().removePreference(group);
        }
        pref.setValue(method);
        pref.setSummary(pref.getEntry());
    }

    void setDismissObjectCategory(ListPreference pref, String category) {
        getAlarm().objectCategories = category;
        pref.setValue(category);
        pref.setSummary(pref.getEntry());
    }

    private void initDismiss() {
        final ListPreference prefDismissMethod =
                (ListPreference) findPreference("pref_dismiss_method");
        final ListPreference prefDismissMethodPhotoCategories =
                (ListPreference) findPreference("pref_dismiss_method_photo_categories");
        getPreferenceScreen().removePreference(prefDismissMethodPhotoCategories);
        prefDismissMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String dismissMethod = (String) o;
                setDismissMethod(
                    prefDismissMethod,
                    prefDismissMethodPhotoCategories,
                    dismissMethod
                );
                return false;
            }
        });
        prefDismissMethodPhotoCategories.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String category = (String) o;
                setDismissObjectCategory(
                    prefDismissMethodPhotoCategories,
                    category
                );
                return false;
            }
        });
        setDismissMethod(
            prefDismissMethod,
            prefDismissMethodPhotoCategories,
            getAlarm().dismissMethod
        );
        setDismissObjectCategory(
            prefDismissMethodPhotoCategories,
            getAlarm().objectCategories
        );
    }

    private void initDefault() {
        final Preference prefTime =
            findPreference("pref_time");
        final ListPreference prefSnoozeInterval =
            (ListPreference) findPreference("pref_snooze_interval");
        prefTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        setTime(prefTime, selectedHour, selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select time");
                mTimePicker.show();
                return true;
            }
        });
        prefSnoozeInterval.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                setSnoozeInterval(prefSnoozeInterval, (String) o);
                return false;
            }
        });
        setTime(prefTime, getAlarm().alarmTime);
        setSnoozeInterval(prefSnoozeInterval, getAlarm().snoozeInterval);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_alarm_setup, rootKey);
        if ("pref_sound".equals(rootKey)) {
            initSound();
        } else if ("pref_dismiss".equals(rootKey)) {
            initDismiss();
        } else {
            initDefault();
        }
    }
}
