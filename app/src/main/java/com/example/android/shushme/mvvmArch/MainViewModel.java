package com.example.android.shushme.mvvmArch;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.shushme.room.AppDatabase;
import com.example.android.shushme.room.ListItemsEntity;

import java.util.List;

    public class MainViewModel extends AndroidViewModel {
        // Constant for logging
        private static final String TAG = MainViewModel.class.getSimpleName();

        private LiveData<List<ListItemsEntity>> tasks;
        private final TasksRepository tasksRepository;
        public MainViewModel(Application application) {
            super(application);
            AppDatabase database = AppDatabase.getInstance(this.getApplication());
            Log.d(TAG, "Actively retrieving the tasks from the DataBase");
            //tasks = database.taskDao().loadAllTasks();
            tasksRepository = new TasksRepository(database);
            tasks = tasksRepository.getloadAllTasks();
        }

        public LiveData<List<ListItemsEntity>> getTasks() {
            return tasks;
        }

        public void deleteTask(ListItemsEntity taskEntry) {
            tasksRepository.deleteTasks(taskEntry);
        }

        public void insertTasks(ListItemsEntity taskEntry) {
            tasksRepository.insertTasks(taskEntry);
        }

        public void fetchPlacesbyId(String placeId) {
            tasksRepository.fetchPlacesbyId(placeId);
        }
    }
