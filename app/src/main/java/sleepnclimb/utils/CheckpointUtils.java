package sleepnclimb.utils;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import sleepnclimb.R;
import sleepnclimb.daos.CheckpointDao;
import sleepnclimb.databases.AppDatabase;
import sleepnclimb.models.Checkpoint;

public class CheckpointUtils {
    public static void save(Context context, Checkpoint checkpoint) {
        AppDatabase db = AppDatabase.getInstance(context);
        CheckpointDao checkpointDao = db.checkpointDao();
        checkpoint.altitude = 0;
        checkpoint.oxygen = -1;
        if (checkpoint.snoozeCount == 0) {
            if (Math.random() < 0.5) {
                // Oxygen from a fellow climber
                checkpoint.oxygen += 2 + (int) Math.ceil(Math.random() * 2);
            }
            checkpoint.altitude += 200 + (int) Math.ceil(Math.random() * 200);
        } else if (checkpoint.snoozeCount == 1) {
            if (Math.random() < 0.5) {
                // Oxygen from a fellow climber
                checkpoint.oxygen += 1;
            }
            checkpoint.altitude += 50 + (int) Math.ceil(Math.random() * 100);
        } else {
            checkpoint.altitude += 50 + (int) Math.ceil(Math.random() * 10);
        }
        String text = "";
        if (checkpoint.oxygen > 0) {
            text = "You climbed "
                + checkpoint.altitude
                + " units and gained "
                + checkpoint.oxygen
                + " units of oxygen!";
        } else if (checkpoint.oxygen < 0) {
            text = "You climbed "
                + checkpoint.altitude
                + " units and lost "
                + checkpoint.oxygen
                + " units of oxygen!";
        } else {
            text = "You climbed "
                + checkpoint.altitude
                + " units and gained no oxygen!";
        }
        checkpointDao.insertAll(checkpoint);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, "status")
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("Journey status")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

    public static void reset(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        CheckpointDao checkpointDao = db.checkpointDao();
        checkpointDao.deleteAll();
    }
}
