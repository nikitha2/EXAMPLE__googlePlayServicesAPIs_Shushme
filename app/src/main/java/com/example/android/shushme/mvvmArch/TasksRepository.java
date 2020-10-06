package com.example.android.shushme.mvvmArch;

import androidx.lifecycle.LiveData;

import com.example.android.shushme.room.AppDatabase;
import com.example.android.shushme.room.ListItemsEntity;
import com.example.android.shushme.room.TaskDao;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TasksRepository {

    private static final String LOG_TAG = TasksRepository.class
            .getSimpleName();
    private LiveData<List<ListItemsEntity>> tasks;
    private TaskDao taskDao;
    AppDatabase database;
    ExecutorService executor;
    public TasksRepository(AppDatabase database) {
        this.database = database;
        executor = MyApplication.getInstance().executorService;

    }

    public LiveData<List<ListItemsEntity>> getloadAllTasks() {
        tasks = database.taskDao().loadAllTasks();
        return tasks;
    }

    public LiveData<ListItemsEntity> getloadTaskById(int taskId) {
        return database.taskDao().loadTaskById(taskId);
    }

    public void deleteTasks(ListItemsEntity taskEntry) {
        database.taskDao().deleteTask(taskEntry);
    }

    public void insertTasks(ListItemsEntity taskEntry) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.taskDao().insertTask(taskEntry);
            }
        });
    }

    public void updateTaskById(ListItemsEntity task) {
        database.taskDao().updateTask(task);
    }
}
