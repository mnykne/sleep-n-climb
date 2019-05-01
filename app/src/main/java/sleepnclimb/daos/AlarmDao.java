package sleepnclimb.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import sleepnclimb.models.Alarm;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM Alarm")
    LiveData<List<Alarm>> getAll();

    @Query("SELECT * FROM Alarm WHERE cid IN (:challengeIds)")
    List<Alarm> findByIds(long[] challengeIds);

    @Query("SELECT * FROM Alarm WHERE cid IS :id LIMIT 1")
    Alarm findById(long id);

    @Query("SELECT * FROM Alarm ORDER BY cid DESC LIMIT 1")
    Alarm findLast();

    @Update
    void update(Alarm alarm);

    @Insert
    long[] insertAll(Alarm... alarms);

    @Delete
    void delete(Alarm alarm);

    @Query("DELETE FROM Alarm")
    void deleteAll();}
