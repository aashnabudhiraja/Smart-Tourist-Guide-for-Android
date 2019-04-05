package com.major.touristguide.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.major.touristguide.R;
import com.major.touristguide.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItineraryActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> itineraryIds;
    private String days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Itinerary");

        Bundle extras = getIntent().getExtras();
        itineraryIds = (ArrayList<String>)getIntent().getSerializableExtra("itineraryIds");
        System.out.println("array"+itineraryIds);
        days = extras.getString("days");
        System.out.println("itinerary" +extras.getString("days"));

         int day;
        if (!TextUtils.isEmpty(days) && TextUtils.isDigitsOnly(days)) {
            day = Integer.parseInt(days);
        } else {
            day = 0;
        }
        String title[] = new String[day];
        for(int i=1;i<=day;i++)
            title[i-1]="Day"+i;

        String startDate = extras.getString("startDate");
        System.out.println("Itinerary" + startDate);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),title,itineraryIds, startDate);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(ItineraryActivity.this, MainHome.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }


}
