package com.major.touristguide.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.HeaderRecyclerViewSection;
import com.major.touristguide.R;
import com.major.touristguide.models.ItemObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Home extends AppCompatActivity {

    private static final String TAG = Home.class.getSimpleName();
    private RecyclerView sectionHeader;
    private SectionedRecyclerViewAdapter sectionAdapter;
    Firebase reference1;
    Firebase reference2, reference3;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private TextView heading;
    List<ItemObject> previousTrips = new ArrayList<>();
    List<ItemObject> currentTrips = new ArrayList<>();
    List<ItemObject> futureTrips = new ArrayList<>();
    List<String> placeIds = new ArrayList<>();
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dl = (DrawerLayout) findViewById(R.id.activity_navbar);

        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        t.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Home");
        nv = (NavigationView)findViewById(R.id.nv);
        View hView = nv.getHeaderView(0);
        heading =(TextView) hView.findViewById(R.id.heading);
        heading.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                System.out.println(" id   "+id);
                switch (id) {
                    case R.id.logout: {
                        SharedPreferences preferences =getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(Home.this, Login.class));
                        finish();
                        return true;
                    }

                    case R.id.home: {
                        startActivity(new Intent(Home.this,Home.class));
                        finish();
                        return true;
                    }
                    default:
                        return true;
                }


            }
        });
        Firebase.setAndroidContext(this);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/trips");

        //reference1.child("User UID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())

        System.out.println("reference1 "+reference1.child("User UID"));
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("keys " + dataSnapshot.getChildrenCount());
                for(final DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = ds.getValue(Map.class);
                    //System.out.println("map " + map);
                    if(map.get("User UID").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        String startD = map.get("startDate").toString();
                        String endD = map.get("endDate").toString();
                        Calendar fromDate = Calendar.getInstance();
                        Calendar toDate = Calendar.getInstance();
                        Calendar now = Calendar.getInstance();

                        try {
                            fromDate.setTime(dateFormatter.parse(startD));
                            toDate.setTime(dateFormatter.parse(endD));

                        } catch(ParseException e){
                            e.printStackTrace();
                        }
                        Date startDate = fromDate.getTime();
                        Date endDate = toDate.getTime();
                        Date now1 = now.getTime();

                        long diff = startDate.getTime() - now1.getTime();
                        long diff1 = endDate.getTime() - now1.getTime();


                        if(diff1<0 ){
                            previousTrips.add(new ItemObject("Previous Trip ", ds.getKey(),map.get("totalDays").toString(), map.get("startDate").toString()));


                            reference3 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");

                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    System.out.println("keys " + dataSnapshot.getChildrenCount());
                                    for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                        Map map = ds1.getValue(Map.class);
                                        System.out.println("ids " + map.get("tripId").toString());
                                        if (map.get("tripId").toString().equals(ds.getKey())) {
                                            placeIds = (ArrayList<String>)map.get("placesId");

                                            System.out.println("placeIds in trip"+placeIds);

                                            reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/user");
                                            reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds2 : dataSnapshot.getChildren()) {
                                                        if (ds2.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                            Map map = ds2.getValue(Map.class);
                                                            System.out.println("user data" + map);
                                                            if (map.containsKey("placesVisited")) {
                                                                HashMap<String, String> places = (HashMap<String, String>) map.get("placesVisited");
                                                                System.out.println("Visited Places" + places);

                                                                for (int k = 0; k < placeIds.size(); k++) {
                                                                    if (!places.containsKey(placeIds.get(k))) {
                                                                        System.out.println("Places is not visited" + placeIds.get(k));
                                                                        places.put(placeIds.get(k).toString(), "-1");
                                                                        //places.add();
                                                                    }
                                                                }

                                                                reference2.child(ds2.getKey()).child("placesVisited").setValue(places);
                                                            }

                                                            else {
                                                                System.out.println("Enter here for addition");
                                                                Map<String, String> map2 = new HashMap<>();
                                                                for(int k=0;k<placeIds.size();k++){
                                                                    map2.put(placeIds.get(k).toString(), "-1");

                                                                }
                                                                reference2.child(ds2.getKey()).child("placesVisited").setValue(map2);
                                                            }
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(FirebaseError firebaseError) {

                                                }
                                            });

                                            System.out.println("itinerary " + map);
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });


                        }
                        if(diff1>=0&&diff<=0){
                            currentTrips.add(new ItemObject("Current Trip \nDestination: "+map.get("destination")+"\nStart Date: "+map.get("startDate"), ds.getKey(), map.get("totalDays").toString(),  map.get("startDate").toString()));
                        }
                        if(diff1>0&&diff>0){
                            futureTrips.add(new ItemObject("Future Trip \nDestination: "+map.get("destination")+"\nStart Date: "+map.get("startDate"), ds.getKey(), map.get("totalDays").toString(),  map.get("startDate").toString()));
                        }
                       // if(map.get("startDate").toString())
                        System.out.println("map " + map);
                    }
                }


                sectionHeader = (RecyclerView)findViewById(R.id.add_header);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Home.this);
                sectionHeader.setLayoutManager(linearLayoutManager);
                sectionHeader.setHasFixedSize(true);
                HeaderRecyclerViewSection createSection = new HeaderRecyclerViewSection("Create New Trip",getDataSource());
                HeaderRecyclerViewSection firstSection = new HeaderRecyclerViewSection("Future Trips", futureTrips);
                HeaderRecyclerViewSection secondSection = new HeaderRecyclerViewSection("Current Trips", currentTrips);
                HeaderRecyclerViewSection thirdSection = new HeaderRecyclerViewSection("Trips History", previousTrips.subList(0, min(max(0,previousTrips.size()-1), 5)));
                HeaderRecyclerViewSection fourthSection = new HeaderRecyclerViewSection("Popular Places", getDataSourceForPopularPlaces());
                sectionAdapter = new SectionedRecyclerViewAdapter();
                sectionAdapter.addSection(createSection);
                sectionAdapter.addSection(firstSection);
                sectionAdapter.addSection(secondSection);
                sectionAdapter.addSection(fourthSection);
                sectionAdapter.addSection(thirdSection);
                sectionHeader.setAdapter(sectionAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError){

            }
        });

    }

    private List<ItemObject> getDataSource(){
        List<ItemObject> data = new ArrayList<ItemObject>();
        data.add(new ItemObject("Create New Trip", "Trip", "One","Date" ));
        return data;
    }

    private List<ItemObject> getDataSourceForPopularPlaces() {
        List<ItemObject> data = new ArrayList<ItemObject>();
        System.out.println("popular place inside");
        data.add(new ItemObject("Popular Places", "Trip", "One","Date" ));
        return data;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}
