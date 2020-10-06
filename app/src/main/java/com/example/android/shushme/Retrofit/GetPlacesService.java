package com.example.android.shushme.Retrofit;

import com.example.android.shushme.room.ListItemsEntity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetPlacesService {

//    https://api.themoviedb.org/3/movie/popular?api_key=841d0fa80309aa3e96d864930905571d
   // @Headers({"cache-control:public, max-age=21600","content-type:content-type"})
    //@Query("api_key","841d0fa80309aa3e96d864930905571d")
    @GET("/maps/api/place/details/json?")
    Call<ListItemsEntity> getListItemsEntityById(@Query("place_id") String place_id, @Query("key") String api_key, @Query("fields") String fields);


}
