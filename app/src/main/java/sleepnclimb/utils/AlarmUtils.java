package sleepnclimb.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import sleepnclimb.activities.WakeUpActivity;
import sleepnclimb.daos.AlarmDao;
import sleepnclimb.databases.AppDatabase;
import sleepnclimb.models.Alarm;

public class AlarmUtils {
    public static Alarm save(Context context, Alarm alarm) {
        AppDatabase db = AppDatabase.getInstance(context);
        AlarmDao alarmDao = db.alarmDao();
        Alarm existing = alarmDao.findById(alarm.cid);
        if (existing != null) {
            alarmDao.update(alarm);
        } else {
            alarm.cid = alarmDao.insertAll(alarm)[0];
        }
        return alarm;
    }

    public static Alarm find(Context context, long cid) {
        AppDatabase db = AppDatabase.getInstance(context);
        AlarmDao alarmDao = db.alarmDao();
        return alarmDao.findById(cid);
    }

    public static boolean active(Context context, Alarm alarm) {
        return (PendingIntent.getActivity(context, (int) alarm.cid,
                new Intent(context, WakeUpActivity.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static void schedule(Context context, Alarm alarm) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar tmpCalendar = Calendar.getInstance();
        tmpCalendar.setTimeInMillis(alarm.alarmTime);
        Calendar updatedCalendar = Calendar.getInstance();
        long currentTime = updatedCalendar.getTimeInMillis();
        updatedCalendar.set(Calendar.HOUR_OF_DAY, tmpCalendar.get(Calendar.HOUR_OF_DAY));
        updatedCalendar.set(Calendar.MINUTE, tmpCalendar.get(Calendar.MINUTE));
        updatedCalendar.set(Calendar.SECOND, 0);
        if (currentTime > updatedCalendar.getTimeInMillis()) {
            updatedCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarm.alarmTime = updatedCalendar.getTimeInMillis();
        long timeDifference = updatedCalendar.getTimeInMillis() - currentTime;
        Intent intent = new Intent(context, WakeUpActivity.class);
        intent.putExtra("cid", alarm.cid);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            (int) alarm.cid,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        );
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarm.alarmTime,
            pendingIntent
        );
        Toast.makeText(
            context,
            "Alarm in "
                + timeDifference/1000/60/60
                + " hours ("
                + timeDifference/1000/60
                + " minutes)",
            Toast.LENGTH_LONG
        ).show();
    }

    public static void cancel(Context context, Alarm alarm) {
        AlarmManager alarmManager =
            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, WakeUpActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, (int) alarm.cid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
        alarmManager.cancel(pendingIntent);
    }
}
