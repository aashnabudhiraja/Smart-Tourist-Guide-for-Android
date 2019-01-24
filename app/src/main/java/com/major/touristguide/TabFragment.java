package com.major.touristguide;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.major.touristguide.activities.ItineraryEdit;
import com.major.touristguide.activities.MapsActivity;
import com.major.touristguide.activities.Routing;
import com.major.touristguide.models.RowItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TabFragment extends Fragment implements AdapterView.OnItemClickListener {

    int position;
    static List<String> itineraryIds;
    static String startDate;
    Firebase reference1,reference2;
    //private TextView textView;
    Button routeButton, editButton;

    ListView listView;
    List<RowItem> rowItems;
    final List<List<String>> placesList = new ArrayList<>();
    Map<String, ArrayList<String>> placeIdsPerItinerary;
    Map<String, ArrayList<String>> placeNamesPerItinerary;
    Map<String, ArrayList<String>> openPerItinerary;
    Map<String, ArrayList<String>> closePerItinerary;
    Map<String, ArrayList<String>> latitudesPerItinerary;
    Map<String, ArrayList<String>> longitudesPerItinerary;

    public static Fragment getInstance(int position, List<String> itineraryId, String stDate) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        itineraryIds= itineraryId;
        startDate = stDate;

        System.out.println("ite" + itineraryIds);
        System.out.println("ite start" + startDate);

        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("pos");

        List<String> place0 = new ArrayList<>();
        List<String> place1 = new ArrayList<>();
        List<String> place2 = new ArrayList<>();
        List<String> place3 = new ArrayList<>();
        place0.add("10:00");
        place0.add("2:00");
        place0.add("5:00");

        place1.add("10:00");
        place1.add("1:30");
        place1.add("4:00");
        place1.add("6:00");

        place2.add("10:00");
        place2.add("1:00");
        place2.add("4:00");

        place3.add("10:00");
        place3.add("4:00");
        placesList.add(place0);
        placesList.add(place1);
        placesList.add(place2);
        placesList.add(place3);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        placeIdsPerItinerary = new HashMap<>();
//        placeNamesPerItinerary = new HashMap<>();
//        openPerItinerary = new HashMap<>();
//        closePerItinerary = new HashMap<>();
//        latitudesPerItinerary = new HashMap<>();
//        longitudesPerItinerary = new HashMap<>();

        Firebase.setAndroidContext(view.getContext());
//        rowItems = new ArrayList<>();

        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();

        reference1.child(itineraryIds.get(position)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("snapshot"+dataSnapshot.getValue());
                Map map = dataSnapshot.getValue(Map.class);
                final List<String>placeIds = (ArrayList<String>)map.get("placesId");

                reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

                System.out.println("places"+placeIds);
                if(placeIds != null)
                for(int i=0;i<placeIds.size();i++) {
                    rowItems = new ArrayList<>();
                    placeIdsPerItinerary = new HashMap<>();
                    placeNamesPerItinerary = new HashMap<>();
                    openPerItinerary = new HashMap<>();
                    closePerItinerary = new HashMap<>();
                    latitudesPerItinerary = new HashMap<>();
                    longitudesPerItinerary = new HashMap<>();
                    final int j=i;
                    if(placeIds.get(j) != null)
                    reference2.child(placeIds.get(j)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map map1 = dataSnapshot.getValue(Map.class);
                            String placeName = map1.get("placeName").toString();
                            String openTime = map1.get("openTime").toString();
                            String closeTime = map1.get("closeTime").toString();
                            rowItems.add(new RowItem(placesList.get(position).get(j), placeName, "Place Opens at "+openTime+"\nCloses at "+closeTime));
                            CustomBaseAdapter adapter = new CustomBaseAdapter(getContext(), rowItems);
                            listView.setAdapter(adapter);

                            //populate placeids list
                            if(placeIdsPerItinerary.isEmpty()) {
                                ArrayList<String> placeIdsList = new ArrayList<>();
                                placeIdsList.add(placeIds.get(j));
                                placeIdsPerItinerary.put(itineraryIds.get(position), placeIdsList);
                            }
                            else {
                                ArrayList<String> placeIdsList = placeIdsPerItinerary.get(itineraryIds.get(position));
                                placeIdsList.add(placeIds.get(j));
                                placeIdsPerItinerary.put(itineraryIds.get(position), placeIdsList);
                            }

                            //populate place names list
                            if(placeNamesPerItinerary.isEmpty()) {
                                ArrayList<String> placeNameList = new ArrayList<>();
                                placeNameList.add(placeName);
                                placeNamesPerItinerary.put(itineraryIds.get(position), placeNameList);
                            }
                            else {
                                ArrayList<String> placeNameList = placeNamesPerItinerary.get(itineraryIds.get(position));
                                placeNameList.add(placeName);
                                placeNamesPerItinerary.put(itineraryIds.get(position), placeNameList);
                            }

                            //populate open list
                            if(openPerItinerary.isEmpty()) {
                                ArrayList<String> openList = new ArrayList<>();
                                openList.add(map1.get("openTime").toString());
                                openPerItinerary.put(itineraryIds.get(position), openList);
                            }
                            else {
                                ArrayList<String> openList = openPerItinerary.get(itineraryIds.get(position));
                                openList.add(map1.get("openTime").toString());
                                openPerItinerary.put(itineraryIds.get(position), openList);
                            }

                            //populate close list
                            if(closePerItinerary.isEmpty()) {
                                ArrayList<String> closeList = new ArrayList<>();
                                closeList.add(map1.get("closeTime").toString());
                                closePerItinerary.put(itineraryIds.get(position), closeList);
                            }
                            else {
                                ArrayList<String> closeList = closePerItinerary.get(itineraryIds.get(position));
                                closeList.add(map1.get("closeTime").toString());
                                closePerItinerary.put(itineraryIds.get(position), closeList);
                            }

                            //populate latitudes list
                            if(latitudesPerItinerary.isEmpty()) {
                                ArrayList<String> latitudeList = new ArrayList<>();
                                latitudeList.add(map1.get("latitude").toString());
                                latitudesPerItinerary.put(itineraryIds.get(position), latitudeList);
                            }
                            else {
                                ArrayList<String> latitudeList = latitudesPerItinerary.get(itineraryIds.get(position));
                                latitudeList.add(map1.get("latitude").toString());
                                latitudesPerItinerary.put(itineraryIds.get(position), latitudeList);
                            }

                            //populate longitudes list
                            if(longitudesPerItinerary.isEmpty()) {
                                ArrayList<String> longitudeList = new ArrayList<>();
                                longitudeList.add(map1.get("longitude").toString());
                                longitudesPerItinerary.put(itineraryIds.get(position), longitudeList);
                            }
                            else {
                                ArrayList<String> longitudeList = longitudesPerItinerary.get(itineraryIds.get(position));
                                longitudeList.add(map1.get("longitude").toString());
                                longitudesPerItinerary.put(itineraryIds.get(position), longitudeList);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

                pd.dismiss();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        System.out.println("position"+position);
        listView = (ListView) view.findViewById(R.id.list);
        routeButton=(Button) view.findViewById(R.id.route);
        editButton=(Button) view.findViewById(R.id.editButton);
        listView.setOnItemClickListener(this);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        Calendar startDateCalendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        System.out.println("startdate final"+startDate);
        try {
            startDateCalendar.setTime(dateFormatter.parse(startDate));
            startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
            startDateCalendar.set(Calendar.MINUTE, 0);
            startDateCalendar.set(Calendar.SECOND, 0);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        startDateCalendar.add(Calendar.DATE, position);
        if(startDateCalendar.compareTo(now) < 0)
            editButton.setVisibility(View.INVISIBLE);
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),Routing.class);
                i.putExtra("itineraryId",itineraryIds.get(position));
                i.putStringArrayListExtra("placeNames", placeNamesPerItinerary.get(itineraryIds.get(position)));
                i.putStringArrayListExtra("latitudes", latitudesPerItinerary.get(itineraryIds.get(position)));
                i.putStringArrayListExtra("longitudes", longitudesPerItinerary.get(itineraryIds.get(position)));
                startActivity(i);

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(),ItineraryEdit.class);
                i.putExtra("itineraryId",itineraryIds.get(position));
                i.putStringArrayListExtra("placeIds", placeIdsPerItinerary.get(itineraryIds.get(position)));
                i.putStringArrayListExtra("placeNames", placeNamesPerItinerary.get(itineraryIds.get(position)));
                i.putStringArrayListExtra("open", openPerItinerary.get(itineraryIds.get(position)));
                i.putStringArrayListExtra("close", closePerItinerary.get(itineraryIds.get(position)));
                startActivity(i);

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.detach(TabFragment.this).attach(TabFragment.this).commit();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        System.out.println("worked");
    }

}
