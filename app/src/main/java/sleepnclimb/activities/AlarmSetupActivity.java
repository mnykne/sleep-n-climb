package sleepnclimb.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import sleepnclimb.R;
import sleepnclimb.daos.AlarmDao;
import sleepnclimb.databases.AppDatabase;
import sleepnclimb.fragments.AlarmSetupSubFragment;
import sleepnclimb.models.Alarm;
import sleepnclimb.utils.AlarmUtils;

public class AlarmSetupActivity extends AppCompatActivity
        implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    Button mTestButton;
    Button mCancelButton;
    Button mSaveButton;
    Alarm mAlarm;
    boolean mExisting;
    AlarmManager mAlarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setup);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long cid = getIntent().getLongExtra("cid", 0);
        mAlarm = null;
        mExisting = false;
        if (cid != 0) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            AlarmDao alarmDao = db.alarmDao();
            mAlarm = alarmDao.findById(cid);
            if (mAlarm != null) {
                mExisting = false;
            }
        }
        if (mAlarm == null) {
            mAlarm = new Alarm();
            Uri defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    this, RingtoneManager.TYPE_ALARM);
            mAlarm.alarmTone = defaultRintoneUri.toString();
            mAlarm.alarmVolume = 75;
            mAlarm.snoozeInterval = 300000;
            mAlarm.dismissMethod = "default";
            mAlarm.objectCategories = "duck";
            mExisting = false;
        } else {
            mExisting = true;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = new AlarmSetupSubFragment();
            fragmentTransaction.add(android.R.id.content, fragment);
            fragmentTransaction.commit();
        }
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        LayoutInflater li = LayoutInflater.from(this);
        View ab_custom = li.inflate(R.layout.actionbar_alarm_setup, null);
        getSupportActionBar().setCustomView(ab_custom);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        mTestButton = ab_custom.findViewById(R.id.alarm_setup_test_btn);
        mTestButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               testAlarm();
           }
        });
        mCancelButton = ab_custom.findViewById(R.id.alarm_setup_cancel_btn);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSaveButton = ab_custom.findViewById(R.id.alarm_setup_save_btn);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAlarm();
                onBackPressed();
            }
        });

    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    private void testAlarm() {
        Alarm alarm = getAlarm();
        Alarm copy = new Alarm();
        copy.cid = 1;
        copy.dismissMethod = alarm.dismissMethod;
        copy.objectCategories= alarm.objectCategories;
        copy.snoozeInterval = alarm.snoozeInterval;
        copy.alarmTime = alarm.alarmTime;
        copy.alarmVolume = alarm.alarmVolume;
        copy.alarmTone = alarm.alarmTone;
        AlarmUtils.save(getApplicationContext(), copy);
        Intent intent = new Intent(getApplicationContext(), WakeUpActivity.class);
        intent.putExtra("cid", copy.cid);
        startActivity(intent);
    }

    private void saveAlarm() {
        mAlarm = AlarmUtils.save(getApplicationContext(), getAlarm());
        AlarmUtils.schedule(getApplicationContext(), getAlarm());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 0) {
            super.onBackPressed();
        } else {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(
            PreferenceFragmentCompat preferenceFragmentCompat,
            PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AlarmSetupSubFragment fragment = new AlarmSetupSubFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.replace(android.R.id.content, fragment, preferenceScreen.getKey());
        ft.addToBackStack(null);
        ft.commit();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        return true;
    }
}
