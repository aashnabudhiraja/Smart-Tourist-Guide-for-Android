package com.major.touristguide;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class TabFragment extends Fragment implements AdapterView.OnItemClickListener {

    int position;
    static List<String> itineraryIds;
    Firebase reference1,reference2;
    //private TextView textView;
    Button routeButton;

    ListView listView;
    List<RowItem> rowItems;
    final List<List<String>> placesList = new ArrayList<>();

    public static Fragment getInstance(int position, List<String> itineraryId) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        itineraryIds= itineraryId;

        System.out.println("ite" + itineraryIds);

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
        //textView = (TextView) view.findViewById(R.id.textView);

        //textView.setText("Fragment " + (position + 1));
        Firebase.setAndroidContext(view.getContext());
        rowItems = new ArrayList<>();

        reference1 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/itinerary");

        reference1.child(itineraryIds.get(position)).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("snapshot"+dataSnapshot.getValue());
                Map map = dataSnapshot.getValue(Map.class);
                final List<String>placeIds = (ArrayList<String>)map.get("placesId");

                reference2 = new Firebase("https://tourist-guide-fd1e1.firebaseio.com/place");

                System.out.println("places"+placeIds);
                for(int i=0;i<placeIds.size();i++) {
                    final int j=i;
//                    StringRequest request1 = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String s) {
//                            try {
//
//                                JSONObject obj1 = new JSONObject(s);
//                                System.out.println("object"+obj1);
//                                String placeName = obj1.getJSONObject(placeIds.get(j)).getString("placeName");
//
//                                rowItems.add(new RowItem(R.drawable.ic_launcher_background, placeName, "Chandigarh Popular Places to visit"));
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError volleyError) {
//                            System.out.println("" + volleyError);
//                        }
//                    });
//
//                    RequestQueue rQueue1 = Volley.newRequestQueue(getContext());
//                    rQueue1.add(request1);
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

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        System.out.println("position"+position);
        listView = (ListView) view.findViewById(R.id.list);
        routeButton=(Button) view.findViewById(R.id.route);
        listView.setOnItemClickListener(this);

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getContext(),Home.class);
                i.putExtra("itineraryId",itineraryIds.get(position));
                startActivity(i);

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        System.out.println("worked");
    }
}
