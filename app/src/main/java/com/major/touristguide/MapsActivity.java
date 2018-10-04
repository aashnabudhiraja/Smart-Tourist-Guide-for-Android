package com.major.touristguide;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

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

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    private static ArrayList<LatLng> positions = new ArrayList<>();
    private static ArrayList<String> positionsTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        if(positions.size() > 0) positions = new ArrayList<>();
        if(positionsTitles.size() > 0) positionsTitles = new ArrayList<>();

        double[] positionsLatitudes = {30.745828, 30.759160, 30.752941, 30.756785, 30.747579, 30.7398}; //get from db
        double[] positionsLongitudes = {76.810234, 76.807490, 76.805045, 76.802335, 76.784228, 76.7827}; //get from db
        String[] positionTitlesFromDb = {"Sukhna Lake", "Open Hand Monument", "Rock Garden", "Capitol Complex Tourist Center", "Rose Garden", "Sector 17 Plaza"}; // get from db

        for(int i = 0; i < positionsLatitudes.length; i++) {
            positions.add(new LatLng(positionsLatitudes[i], positionsLongitudes[i]));
            positionsTitles.add(positionTitlesFromDb[i]);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .addAll(positions));
//        polyline1.setTag("A");

        stylePolyline(polyline1);

        // Position the map's camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.752941, 76.805045), 14));

//        googleMap.setOnPolylineClickListener(this);

        for(int i=0; i<positions.size(); i++) {

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
        }

    }


    private void stylePolyline(Polyline polyline) {
        /*String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }*/

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLACK_ARGB);
        polyline.setJointType(JointType.ROUND);
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

//        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
//                Toast.LENGTH_SHORT).show();
    }

}