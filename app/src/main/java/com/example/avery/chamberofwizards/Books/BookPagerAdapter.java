package com.example.avery.chamberofwizards.Books;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.avery.chamberofwizards.Books.BookFragments.LatestFragment;
import com.example.avery.chamberofwizards.Books.BookFragments.MostDiscussed;
import com.example.avery.chamberofwizards.Books.BookFragments.ReadFragment;

public class BookPagerAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public BookPagerAdapter(FragmentManager fm, int NumberOfTabs) {

        super(fm);
        this.noOfTabs = NumberOfTabs;


    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                ReadFragment readFragment = new ReadFragment();
                return readFragment;

            case 1:
                MostDiscussed mostDiscussed = new MostDiscussed();
                return mostDiscussed;

            case 2:
                LatestFragment latestFragment = new LatestFragment();
                return latestFragment;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 0;
    }
}
