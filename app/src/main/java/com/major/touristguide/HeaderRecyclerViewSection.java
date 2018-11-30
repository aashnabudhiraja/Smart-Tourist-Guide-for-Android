package com.major.touristguide;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;
    private List<ItemObject> list;
    private String days;
    private List<String> itinerarys = new ArrayList<>();
    private Context mContext;
    Firebase reference1;
    public HeaderRecyclerViewSection(String title, List<ItemObject> list) {
        super(R.layout.header_layout, R.layout.item_layout);
        this.title = title;
        this.list = list;
    }
    @Override
    public int getContentItemsTotal() {
        return list.size();
    }
    public List<ItemObject> getContent() {
        return list;
    }
    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder iHolder = (ItemViewHolder)holder;
        iHolder.itemContent.setText(list.get(position).getContents());

        ((ItemViewHolder) holder).itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Firebase.setAndroidContext(view.getContext());
                if(title.equals("Create New Trip")){
                    Intent intent = new Intent(view.getContext(), Home.class);
                    view.getContext().startActivity(intent);
                }

                else {
                    reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");

                    reference1.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            System.out.println("keys " + dataSnapshot.getChildrenCount());
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Map map = ds.getValue(Map.class);
                                //  System.out.println("ids " + map.get("tripId").toString());
                                if (map.get("tripId").toString().equals(list.get(position).getIds())) {
                                    itinerarys.add(ds.getKey());

                                    System.out.println("itinerary " + map);
                                }
                            }

                            Intent i = new Intent(view.getContext(), Itinerary.class);
                            i.putExtra("days", list.get(position).getDays());
                            System.out.println("array value" + itinerarys);
                            i.putExtra("itineraryIds", (Serializable) itinerarys);
                            view.getContext().startActivity(i);

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    Log.d(TAG, "onClick: clicked on: " + list.get(position).getIds() + title);
                }

                //Toast.makeText(mContext,list.get(position).getContents(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder)holder;
        hHolder.headerTitle.setText(title);
    }
}
