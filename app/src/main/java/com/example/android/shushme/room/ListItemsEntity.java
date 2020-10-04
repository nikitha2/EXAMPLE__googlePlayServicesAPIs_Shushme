package com.example.android.shushme.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "places")
public class ListItemsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String placeID;

    public ListItemsEntity(String placeID) {
        this.placeID = placeID;
    }

    @Ignore
    public ListItemsEntity(int id, String placeID) {
        this.id = id;
        this.placeID = placeID;
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

}
