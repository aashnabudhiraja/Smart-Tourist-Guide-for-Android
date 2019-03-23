package com.major.touristguide.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.major.touristguide.R;
import com.major.touristguide.models.Destination;
import com.major.touristguide.models.Itinerary;
import com.major.touristguide.models.Population;
import com.major.touristguide.models.Route;
import com.major.touristguide.services.GeneticAlgoRouting;

import java.util.ArrayList;
import java.util.List;

public class Routing extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
//    private boolean solveInProgress = false; // Flag for GA_Task or SA_Task being in progress
//    private AsyncTask solverTask; // Reference to the GA_Task or SA_Task that is in progress
    private int publishInterval = 333; // defines publishing rate in milliseconds
    private Itinerary currentItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);

        // mock set of destinations :
        ArrayList<Destination> destinations = new ArrayList<>();
        destinations.add(new Destination(0, 0, "Equator"));
        destinations.add(new Destination(100, 0, "Equator1"));
        destinations.add(new Destination(200, 0, "Equator2"));
        destinations.add(new Destination(300, 0, "Equator3"));

        currentItinerary = new Itinerary();
        currentItinerary.destinations = destinations;

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        runGeneticAlgorithm();
    }

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
