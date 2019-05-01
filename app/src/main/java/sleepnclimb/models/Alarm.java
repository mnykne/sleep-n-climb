package sleepnclimb.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    public long cid;

    @ColumnInfo(name = "alarmTime")
    public long alarmTime;

    @ColumnInfo(name = "snoozeInterval")
    public int snoozeInterval;

    @ColumnInfo(name = "dismissMethod")
    public String dismissMethod;

    @ColumnInfo(name = "stepCount")
    public int stepCount;

    @ColumnInfo(name = "objectCategories")
    public String objectCategories;

    @ColumnInfo(name = "alarmTone")
    public String alarmTone;

    @ColumnInfo(name = "alarmVolume")
    public int alarmVolume;
}
