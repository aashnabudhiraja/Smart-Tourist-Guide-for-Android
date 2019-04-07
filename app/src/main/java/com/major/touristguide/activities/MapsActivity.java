package com.major.touristguide.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.ui.IconGenerator;
import com.major.touristguide.R;

import java.util.ArrayList;
import java.util.List;

import static com.major.touristguide.R.id.map;


public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener {

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 5;

    private static ArrayList<LatLng> positions = new ArrayList<>();
    private static ArrayList<String> positionsTitles = new ArrayList<>();

    private FusedLocationProviderClient mFusedLocationClient;

    double latitudeCurr;
    double longitudeCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        System.out.println("Start");

        if (positions.size() > 0) positions = new ArrayList<>();
        if (positionsTitles.size() > 0) positionsTitles = new ArrayList<>();

//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Creating Route...");
//        pd.show();

        Bundle extras = getIntent().getExtras();
        final List<String> placeNameList = extras.getStringArrayList("placeNames");
        final List<String> latitudeList = extras.getStringArrayList("latitudes");
        final List<String> longitudeList = extras.getStringArrayList("longitudes");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                for (int i = 0; i < placeNameList.size(); i++) {
                    positionsTitles.add(placeNameList.get(i));
                    positions.add(new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i))));
                }

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(map);
                mapFragment.getMapAsync(MapsActivity.this);

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
//                pd.dismiss();

            }
        }, 5);

    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .addAll(positions));

        stylePolyline(polyline1);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        googleMap.addMarker(new MarkerOptions().position(positions.get(0)));

        for (int i = 0; i < positions.size(); i++) {

            TextView text = new TextView(this);
            text.setText(positionsTitles.get(i));
            IconGenerator generator = new IconGenerator(this);
            generator.setContentView(text);
            Bitmap icon = generator.makeIcon();

            MarkerOptions options = new MarkerOptions()
                    .position(positions.get(i))
                    .icon(BitmapDescriptorFactory.fromBitmap(icon));
            googleMap.addMarker(options);

            builder.include(options.getPosition());

            if (i < positions.size() - 1) {
                double lat1 = positions.get(i).latitude;
                double lng1 = positions.get(i).longitude;

                // destination
                double lat2 = positions.get(i + 1).latitude;
                double lng2 = positions.get(i + 1).longitude;

                //midpoint
                double lat = (lat1 + lat2) / 2;
                double lng = (lng1 + lng2) / 2;

                double dLon = (lng2 - lng1);
                double y = Math.sin(dLon) * Math.cos(lat2);
                double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
                double brng = Math.toDegrees((Math.atan2(y, x)));

                LatLng a = LatLngBounds.builder().include(positions.get(i)).include(positions.get(i + 1)).build().getCenter();

                MarkerOptions marker = new MarkerOptions().position(a);
                marker.anchor(0.5f, 0.5f);
                marker.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.up_arrow), 50, 50, false)));
                marker.rotation((float) brng);
                marker.flat(true);

                googleMap.addMarker(marker);
            }
        }
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000, null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_nav_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNavigation();
            }
        });

    }


    private void stylePolyline(Polyline polyline) {
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void startNavigation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitudeCurr = location.getLatitude();
                                longitudeCurr = location.getLongitude();
                                startGoogleMapsNavigation(latitudeCurr, longitudeCurr);
                            } else {
                                latitudeCurr = positions.get(0).latitude;
                                longitudeCurr = positions.get(0).longitude;
                                startGoogleMapsNavigation(latitudeCurr, longitudeCurr);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startNavigation();
                } else {
                    // permission denied, Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void startGoogleMapsNavigation(double latitudeCurr, double longitudeCurr) {
        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + latitudeCurr + "," + longitudeCurr + "&destination=" + positions.get(positions.size() - 1).latitude + "," + positions.get(positions.size() - 1).longitude + "&waypoints=";
        for (int i = 0; i < positions.size() - 2; i++)
            uri += positions.get(i).latitude + "," + positions.get(i).longitude + "%7C";
        uri += positions.get(positions.size() - 2).latitude + "," + positions.get(positions.size() - 2).longitude + "&travelmode=driving&dir_action=navigate";

        System.out.println(uri);

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri));
        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

}