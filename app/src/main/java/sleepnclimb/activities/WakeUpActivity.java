package sleepnclimb.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sleepnclimb.R;

import sleepnclimb.fragments.AlarmPhotoDismissFragment;
import sleepnclimb.fragments.AlarmShakeDismissFragment;
import sleepnclimb.models.Alarm;
import sleepnclimb.models.Checkpoint;
import sleepnclimb.utils.AlarmUtils;
import sleepnclimb.utils.CheckpointUtils;

public class WakeUpActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private AlarmManager mAlarmManager;
    private PowerManager.WakeLock mWakeLock;
    private int mSnoozeCount;
    private Alarm mAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        getSupportActionBar().hide();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(
            PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "sleep_n_climb:wake_up.wakelock"
        );
        mWakeLock.acquire();
        long cid = getIntent().getLongExtra("cid", 0);
        mSnoozeCount = 0;
        mAlarm = AlarmUtils.find(this, cid);
        getWindow().addFlags(
              WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_FULLSCREEN
            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        TextView timeView = findViewById(R.id.alarm_time_2);
        TextView periodView = findViewById(R.id.alarm_period_2);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeView.setText(sdf.format(calendar.getTimeInMillis()));
        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == Calendar.AM) {
            periodView.setText("AM");
        } else if (am_pm == Calendar.PM) {
            periodView.setText("PM");
        }
        final Button snooze = findViewById(R.id.btn_snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAndSnooze();
            }
        });
        Button dismiss = findViewById(R.id.btn_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                if ("photo".equals(mAlarm.dismissMethod)) {
                    fragment = new AlarmPhotoDismissFragment();
                } else if ("shaking".equals(mAlarm.dismissMethod)) {
                    fragment = new AlarmShakeDismissFragment();
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(android.R.id.content, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    dismissAndFinalize();
                }
            }
        });
        startAlarmSound();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        dismissAndSnooze();
    }

    public void dismiss() {
        stopAlarmSound();
        finish();
    }

    public Alarm getAlarm() {
        return mAlarm;
    }

    public void dismissAndFinalize() {
        if (mAlarm.cid != 1) {
            saveCheckpoint();
        }
        dismiss();
    }

    public void dismissAndSnooze() {
        mSnoozeCount += 1;
        snooze();
        dismiss();
    }

    private void saveCheckpoint() {
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.snoozeCount = mSnoozeCount;
        CheckpointUtils.evaluate(checkpoint);
        CheckpointUtils.save(this, checkpoint);
        CheckpointUtils.notify(this, checkpoint);
    }

    public void snooze() {
        if (mAlarm.cid != 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 1);
            Intent myIntent = new Intent(getApplicationContext(), WakeUpActivity.class);
            myIntent.putExtra("cid", mAlarm.cid);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                (int) mAlarm.cid, myIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void startAlarmSound() {
        float volume = (float) mAlarm.alarmVolume / 100.0f;
        Uri ringtoneUri = Uri.parse(mAlarm.alarmTone);
        mMediaPlayer = new MediaPlayer();
        class Listener implements MediaPlayer.OnPreparedListener {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        }
        mMediaPlayer.setOnPreparedListener(new Listener());
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(volume, volume);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), ringtoneUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    private void stopAlarmSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
