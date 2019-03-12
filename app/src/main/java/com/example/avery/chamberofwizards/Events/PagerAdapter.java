package com.example.avery.chamberofwizards.Events;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.avery.chamberofwizards.Events.EventsFragments.BlankFragment;
import com.example.avery.chamberofwizards.Events.EventsFragments.MathSocEventsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                BlankFragment collegeOfScienceEventsFragment = new BlankFragment();
                return collegeOfScienceEventsFragment;
            case 1:
                MathSocEventsFragment mathSocEventsFragment = new MathSocEventsFragment();
                return mathSocEventsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
