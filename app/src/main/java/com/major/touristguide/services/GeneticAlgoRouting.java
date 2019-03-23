package com.major.touristguide.services;

import android.content.SharedPreferences;

import com.major.touristguide.models.Itinerary;
import com.major.touristguide.models.Population;
import com.major.touristguide.models.Route;

public class GeneticAlgoRouting {

    /* GA parameters */
    private static float mutationRate;
    private static float crossoverRate;
    private static boolean elitism = true;

    private static int tournamentSize = 5; // not adjustable


    // Evolves a population over one generation
    public static Population evolvePopulation(Population pop, SharedPreferences pref, Itinerary currentItinerary) {

        mutationRate = Float.parseFloat(pref.getString("muteRate", "0.025f"));
        crossoverRate = Float.parseFloat(pref.getString("crossRate", "0.5f"));
        elitism = Boolean.parseBoolean(pref.getString("elitism", "true"));

        Population newPopulation = new Population(pop.populationSize(), false, currentItinerary);

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveRoute(0, pop.getFittest());
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            Route parent1 = tournamentSelection(pop, currentItinerary);
            Route parent2 = tournamentSelection(pop, currentItinerary);

            Route child;
            if (Math.random() <= crossoverRate) {
                // Crossover parents
                child = crossover(parent1, parent2, currentItinerary);
            } else {
                child = new Route(parent1);
            }

            // Add child to new population
            newPopulation.saveRoute(i, child);
        }

        // Mutate the new population a bit to add some new genetic material
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getRoute(i));
        }

        return newPopulation;
    }

    // Applies crossover to a set of parents and creates offspring
    public static Route crossover(Route parent1, Route parent2, Itinerary itinerary) {
        // Create new child route
        Route child = new Route(itinerary);

        // Get start and end sub route positions for parent1's route
        int startPos = (int) (Math.random() * parent1.routeSize());
        int endPos = (int) (Math.random() * parent1.routeSize());

        // Loop and add the sub route from parent1 to our child
        for (int i = 0; i < child.routeSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setDestination(i, parent1.getDestination(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setDestination(i, parent1.getDestination(i));
                }
            }
        }

        // Loop through parent2's city route
        for (int i = 0; i < parent2.routeSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsDestination(parent2.getDestination(i))) {
                // Loop to find a spare position in the child's route
                for (int ii = 0; ii < child.routeSize(); ii++) {
                    // Spare position found, add city
                    if (child.getDestination(ii) == null) {
                        child.setDestination(ii, parent2.getDestination(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    // Mutate a route using swap mutation
    private static void mutate(Route route) {
        // Loop through route cities
        for(int routePos1=0; routePos1 < route.routeSize(); routePos1++){
            // Apply mutation rate
            if(Math.random() < mutationRate){
                // Get a second random position in the route
                route.mutateIndividual();
            }
        }
    }

    // Selects candidate route for crossover
    private static Route tournamentSelection(Population pop, Itinerary itinerary) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false, itinerary);
        // For each place in the tournament get a random candidate route and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveRoute(i, pop.getRoute(randomId));
        }
        // Get the fittest route
        Route fittest = tournament.getFittest();
        return fittest;
    }
}
