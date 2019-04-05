package com.major.touristguide.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.major.touristguide.CustomAdapter;
import com.major.touristguide.R;
import com.major.touristguide.models.PlaceItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PopularPlaces extends AppCompatActivity {

    private Spinner city;
    Firebase reference1, reference2;
    List<String> cityNames = new ArrayList<>();
    List<String> cityIds = new ArrayList<>();
    List<PlaceItem> placeItems;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_places);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Popular Places");

        city = (Spinner) findViewById(R.id.cities);
        listView = (ListView) findViewById(R.id.places);
        Firebase.setAndroidContext(this);


        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/city");

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = ds.getValue(Map.class);
                    cityNames.add(map.get("cityName").toString());
                    cityIds.add(ds.getKey());

                }

                ArrayAdapter<String> destAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, cityNames);
                destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                city.setAdapter(destAdapter);
                city.setSelection(0);


                city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        placeItems = new ArrayList<>();
                        //String Id = "";
                        String cityText = city.getSelectedItem().toString();
//                        for(int i=0;i<cityNames.size();i++){
//                            if(cityNames.get(i).equals(cityText)){
//                                 Id = cityIds.get(i);
//                            }
//                        }
                        final String cityId = cityIds.get(position);

                        System.out.println("City Selected"+ cityId);


                        reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");
                        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Map map = ds.getValue(Map.class);

                                    if(map.get("cityId").toString().equals(cityId)){
                                        String placeName = map.get("placeName").toString();
                                        String rating = map.get("avgRating").toString();
                                        String placeId = ds.getKey();
                                        placeItems.add(new PlaceItem(placeName, placeId, rating));
                                        System.out.println("places in city"+ placeName);
                                    }
                                }

                                Collections.sort(placeItems, Collections.reverseOrder());

                                CustomAdapter adapter = new CustomAdapter(getApplicationContext(), placeItems);
                                System.out.println("adapter value" + adapter);
                                listView.setAdapter(adapter);

                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        PlaceItem placeSelected = (PlaceItem)adapterView.getItemAtPosition(i);
                                        System.out.println("Place selected"+ placeSelected.getPlaceId());

                                        Intent intent = new Intent(PopularPlaces.this, PlaceRating.class);
                                        intent.putExtra("placeId",placeSelected.getPlaceId());
                                        intent.putExtra("placeName",placeSelected.getPlaceName());
                                        startActivity(intent);

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainHome.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

}
