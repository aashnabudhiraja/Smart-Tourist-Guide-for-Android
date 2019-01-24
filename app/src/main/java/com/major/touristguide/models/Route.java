package com.major.touristguide.models;

import com.major.touristguide.services.RouteManager;

import java.util.ArrayList;
import java.util.Collections;

public class Route {

    // Holds our route
    public ArrayList<Destination> route = new ArrayList<Destination>();
    // Cache of distance of route
    private double distance = 0;
    private double fitness = 0;

    // Construct a blank route
    public Route() {
        for (int i = 0; i < RouteManager.numberOfDestinations(); i++) {
            route.add(null);
        }
    }

    // Constructs a route from another route
    @SuppressWarnings("unchecked")
    public Route(Route someRoute){
        this.route = (ArrayList<Destination>) someRoute.route.clone();
    }

    // generate a random route
    public void generateIndividual() {
        // Loop through all our destinations in TM and add them to our route -- shallow copy
        for (int index = 0; index < RouteManager.numberOfDestinations(); index++) {
            setDestination(index, RouteManager.getDestination(index));
        }
        // Randomly reorder the route
        Collections.shuffle(route);

//        // deep copy (uses more memory)
//        route = new ArrayList<Destination>(RouteManager.allDestinations());
//        Collections.shuffle(route);
    }

    // make small mutation - swap order of 2 random destinations
    public void mutateIndividual() {

        int routePos1 = (int) (routeSize() * Math.random());
        int routePos2 = (int) (routeSize() * Math.random());

        Collections.swap(route, routePos1, routePos2);

//        Destination dest1 = getDestination(routePos1);
//        Destination dest2 = getDestination(routePos2);
//        //swap the 2 destinations
//        setDestination(routePos2, dest1);
//        setDestination(routePos1, dest2);
    }

    public ArrayList<Destination> getAllDest() {
        return route;
    }

    public Destination getDestination(int routePosition) {
        return route.get(routePosition);
    }

    // sets a Destination to certain position in the route
    public void setDestination(int routePosition, Destination destination) {
        route.set(routePosition, destination);
        // we have altered the route, so reset distance and fitness
        distance = 0;
    }

    public double getDistance(){
        if (distance == 0) {
            int routeDistance = 0;
            // Loop through our route's destinations
            for (int Index=0; Index < routeSize(); Index++) {
                // Get  we're travelling from
                Destination fromDestination = getDestination(Index);
                // Destination we're travelling to
                Destination destinationDestination;
                // Check we're not on our route's last , if we are set our
                // route's final destination to our starting
                if(Index+1 < routeSize()){
                    destinationDestination = getDestination(Index+1);
                }
                else{
                    destinationDestination = getDestination(0);
                }
                // Get the distance between the two cities
                routeDistance += fromDestination.distanceTo(destinationDestination); // DISTANCE THROUGH ROADS
            }
            distance = routeDistance;
        }
        return distance;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/ getDistance();
        }
        return fitness;
    }

    // Check if the route contains a
    public boolean containsDestination(Destination destination){
        return route.contains(destination);
    }

    public int routeSize() {
        return route.size();
    }

    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < routeSize(); i++) {
            geneString += getDestination(i)+"|";
        }
        return geneString;
    }
}
