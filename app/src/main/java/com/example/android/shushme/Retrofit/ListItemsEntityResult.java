package com.example.android.shushme.Retrofit;

import com.example.android.shushme.room.ListItemsEntity;
import java.util.List;

public class ListItemsEntityResult {

    List<ListItemsEntity> results;

    public ListItemsEntityResult(List<ListItemsEntity> results) {
        this.results = results;
    }

    public List<ListItemsEntity> getResults() {
        return results;
    }

    public void setResults(List<ListItemsEntity> results) {
        this.results = results;
    }


}
