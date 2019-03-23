package com.major.touristguide.models;

import com.major.touristguide.models.Destination;

import java.util.ArrayList;

public class Itinerary {

    //List of Destinations
    public static ArrayList<Destination> destinations = new ArrayList<Destination>();

    public  static void addDestination(Destination destination) {
        destinations.add(destination);
    }

    //get the destination by index
    public static Destination getDestination(int index) {
        return (Destination) destinations.get(index);
    }

    public static void removeAll() {
        destinations = new ArrayList<Destination>();
    }

    public static ArrayList<Destination> allDestinations() {
        return destinations;
    }

    // get the number of Destinations
    public static int numberOfDestinations() {
        return destinations.size();
    }
}
