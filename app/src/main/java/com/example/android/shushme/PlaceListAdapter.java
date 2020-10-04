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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    private Context mContext;
    ListItemOnClickListener mListItemOnClickListener;

    public PlaceListAdapter(Context mainActivity, ListItemOnClickListener mListItemOnClickListener) {
        this.mContext=mainActivity;
        this.mListItemOnClickListener=mListItemOnClickListener;
    }
    interface ListItemOnClickListener{
        void OnItemClick(int position);
    }
    public PlaceListAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {

    }


    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * PlaceViewHolder class for the recycler view item
     */
//    class PlaceViewHolder extends RecyclerView.ViewHolder {
//
//        TextView nameTextView;
//        TextView addressTextView;
//
//        public PlaceViewHolder(View itemView) {
//            super(itemView);
//            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
//            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
//            itemView.setOnClickListener(this);
//        }
//
//    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View itemView;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListItemOnClickListener.OnItemClick(getAdapterPosition());
        }
    }
}