package com.major.touristguide.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.major.touristguide.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner destination;

    private EditText fromDateEtxt;
    private EditText toDateEtxt;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private SimpleDateFormat dateFormatter;
    private Button confirm;
    Firebase reference1;
    Firebase reference2;
    Toolbar toolbar;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private TextView heading;


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
                        startActivity(new Intent(HomeActivity.this, Login.class));
                        finish();
                        return true;
                    }

                    case R.id.home: {
                        startActivity(new Intent(HomeActivity.this,MainHome.class));
                        finish();
                        return true;
                    }
                    default:
                        return true;
                }


            }
        });

        final List<List<String>> placesList = new ArrayList<>();

        List<String> place0 = new ArrayList<>();
        List<String> place1 = new ArrayList<>();
        List<String> place2 = new ArrayList<>();
        List<String> place3 = new ArrayList<>();
        place0.add("-LNqYJ6J_Le9m3AjpG31");
        place0.add("-LNqcIBfpDOoRWO1VS8d");
        place0.add("-LNqcIB3OkjLGJmXFqjw");

        place1.add("-LNqcIBhh5lavC3NShQs");
        place1.add("-LNqcIBhh5lavC3NShQt");
        place1.add("-LNqcIBiF65WyfdCMt8j");
        place1.add("-LNqcIBlziJ-qZ2NsQ89");

        place2.add("-LNqcIBjeodYIQP-Df91");
        place2.add("-LNqcIBlziJ-qZ2NsQ8A");
        place2.add("-LNqcIBkjkZpoKasH8LN");

        place3.add("-LNqcIBmg5DSJiROobmh");
        place3.add("-LNqcIBmg5DSJiROobmg");
        placesList.add(place0);
        placesList.add(place1);
        placesList.add(place2);
        placesList.add(place3);

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
                List<String> itineraryIds = new ArrayList<>();

                String fromDateString = fromDateEtxt.getText().toString();
                System.out.println("real value"+fromDateString);
                String toDateString = toDateEtxt.getText().toString();
                String destinationText = destination.getSelectedItem().toString();
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

                if (days>=5) {
                    Toast.makeText(getApplicationContext(), "Days Can't be more than 4", Toast.LENGTH_SHORT).show();
                    return;
                }

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


                Intent i = new Intent(HomeActivity.this, ItineraryActivity.class);
                i.putExtra("startDate",fromDateString);
                System.out.println("home"+fromDateString);
//                i.putExtra("startDateCalendar",fromDate);
                i.putExtra("days",Long.toString(days));
                System.out.println("array value"+itineraryIds);
                i.putExtra("itineraryIds",(Serializable)itineraryIds);

                startActivity(i);
                finish();


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