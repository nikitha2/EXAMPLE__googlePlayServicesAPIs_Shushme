package com.example.android.shushme.room;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

public class DateConverter {

    @TypeConverter
    public static LatLng StringtoLatLng(String latLngString) {
        if(latLngString==null){
            return null;
        }
        String[] latlong =  latLngString.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        LatLng latLng = new LatLng(latitude,longitude);
        return latLng;
    }

    @TypeConverter
    public static String latLngtoString(LatLng latLng) {
        if(latLng==null)
            return null;
        String result = String.valueOf(latLng.latitude).concat(",").concat(String.valueOf(latLng.longitude));
        return result;
    }
}
