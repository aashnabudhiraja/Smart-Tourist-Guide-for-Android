package com.major.touristguide.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.stream.Collectors.toMap;

public class CreateTrip extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner destination;

    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    private Button confirm;
    Firebase reference1;
    Firebase reference2;
    Firebase reference3, reference4, reference5;
    Toolbar toolbar;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private TextView heading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        dl = (DrawerLayout) findViewById(R.id.activity_navbar);

        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        t.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Create Trip");
        nv = (NavigationView)findViewById(R.id.nv);
        View hView = nv.getHeaderView(0);
        heading =(TextView) hView.findViewById(R.id.heading);

        heading.setText("User: "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
        System.out.println("nv  "+nv);
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
                        startActivity(new Intent(CreateTrip.this, Login.class));
                        finish();
                        return true;
                    }

                    case R.id.home: {
                        startActivity(new Intent(CreateTrip.this,Home.class));
                        finish();
                        return true;
                    }
                    default:
                        return true;
                }


            }
        });

        final List<List<String>> placesList = new ArrayList<>();


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        destination = (Spinner) findViewById(R.id.destination);
        fromDateEtxt = (EditText) findViewById(R.id.etxt_fromdate);
        toDateEtxt = (EditText) findViewById(R.id.etxt_todate);
        confirm = (Button) findViewById(R.id.confirmButton);
        destination = (Spinner) findViewById(R.id.destination);

        fromDateEtxt.setInputType(InputType.TYPE_NULL);
        fromDateEtxt.requestFocus();
        toDateEtxt.setInputType(InputType.TYPE_NULL);

        destination.setOnItemSelectedListener(this);
        fromDateEtxt.setOnClickListener(this);
        toDateEtxt.setOnClickListener(this);
        Firebase.setAndroidContext(this);

        List<String> dest = new ArrayList<>();
        dest.add("Chandigarh");
        dest.add("Delhi");
        dest.add("Mumbai");
        dest.add("Bangalore");
        dest.add("Kolkata");

        ArrayAdapter<String> destAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dest);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        destination.setAdapter(destAdapter);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        toDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                final List<String> itineraryIds = new ArrayList<>();

                final String fromDateString = fromDateEtxt.getText().toString();
                System.out.println("real value"+fromDateString);
                final String toDateString = toDateEtxt.getText().toString();
                final String destinationText = destination.getSelectedItem().toString();
                String city="";
                double lat = 30.766509, lon = 76.783478;

                if(destinationText.equals("Chandigarh")){
                    city = "CHD";
                    lat = 30.766509;
                    lon= 76.783478; // PEC
//                    List<String> place0 = new ArrayList<>();
//                    List<String> place1 = new ArrayList<>();
//                    List<String> place2 = new ArrayList<>();
//                    List<String> place3 = new ArrayList<>();
//                    place0.add("3");
//                    place0.add("2");
//                    place0.add("1");
//
//                    place1.add("6");
//                    place1.add("5");
//                    place1.add("4");
//                    place1.add("9");
//
//                    place2.add("10");
//                    place2.add("7");
//                    place2.add("8");
//
//                    place3.add("11");
//                    place3.add("12");
//                    placesList.add(place0);
//                    placesList.add(place1);
//                    placesList.add(place2);
//                    placesList.add(place3);


                }

                else if (destinationText.equals("Delhi")){
                    city = "DEL";
                    lat = 28.613950;
                    lon= 77.209027;
//                    List<String> place0 = new ArrayList<>();
//                    List<String> place1 = new ArrayList<>();
//                    List<String> place2 = new ArrayList<>();
//                    List<String> place3 = new ArrayList<>();
//                    place0.add("13");
//                    place0.add("16");
//                    place0.add("24");
//
//                    place1.add("14");
//                    place1.add("22");
//                    place1.add("17");
//
//                    place2.add("20");
//                    place2.add("23");
//                    place2.add("21");
//
//                    place3.add("19");
//                    place3.add("15");
//                    place3.add("18");
//                    placesList.add(place0);
//                    placesList.add(place1);
//                    placesList.add(place2);
//                    placesList.add(place3);

                }

                else if(destinationText.equals("Mumbai")){
                    city = "MUM";
                    lat = 19.076169;
                    lon = 72.877404;
//                    List<String> place0 = new ArrayList<>();
//                    List<String> place1 = new ArrayList<>();
//                    List<String> place2 = new ArrayList<>();
//                    List<String> place3 = new ArrayList<>();
//                    place0.add("25");
//                    place0.add("26");
//                    place0.add("30");
//                    place0.add("27");
//
//                    place1.add("28");
//                    place1.add("29");
//                    place1.add("31");
//                    place1.add("32");
//
//                    place2.add("33");
//
//                    place3.add("34");
//                    placesList.add(place0);
//                    placesList.add(place1);
//                    placesList.add(place2);
//                    placesList.add(place3);

                }

                else if(destinationText.equals("Bangalore")){
                    city = "BGLR";
                    lat = 12.972144;
                    lon = 77.593981;
//                    List<String> place0 = new ArrayList<>();
//                    List<String> place1 = new ArrayList<>();
//                    List<String> place2 = new ArrayList<>();
//                    List<String> place3 = new ArrayList<>();
//                    place0.add("58");
//                    place0.add("54");
//                    place0.add("41");
//                    place0.add("42");
//                    place0.add("53");
//
//                    place1.add("56");
//                    place1.add("38");
//                    place1.add("48");
//                    place1.add("44");
//                    place1.add("40");
//                    place1.add("47");
//
//                    place2.add("37");
//                    place2.add("39");
//                    place2.add("52");
//                    place2.add("51");
//                    place2.add("45");
//
//                    place3.add("36");
//                    place3.add("57");
//                    place3.add("43");
//                    placesList.add(place0);
//                    placesList.add(place1);
//                    placesList.add(place2);
//                    placesList.add(place3);
                }

                else if(destinationText.equals("Kolkata")){
                    city = "KOL";
                    lat = 22.601850;
                    lon = 88.383058;
//                    List<String> place0 = new ArrayList<>();
//                    List<String> place1 = new ArrayList<>();
//                    List<String> place2 = new ArrayList<>();
//                    List<String> place3 = new ArrayList<>();
//                    place0.add("65");
//                    place0.add("59");
//                    place0.add("70");
//                    place0.add("60");
//
//                    place1.add("61");
//                    place1.add("62");
//                    place1.add("66");
//
//                    place2.add("64");
//                    place2.add("63");
//
//                    place3.add("67");
//                    placesList.add(place0);
//                    placesList.add(place1);
//                    placesList.add(place2);
//                    placesList.add(place3);
                }
                //creating itinerary
                final String cityId = city;
                final double srcLat = lat, srcLong= lon;
                final Map<String,Double> placeScore = new HashMap<>(); // <placeId,weightedScore>
                final Map<String,Double> placeAvgTime = new HashMap<>(); // <placeId, avgTime>
                final double a = 0.472, b = 0.391, c = 0.137;
                String userUuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                System.out.println("Create"+userUuid);
                reference5 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/user");
                reference5.child(userUuid).addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map map = dataSnapshot.getValue(Map.class);
                        final List<String> userTags = (ArrayList<String>) map.get("categoryList");

                        reference3 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/city");
                        reference3.child(cityId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                System.out.println("snapshot" + dataSnapshot.getValue());
                                Map map = dataSnapshot.getValue(Map.class);
                                final List<String> placeIds = (ArrayList<String>) map.get("placesId");

                                reference4 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

                                System.out.println("places" + placeIds);
                                for (int i = 0; i < placeIds.size(); i++) {
                                    final int j = i;
                                    // fixing DB error
                                    int id = Integer.parseInt(placeIds.get(j)) - 1;
                                    String strId = Integer.toString(id);

                                    reference4.child(strId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Map map1 = dataSnapshot.getValue(Map.class);
                                            String avgTime = map1.get("Average Time").toString();
                                            String avgRating = map1.get("avgRating").toString();
                                            String latString = map1.get("Lat").toString();
                                            String longString = map1.get("Long").toString();
                                            double placelat = Double.parseDouble(latString);
                                            double placeLong = Double.parseDouble(longString);
                                            final List<String> placeTags = (ArrayList<String>) map1.get("categoryList");
                                            double avgTimeinHrs,avgTimeinMins;
                                            StringTokenizer str = new StringTokenizer(avgTime, ":");
                                            String avgTimeHrs = str.nextToken();
                                            String avgTimeMins = str.nextToken();
                                            System.out.println("Hrs"+avgTimeHrs+"mins"+avgTimeMins);
                                            avgTimeinHrs = Double.parseDouble(avgTimeHrs);
                                            avgTimeinMins = Double.parseDouble(avgTimeMins) + avgTimeinHrs*60;
                                            placeAvgTime.put(strId, avgTimeinMins);
                                            System.out.println(strId + "megha" + avgTime);

                                            if (TextUtils.isEmpty(fromDateString)) {
                                                Toast.makeText(getApplicationContext(), "Enter From Date", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if (TextUtils.isEmpty(toDateString)) {
                                                Toast.makeText(getApplicationContext(), "Enter To Date", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            Calendar fromDate = Calendar.getInstance();
                                            Calendar toDate = Calendar.getInstance();
                                            Calendar now = Calendar.getInstance();
                                            now.set(Calendar.HOUR_OF_DAY, 0);
                                            now.set(Calendar.MINUTE, 0);
                                            now.set(Calendar.SECOND, 0);
                                            try {
                                                fromDate.setTime(dateFormatter.parse(fromDateString));
                                                toDate.setTime(dateFormatter.parse(toDateString));

                                            } catch(ParseException e){
                                                e.printStackTrace();
                                            }

                                            Date fromTime = fromDate.getTime();
                                            Date toTime = toDate.getTime();
                                            Date now1 = now.getTime();


                                            long diff = toTime.getTime() - fromTime.getTime();
                                            long days = diff / 1000 / 60 / 60 / 24;
                                            days =days+1;

                                            System.out.println("days   "+days);
                                            long diff1 = fromTime.getTime() - now1.getTime();
                                            long days1 = diff1/1000/60/60/24;

                                            long diff2 =toTime.getTime() - now1.getTime();
                                            long days2 = diff2/1000/60/60/24;

                                            if(days1<0){
                                                Toast.makeText(getApplicationContext(), "From Date before today", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if(days2<0){
                                                Toast.makeText(getApplicationContext(), "To Date before today", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if (days<=0) {
                                                Toast.makeText(getApplicationContext(), "Enter Correct To Date", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

//                if (days>=5) {
//                    Toast.makeText(getApplicationContext(), "Days Can't be more than 4", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                                            //score generation
                                            int tagsMatched=0;
                                            double Su=0,Sf,Sd,score,weightedScore;
                                            double dis;
                                            System.out.println("userTags"+userTags);
                                            if(userTags != null) {
                                                System.out.println("userTagsnn"+userTags);
                                                System.out.println("placeTags"+placeTags);
                                                for (int lc = 0; lc < userTags.size(); lc++) {
                                                    for (int lc2 = 0; lc2 < placeTags.size(); lc2++) {
                                                        System.out.println(Integer.parseInt(userTags.get(lc)));
                                                        System.out.println(Integer.parseInt(placeTags.get(lc2)));
                                                        if ( Integer.parseInt(userTags.get(lc)) == Integer.parseInt(placeTags.get(lc2))) {
                                                            tagsMatched++;
                                                            System.out.println("matched"+tagsMatched);
                                                        }
                                                    }

                                                }
                                                Su = tagsMatched*5 / userTags.size();
                                                System.out.println("tagsmatched"+tagsMatched+"su"+Su);
                                            }
                                            else
                                                Su=0;

                                            Sf = Double.parseDouble(avgRating);

                                            //dis = Math.sqrt( Math.pow((srcLat - placelat),2) + Math.pow((srcLong - placeLong),2));
                                            Location locationA = new Location("point A");

                                            locationA.setLatitude(srcLat);
                                            locationA.setLongitude(srcLong);

                                            Location locationB = new Location("point B");

                                            locationB.setLatitude(placelat);
                                            locationB.setLongitude(placeLong);

                                            dis = (double) locationA.distanceTo(locationB); // in meters
                                            System.out.println("dis"+dis);
                                            Sd = Math.min(25000/dis, 5);

                                            System.out.println("Su"+Su);
                                            System.out.println("Sf"+Sf);
                                            System.out.println("Sd"+Sd);

                                            score = a*Su + b*Sf + c*Sd;
                                            System.out.println("score"+score);

                                            weightedScore = score/avgTimeinMins;
                                            //weightedScore = score;
                                            placeScore.put(strId, weightedScore);
                                            System.out.println("placeScore"+placeScore);

                                            if(placeScore.size() == placeIds.size()) //all places added
                                            {
                                                //places selection
                                                Map<String, Double> sorted = null;
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                    sorted = placeScore
                                                            .entrySet()
                                                            .stream()
                                                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                                                            .collect(
                                                                    toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
                                                }

                                                System.out.println("placeScorebs"+placeScore);
                                                System.out.println("sorted"+sorted);

                                                int d=0, it=0;
                                                Calendar fromDateSc = Calendar.getInstance();
                                                Calendar toDateSc = Calendar.getInstance();
                                                try {
                                                    fromDateSc.setTime(dateFormatter.parse(fromDateString));
                                                    toDateSc.setTime(dateFormatter.parse(toDateString));

                                                } catch(ParseException e){
                                                    e.printStackTrace();
                                                }

                                                Date fromTimeSc = fromDateSc.getTime();
                                                Date toTimeSc = toDateSc.getTime();

                                                long diffSc = toTimeSc.getTime() - fromTimeSc.getTime();
                                                long daysSc = diffSc / 1000 / 60 / 60 / 24;
                                                daysSc =daysSc+1;

                                                while(d < daysSc && sorted.size() > it)
                                                {
                                                    int time = 0; // in mins
                                                    List<String> finalPlaces = new ArrayList<>();
                                                    time += placeAvgTime.get(sorted.keySet().toArray()[it]);
                                                    System.out.println("day"+d+"time"+time);
                                                    while (time <= 360 && it < sorted.size()){
                                                        if(it<sorted.size())
                                                            finalPlaces.add(sorted.keySet().toArray()[it].toString());
                                                        if(it+1<sorted.size())
                                                            time += placeAvgTime.get(sorted.keySet().toArray()[it+1]);
                                                        System.out.println("day"+d+"time"+time);
                                                        it++;
                                                    }
                                                    d++;
                                                    System.out.println("finalPlaces day"+d+" "+finalPlaces);
                                                    placesList.add(finalPlaces);
                                                }

                                                if(d<daysSc)
                                                {
                                                    Toast.makeText(getApplicationContext(), "Days should be less than "+Integer.toString(d+1), Toast.LENGTH_SHORT).show();
                                                    placesList.clear();
                                                    return;
                                                }

                                                // places selection ends here

                                                Map<String,String> map = new HashMap<>();
                                                map.put("startDate",fromDateString);
                                                map.put("endDate",toDateString);
                                                map.put("totalDays", Long.toString(days));
                                                map.put("User UID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                map.put("destination", destinationText);

                                                reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/trips");
                                                String tripId = reference1.push().getKey();
                                                reference1.child(tripId).setValue(map);


                                                reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");
                                                for(int i=0;i<days;i++){
                                                    String itineraryId = reference2.push().getKey();
                                                    reference2.child(itineraryId).child("tripId").setValue(tripId);
                                                    reference2.child(itineraryId).child("placesId").setValue(placesList.get(i));
                                                    itineraryIds.add(itineraryId);
                                                }


                                                Intent i = new Intent(CreateTrip.this, Itinerary.class);
                                                i.putExtra("startDate",fromDateString);
                                                System.out.println("home"+fromDateString);
//                i.putExtra("startDateCalendar",fromDate);
                                                i.putExtra("days",Long.toString(days));
                                                System.out.println("array value"+itineraryIds);
                                                i.putExtra("itineraryIds",(Serializable)itineraryIds);

                                                startActivity(i);
                                                finish();



                                            }

                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {

                                        }
                                    });
                                }

                            }

                            //});



                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });




            }


        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        } else if(view == toDateEtxt) {
            toDatePickerDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}