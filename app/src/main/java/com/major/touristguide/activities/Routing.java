package com.major.touristguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.major.touristguide.R;
import com.major.touristguide.models.Destination;
import com.major.touristguide.models.Itinerary;
import com.major.touristguide.models.Population;
import com.major.touristguide.models.Route;
import com.major.touristguide.services.GeneticAlgoRouting;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routing extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private int publishInterval = 333; // defines publishing rate in milliseconds
    private Itinerary currentItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> placeNameList = extras.getStringArrayList("placeNames");
        ArrayList<String> latitudeList = extras.getStringArrayList("latitudes");
        ArrayList<String> longitudeList = extras.getStringArrayList("longitudes");

        Map<LatLng, String> placeList = new HashMap<>();
        ArrayList<LatLng> latLngList = new ArrayList<>();
        for(int i=0; i<latitudeList.size(); i++)
            placeList.put(new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i))), placeNameList.get(i));
        for(int i=0; i<latitudeList.size(); i++)
            latLngList.add(new LatLng(Double.parseDouble(latitudeList.get(i)), Double.parseDouble(longitudeList.get(i))));

        ArrayList<LatLng> bestRoute = routing(latLngList);

        ArrayList<String> newplaceNameList = new ArrayList<>();
        ArrayList<String> newlatitudeList = new ArrayList<>();
        ArrayList<String> newlongitudeList = new ArrayList<>();

        for(int i=0; i<latitudeList.size(); i++) {
            newlatitudeList.add((String.valueOf(bestRoute.get(i).latitude)));
            newlongitudeList.add((String.valueOf(bestRoute.get(i).longitude)));
            newplaceNameList.add(placeList.get(bestRoute.get(i)));
        }

        System.out.println(newlatitudeList);

        Intent i = new Intent(Routing.this,MapsActivity.class);
        i.putStringArrayListExtra("placeNames", newplaceNameList);
        i.putStringArrayListExtra("latitudes", newlatitudeList);
        i.putStringArrayListExtra("longitudes", newlongitudeList);
        startActivity(i);
        finish();

/*        ArrayList<Destination> destinations = new ArrayList<>();
        for(int i=0; i<placeNameList.size(); i++) {
            destinations.add(new Destination(Double.valueOf(latitudeList.get(i)), Double.valueOf(longitudeList.get(i)), placeNameList.get(i)));
        }

        currentItinerary = new Itinerary();
        currentItinerary.destinations = destinations;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        runGeneticAlgorithm();*/

    }

    public ArrayList routing(ArrayList latLngList) {
        ArrayList<LatLng> route = new ArrayList<>();

        //get source (nearest place from zone) ------------------------------ TODO
        LatLng source = ((LatLng) latLngList.get(0));
        latLngList.remove(0);

        //store all permutations of remaining places
        ArrayList<ArrayList<LatLng>> permutations = permute(latLngList);

        //get shortest path
        route = findShortestPath(permutations, source);

        return route;
    }

    public ArrayList findShortestPath(ArrayList<ArrayList<LatLng>> permutations, LatLng source) {
        ArrayList<LatLng> result = new ArrayList<>();
        double minLength = Double.MAX_VALUE;
        double currentLength = 0;

        for(int i = 0; i<permutations.size(); i++) {
            ArrayList<LatLng> currentPath = permutations.get(i);
            currentLength = distance(source, currentPath.get(0));
            for(int j=1; j<currentPath.size(); j++) {
                currentLength += distance(currentPath.get(j-1), currentPath.get(j));
            }
            if(currentLength < minLength) {
                minLength = currentLength;
                result = currentPath;
            }
            System.out.print(currentLength);
            System.out.println(currentPath);
        }

        result.add(0, source);

        return result;
    }

    public ArrayList<ArrayList<LatLng>> permute(ArrayList arr) {
        ArrayList<ArrayList<LatLng>> list = new ArrayList<>();
        ArrayList<LatLng> result = new ArrayList<>();
        permuteHelper(list, result, arr);
        return list;
    }

    public void permuteHelper(ArrayList<ArrayList<LatLng>> list, ArrayList<LatLng> resultList, ArrayList arr){

        // Base case
        if(resultList.size() == arr.size()){
            list.add(new ArrayList<>(resultList));
        }
        else{
            for(int i = 0; i < arr.size(); i++){

                if(resultList.contains(arr.get(i)))
                {
                    // If element already exists in the list then skip
                    continue;
                }
                // Choose element
                resultList.add(((LatLng) arr.get(i)));
                // Explore
                permuteHelper(list, resultList, arr);
                // Unchoose element
                resultList.remove(resultList.size() - 1);
            }
        }
    }

    public double distance(LatLng latlng1, LatLng latlng2) {
        float[] dist = new float[1];

        Location.distanceBetween(
                latlng1.latitude,
                latlng1.longitude,
                latlng2.latitude,
                latlng2.longitude,
                dist);

        return dist[0];
    }


    // ----------------------------------------------------------------------------------------------------------------------------------------


    // call this to run GA
    public void runGeneticAlgorithm() {
//        if (RouteManager.numberOfDestinations()==0 || solveInProgress) return;

        GA_Task task = new GA_Task();
//        solverTask = task;
        task.execute();

        System.gc();
    }

    // solves and displays TSP using GA
    class GA_Task extends AsyncTask<Void, Route, Population> {

        Route bestRouteSoFar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            solveInProgress = true;

            //change color of button to indicate processing
//            Button GA_button = (Button) findViewById(R.id.graphGAButton);
//            GA_button.setBackgroundColor(0xb0FF9933);
//
//            // change text of clear button
//            Button button = (Button) findViewById(R.id.clearButton);
//            button.setText("STOP");
        }

        @Override
        protected Population doInBackground(Void... voids) {

            // Initialization
            int popSize = Integer.parseInt(mSharedPreferences.getString("popSize", "50"));
            int generations = Integer.parseInt(mSharedPreferences.getString("generations", "200"));

            Population pop = new Population(popSize, true, currentItinerary);
            Route fittest = pop.getFittest();
            publishProgress(fittest);

            long time = System.currentTimeMillis();
            long lastPublishTime = time;

            for (int i = 0; i < generations; i++) {
                if ( isCancelled() ) break;

                time = System.currentTimeMillis();
                pop = GeneticAlgoRouting.evolvePopulation(pop, mSharedPreferences, currentItinerary);
                bestRouteSoFar = new Route(pop.getFittest());
                if (time - lastPublishTime > publishInterval) {
                    lastPublishTime = time;
                    publishProgress(pop.getFittest());
                }
            }
            return pop;
        }

        @Override
        protected void onProgressUpdate(Route... routes) {
            super.onProgressUpdate(routes[0]);
            Route currentBest = routes[0];
//            graphMap(currentBest);
            System.out.println("Current distance: " + currentBest.getDistance());
        }

        @Override
        protected void onPostExecute(Population pop) {
            super.onPostExecute(pop);
            Route fittest = pop.getFittest();
//            graphMap(fittest);
            System.out.println("GA Final distance: " + pop.getFittest().getDistance());
            System.out.println("GA Final route: " + pop.getFittest().toString());

            // Display final distance
//            TextView tv1 = (TextView) findViewById(R.id.final_distance);
//            int finalDistance = (int) pop.getFittest().getDistance();
//            tv1.setText("FINAL DISTANCE: " + finalDistance + " km");

            //change color of button to indicate finish
//            Button GA_button = (Button) findViewById(R.id.graphGAButton);
//            GA_button.setBackgroundColor(0xb0ffffff);
//
//            // change text of clear button
//            Button button = (Button) findViewById(R.id.clearButton);
//            button.setText("CLEAR");

            pop = null;
//            solveInProgress = false;


            // go to map
            Bundle extras = getIntent().getExtras();
            final ArrayList<String> placeNameList = extras.getStringArrayList("placeNames");
            final ArrayList<String> latitudeList = extras.getStringArrayList("latitudes");
            final ArrayList<String> longitudeList = extras.getStringArrayList("longitudes");
            Intent i = new Intent(Routing.this,MapsActivity.class);
            i.putStringArrayListExtra("placeNames", placeNameList);
            i.putStringArrayListExtra("latitudes", latitudeList);
            i.putStringArrayListExtra("longitudes", longitudeList);
            startActivity(i);
        }
    }
}
