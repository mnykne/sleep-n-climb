package sleepnclimb.daos;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import sleepnclimb.models.Checkpoint;

@Dao
public interface CheckpointDao {
    @Query("SELECT * FROM Checkpoint")
    LiveData<List<Checkpoint>> getAll();

    @Query("SELECT * FROM Checkpoint WHERE pid IN (:ids)")
    List<Checkpoint> findByIds(long[] ids);

    @Query("SELECT * FROM Checkpoint WHERE pid IS :id LIMIT 1")
    Checkpoint findById(long id);

    @Query("SELECT * FROM Checkpoint ORDER BY pid DESC LIMIT 1")
    Checkpoint findLast();

    @Update
    void update(Checkpoint checkpoint);

    @Insert
    long[] insertAll(Checkpoint... checkpoints);

    @Delete
    void delete(Checkpoint checkpoint);

    @Query("DELETE FROM Checkpoint")
    void deleteAll();
}
