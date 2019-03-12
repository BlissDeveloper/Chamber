package com.example.avery.chamberofwizards.Books.BookFragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Books.BookPagerAdapter;
import com.example.avery.chamberofwizards.Books.SectionsPagesAdapterBook;
import com.example.avery.chamberofwizards.Main3Activity;
import com.example.avery.chamberofwizards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment  {

    private View mView;
    private TabLayout tabLayout;
    private SectionsPagesAdapterBook sectionsPagesAdapterBook;

    private ViewPager mViewPager;


    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_tab, container, false);

        setRetainInstance(true);

        tabLayout = mView.findViewById(R.id.tabLayout);
        mViewPager = mView.findViewById(R.id.bookViewPager);
        setUpViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

        sectionsPagesAdapterBook = new SectionsPagesAdapterBook(getChildFragmentManager());

        return mView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        tabLayout.setupWithViewPager(mViewPager);
        sectionsPagesAdapterBook = new SectionsPagesAdapterBook(getChildFragmentManager());

    }

    public void setUpViewPager(ViewPager viewPager)
    {
        SectionsPagesAdapterBook adapterBook = new SectionsPagesAdapterBook(getChildFragmentManager());
        adapterBook.addFragment(new ReadFragment(), "MOST POPULAR");
        adapterBook.addFragment(new MostDiscussed(), "TOP RATED");
        adapterBook.addFragment(new LatestFragment(), "LATEST");
        adapterBook.addFragment(new FavoritesFragment(), "YOUR FAVORITES");
        viewPager.setAdapter(adapterBook);
    }


}
