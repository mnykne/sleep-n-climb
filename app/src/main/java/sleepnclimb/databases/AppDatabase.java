package sleepnclimb.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import sleepnclimb.daos.AlarmDao;
import sleepnclimb.daos.CheckpointDao;
import sleepnclimb.models.Alarm;
import sleepnclimb.models.Checkpoint;
import sleepnclimb.utils.AlarmUtils;
import sleepnclimb.utils.CheckpointUtils;

@Database(entities = {Alarm.class, Checkpoint.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    public abstract AlarmDao alarmDao();

    public abstract CheckpointDao checkpointDao();

    public synchronized static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(context,
                AppDatabase.class, "db").allowMainThreadQueries().build();
            populateDefaults(context);
        }
        return sInstance;
    }

    public static void populateDefaults(Context context) {
        if (AlarmUtils.find(context, 1) == null) {
            Alarm alarm = new Alarm();
            alarm.cid = 1;
            AlarmUtils.save(context, alarm);
        }
    }

    public static void populateDemo(Context context) {
        for (int i = 0; i < 20; i++) {
            Checkpoint checkpoint = new Checkpoint();
            checkpoint.snoozeCount = (int) (Math.random() * Math.max(15 - i, 0));
            CheckpointUtils.save(context, checkpoint);
        }
    }
}
