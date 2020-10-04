package com.example.android.shushme.room;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM places")
    LiveData<List<ListItemsEntity>> loadAllTasks();

    @Query("SELECT * FROM places WHERE id=:id")
    LiveData<ListItemsEntity> loadTaskById(int id);

    @Insert
    void insertTask(ListItemsEntity listItemsEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ListItemsEntity taskEntry);

    @Delete
    void deleteTask(ListItemsEntity taskEntry);
}
