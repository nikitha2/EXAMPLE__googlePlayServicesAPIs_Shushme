package com.example.android.shushme.mvvmArch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android.shushme.R;
import com.example.android.shushme.room.AppDatabase;
import com.example.android.shushme.room.ListItemsEntity;
import com.example.android.shushme.room.TaskDao;
import java.util.List;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import com.example.android.shushme.Retrofit.*;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.android.shushme.MainActivity.API_KEY;

public class TasksRepository {
    static String fields = "name,formatted_address";
    private static final String LOG_TAG = TasksRepository.class.getSimpleName();
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

    public void fetchPlacesbyId(String placeId,MainViewModel viewModel) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        GetPlacesService service = retrofit.create(GetPlacesService.class);

        Call<ListItemsEntityResult> call = service.getListItemsEntityById(placeId, fields, API_KEY);

        final MutableLiveData<ListItemsEntity> listItemsEntity = new MutableLiveData<>();
        call.enqueue(new Callback<ListItemsEntityResult>() {
            @Override
            public void onResponse(Call<ListItemsEntityResult> call, Response<ListItemsEntityResult> response) {
                ListItemsEntityResult body = response.body();
                List<ListItemsEntity> result = body.getResults();
                //listItemsEntity.setValue(body.getResults());
                String message = response.message();
                String res = response.toString();
                
                viewModel.insertTasks(new ListItemsEntity(placeId,result.get(0).getPlaceName(),result.get(0).getPlaceAddress()));
            }

            @Override
            public void onFailure(Call<ListItemsEntityResult> call, Throwable t) {
                System.out.println("onFailure");
                listItemsEntity.setValue(null);
            }
        });

    }
}
