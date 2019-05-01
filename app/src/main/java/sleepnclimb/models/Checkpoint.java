package sleepnclimb.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Checkpoint {
    @PrimaryKey(autoGenerate = true)
    public long pid;

    @ColumnInfo(name = "snoozes")
    public long snoozeCount;

    @ColumnInfo(name = "altitude")
    public int altitude;

    @ColumnInfo(name = "oxygen")
    public long oxygen;
}
