package com.major.touristguide;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    private String title[] ;
    private List<String> itineraryIds;

    public ViewPagerAdapter(FragmentManager manager, String[] title, List<String> itineraryIds) {
        super(manager);
        this.title = title;
        this.itineraryIds = itineraryIds;
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragment.getInstance(position,itineraryIds);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
