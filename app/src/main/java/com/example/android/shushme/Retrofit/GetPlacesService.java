package com.example.android.shushme.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetPlacesService {

//    https://maps.googleapis.com/maps/api/place/details/json?place_id=849VCWC8+QCMW&fields=name,rating,formatted_phone_number&key=YOUR_API_KEY
   // @Headers({"cache-control:public, max-age=21600","content-type:content-type"})
    //@Query("api_key","841d0fa80309aa3e96d864930905571d")
    @GET("/maps/api/place/details/json?")
    Call<ListItemsEntityResult> getListItemsEntityById(@Query("place_id") String place_id,  @Query("fields") String fields, @Query("key") String api_key);


}
