package com.example.android.shushme.mvvmArch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import Re
import com.example.android.shushme.R;
import com.example.android.shushme.room.AppDatabase;
import com.example.android.shushme.room.ListItemsEntity;
import com.example.android.shushme.room.TaskDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import retrofit2.Retrofit;

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

    public void fetchPlacesbyId(String placeId) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        GetMoviesByPopularityService service= retrofit.create(GetPlacesService.class);
        Call<MoviesByPopularityList> call = service.getListItemsEntityById(placeId, R.string.API_KEY,R.string.ReturnFieldsFromAPI);

        final MutableLiveData<List<MoviesDataEntity>> moviesByPopularity = new MutableLiveData<>();
        call.enqueue(new Callback<MoviesByPopularityList>() {
            @Override
            public void onResponse(Call<MoviesByPopularityList> call, Response<MoviesByPopularityList> response) {
                MoviesByPopularityList body = response.body();
                moviesByPopularity.setValue(body.getResults());
            }

            @Override
            public void onFailure(Call<MoviesByPopularityList> call, Throwable t) {
                System.out.println("onFailure");
                moviesByPopularity.setValue(null);
            }
        });
    }
}
