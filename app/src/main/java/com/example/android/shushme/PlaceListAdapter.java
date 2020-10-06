package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.shushme.room.ListItemsEntity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter {
    private static final String TAG = PlaceListAdapter.class.getSimpleName();
    List<ListItemsEntity> allTaskEntries;
    private Context mContext;
    ListItemClickListener mClickListener=null;

    public PlaceListAdapter(Context mainActivity, List<ListItemsEntity> taskEntries,ListItemClickListener mClickListener) {
        this.mContext=mainActivity;
        this.mClickListener=mClickListener;
        this.allTaskEntries=taskEntries;
    }


    public void setData( List<ListItemsEntity> data) {
        allTaskEntries.clear();
        allTaskEntries.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_place_card, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        View currentView = holder.itemView;
        ListItemsEntity currentItemAtPos = allTaskEntries.get(position);

        ImageView imageView=currentView.findViewById(R.id.image);
        TextView name_text_view=currentView.findViewById(R.id.name_text_view);
        TextView address_text_view=currentView.findViewById(R.id.address_text_view);

        name_text_view.setText(currentItemAtPos.getPlaceName());
        address_text_view.setText(currentItemAtPos.getPlaceName());

        Log.i(TAG, "1. Place ID: " + currentItemAtPos.getPlaceID());
        Log.i(TAG, "1. Place found: " + name_text_view.getText());
        Log.i(TAG, "1. Address found: " + address_text_view.getText());
    }

    @Override
    public int getItemCount() {
        if(allTaskEntries==null)
            return 0;
        return allTaskEntries.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        View v;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.v=itemView;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos=getAdapterPosition();
            mClickListener.onListItemClick(pos);
        }
    }
    interface ListItemClickListener{
        void onListItemClick(int position);
    }
}
