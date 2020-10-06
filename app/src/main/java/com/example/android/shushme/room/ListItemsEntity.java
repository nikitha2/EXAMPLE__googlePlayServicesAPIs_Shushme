package com.example.android.shushme.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "places")
public class ListItemsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String placeID;
    private String placeName;
    private String placeAddress;
    public ListItemsEntity(String placeID) {
        this.placeID = placeID;
    }

    @Ignore
    public ListItemsEntity(int id, String placeID) {
        this.id = id;
        this.placeID = placeID;
    }

    @Ignore
    public ListItemsEntity(String placeID,String placeName,String placeAddress) {
        this.placeID = placeID;
        this.placeName= placeName;
        this.placeAddress=placeAddress;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }



}
