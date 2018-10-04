package com.major.touristguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.major.touristguide.R.id.map;


public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener{

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 5;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    private static ArrayList<LatLng> positions = new ArrayList<>();
    private static ArrayList<String> positionsTitles = new ArrayList<>();

    Firebase reference1,reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        System.out.println("Start");

        if(positions.size() > 0) positions = new ArrayList<>();
        if(positionsTitles.size() > 0) positionsTitles = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        String itineraryId = extras.getString("itineraryId");
        List<String> placeNameList = extras.getStringArrayList("placeNames");
        List<String> latitudeList = extras.getStringArrayList("latitudes");
        List<String> longitudeList = extras.getStringArrayList("longitudes");

        for(int i=0; i<placeNameList.size(); i++) {
            positionsTitles.add(placeNameList.get(i));
            positions.add(new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i))));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
            Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                    .addAll(positions));

            stylePolyline(polyline1);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < positions.size(); i++) {

            TextView text = new TextView(this);
            text.setText(positionsTitles.get(i));
            IconGenerator generator = new IconGenerator(this);
//        generator.setBackground(this.getDrawable(R.drawable.bubble_mask));
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
                double lat2 = positions.get(i+1).latitude;
                double lng2 = positions.get(i+1).longitude;

                //midpoint
                double lat = (lat1 + lat2) / 2;
                double lng = (lng1 + lng2) / 2;

                double dLon = (lng2 - lng1);
                double y = Math.sin(dLon) * Math.cos(lat2);
                double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
                double brng = Math.toDegrees((Math.atan2(y, x)));

                LatLng a = LatLngBounds.builder().include(positions.get(i)).include(positions.get(i+1)).build().getCenter();

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

    }


    private void stylePolyline(Polyline polyline) {
        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            polyline.setPattern(null);
        }
    }

}