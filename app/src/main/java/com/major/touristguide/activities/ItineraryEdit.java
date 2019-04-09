package com.major.touristguide.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.major.touristguide.EditBaseAdapter;
import com.major.touristguide.models.EditItem;
import com.major.touristguide.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ItineraryEdit extends AppCompatActivity {

    Firebase reference1,reference2;
    ListView listView;
    List<EditItem> rowItems;

    static String itineraryId;
    static String cityId;
    ArrayList<String> placeIdsPerItinerary;
    ArrayList<String> placeNamesPerItinerary;
    ArrayList<String> openPerItinerary;
    ArrayList<String> closePerItinerary;
    Button cancelbtn, savebtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itineraryedit);

        savebtn = (Button) findViewById(R.id.save);
        cancelbtn = (Button) findViewById(R.id.cancel);

        setTitle("Edit");

        Bundle extras = getIntent().getExtras();
        itineraryId = (String)getIntent().getSerializableExtra("itineraryId");
        cityId = (String)getIntent().getSerializableExtra("cityId");
        System.out.println("cityId"+cityId);

//        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");
//        listView = (ListView) findViewById(R.id.listViewEdit);
//
//        reference1.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                System.out.println("snapshot"+dataSnapshot.getValue());
//                Map map = dataSnapshot.getValue(Map.class);
//                final List<String> placeIds = (ArrayList<String>)map.get("placesId");
//
//                reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");
//
//                System.out.println("places"+placeIds);
//                for(int i=0;i<placeIds.size();i++) {
//                    final int j=i;
//
//                    reference2.child(placeIds.get(j)).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Map map1 = dataSnapshot.getValue(Map.class);
//                            String placeName = map1.get("placeName").toString();
//                            String openTime = map1.get("openTime").toString();
//                            String closeTime = map1.get("closeTime").toString();
//                            rowItems.add(new RowItem("", placeName, "Place Opens at "+openTime+"\nCloses at "+closeTime));
//                            CustomBaseAdapter adapter = new CustomBaseAdapter(ItineraryEdit.this, rowItems);
//                            listView.setAdapter(adapter);
//
//                            //populate place names list
//                            if(placeNamesPerItinerary.isEmpty()) {
//                                ArrayList<String> placeNameList = new ArrayList<>();
//                                placeNameList.add(placeName);
//                                placeNamesPerItinerary.put(itineraryId, placeNameList);
//                            }
//                            else {
//                                ArrayList<String> placeNameList = placeNamesPerItinerary.get(itineraryId);
//                                placeNameList.add(placeName);
//                                placeNamesPerItinerary.put(itineraryId, placeNameList);
//                            }
//                        }
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });
//                }
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        final Firebase firebase = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");
        final Firebase firebase1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

        placeIdsPerItinerary = (ArrayList<String>)getIntent().getStringArrayListExtra("placeIds");
        placeNamesPerItinerary = (ArrayList<String>)getIntent().getStringArrayListExtra("placeNames");
        openPerItinerary = (ArrayList<String>)getIntent().getStringArrayListExtra("open");
        closePerItinerary = (ArrayList<String>)getIntent().getStringArrayListExtra("close");


        firebase1.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listView = (ListView) findViewById(R.id.listViewEdit);
                rowItems = new ArrayList();
                System.out.println("keys " + dataSnapshot.getChildrenCount());
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = ds.getValue(Map.class);
                    boolean flag = false;
                    for(int i=0; i<placeNamesPerItinerary.size(); i++) {

                        if (ds.getKey().equals(placeIdsPerItinerary.get(i))) {

                            String placeId = placeIdsPerItinerary.get(i);
                            String place = placeNamesPerItinerary.get(i);
                            String open = openPerItinerary.get(i);
                            String close = closePerItinerary.get(i);
                            rowItems.add(new EditItem(placeId, place, "Place Opens at " +
                                    open + "\nCloses at " + close, true));
                            flag = true;

                        }
                    }

                         if(!flag&&map.get("cityId").equals(cityId)){

                             String placeId = ds.getKey();
                             String place = map.get("placeName").toString();
                             String open = map.get("Open Time").toString();
                             String close = map.get("Close Time").toString();
                             rowItems.add(new EditItem(placeId, place, "Place Opens at " +
                                     open + "\nCloses at " + close, false));

                        }
                }

                EditBaseAdapter adapter = new EditBaseAdapter(ItineraryEdit.this, rowItems);
                listView.setAdapter(adapter);

                savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                ItineraryEdit.this);

                        // set title
                        alertDialogBuilder.setTitle("Warning");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Changes would be permanent")
                                .setCancelable(false)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        List<String> values = new CopyOnWriteArrayList<>();
                                        for (int i = 0; i < EditBaseAdapter.editItems.size(); i++){
                                            if(EditBaseAdapter.editItems.get(i).isSelected()) {
                                                System.out.println(EditBaseAdapter.editItems.get(i).getId() + EditBaseAdapter.editItems.get(i).getTitle());
                                                //firebase.child(itineraryId).child("placesId").child(Integer.toString(i)).removeValue();
                                                values.add(EditBaseAdapter.editItems.get(i).getId());

                                            }
                                        }

                                        firebase.child(itineraryId).child("placesId").removeValue();
                                        firebase.child(itineraryId).child("placesId").setValue(values);

                                        ItineraryEdit.this.finish();
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();


                    }
                });

                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ItineraryEdit.this.finish();
                    }
                });
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.edit_item, places);


    }
}
