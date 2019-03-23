package com.major.touristguide.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceRating extends AppCompatActivity {

    Firebase reference1, reference2, reference3;
    private String description;
    private String avgRating;
    private TextView descriptionTxt;
    private TextView avgRatingTxt;
    private TextView doRatingTxt;
    private Button yesButton, noButton;
    private LinearLayout doRatingLayout;
    private RatingBar ratingBar;
    private String userRating;
    private Boolean flag = true;
    Map<String, String> visitedPlaces = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_rating);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        final String placeId =  extras.getString("placeId");
        String placeName = extras.getString("placeName");
        setTitle(placeName);
        Firebase.setAndroidContext(this);

         descriptionTxt = (TextView)findViewById(R.id.description);
         avgRatingTxt = (TextView)findViewById(R.id.rating);
         doRatingTxt = (TextView) findViewById(R.id.doRating) ;
         yesButton = (Button)findViewById(R.id.yesButton);
         noButton = (Button)findViewById(R.id.noButton);
         doRatingLayout = (LinearLayout)findViewById(R.id.doRatingLayout);
         ratingBar = (RatingBar)findViewById(R.id.ratingBar);

         ratingBar.setVisibility(View.INVISIBLE);

         reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/user");

        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = ds.getValue(Map.class);
                    System.out.println("placesVisited"+map);

                    if(ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        if(map.containsKey("placesVisited")) {
                            visitedPlaces = (HashMap<String, String>) map.get("placesVisited");
                            System.out.println("Why Empty" + visitedPlaces);

                            if (visitedPlaces.containsKey(placeId)) {
                                userRating = visitedPlaces.get(placeId);
                                if (!userRating.equals("-1")) {
                                    doRatingLayout.setVisibility(View.INVISIBLE);
                                    doRatingTxt.setText("Your Rating: " + userRating);
                                    flag = false;
                                }
                            }
                        }
                    }


               }


               //flag = false;

                if (flag) {
                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("No Button pressed");
                            doRatingTxt.setVisibility(View.INVISIBLE);
                            doRatingLayout.setVisibility(View.INVISIBLE);
                        }
                    });

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doRatingLayout.setVisibility(View.INVISIBLE);
                            ratingBar.setVisibility(View.VISIBLE);
                            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                                    doRatingTxt.setVisibility(View.VISIBLE);
                                    doRatingTxt.setText("Your Rating: " + rating);
                                    visitedPlaces.put(placeId.toString(),Float.toString(rating));
                                    reference2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("placesVisited").setValue(visitedPlaces);

                                    reference3 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

                                    reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                                Map map = ds.getValue(Map.class);

                                                if(ds.getKey().equals(placeId)) {
                                                    String reviews = map.get("numberOfReviews").toString();
                                                    Float avgRating = Float.parseFloat(map.get("avgRating").toString());
                                                    Float newRating = (avgRating*(Integer.parseInt(reviews))+ rating)/((Integer.parseInt(reviews)+1));
                                                    newRating = Math.round(newRating*(float)100.0)/(float)100.0;
                                                    reference3.child(placeId).child("avgRating").setValue(Float.toString(newRating));
                                                    int newReviews = ((Integer.parseInt(reviews)+1));
                                                    reference3.child(placeId).child("numberOfReviews").setValue(Integer.toString(newReviews));
                                                    avgRatingTxt.setText("Rating:  "+Float.toString(newRating));

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                }
                            });

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Map map = ds.getValue(Map.class);

                    if(ds.getKey().equals(placeId)) {
                        description = map.get("description").toString();
                        avgRating = map.get("avgRating").toString();
                        descriptionTxt.append(description);
                        avgRatingTxt.append(avgRating);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }
}
