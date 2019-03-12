package com.example.avery.chamberofwizards.Questions.QuestionsFragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.avery.chamberofwizards.Questions.QuestionsPagerAdapter;
import com.example.avery.chamberofwizards.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionsHomeTabFragment extends Fragment {

    private View mView;

    private TabLayout questionsTabLayout;
    private ViewPager questionsViewPager;

    private Fragment mathFragment;
    private Fragment compScieFragment;
    private Fragment bioFragment;
    private Fragment businessFragment;
    private Fragment enviScieFragment;

    public QuestionsHomeTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_questions_home_tab, container, false);

        questionsTabLayout = mView.findViewById(R.id.questionsTabLayout);
        questionsViewPager = mView.findViewById(R.id.questionsViewPager);

        mathFragment = new MathematicsFragment();
        compScieFragment = new ComputerScienceFragment();
        bioFragment = new BiologyFragment();
        businessFragment = new BusinessFragment();
        enviScieFragment = new EnvironmentalScienceFragment();

       //questionsTabLayout.setupWithViewPager(questionsViewPager);

        setUpViewPager();

        return mView;
    }

    public void setUpViewPager() {
        /*
        TabLayout.Tab mathematicsFragment = questionsTabLayout.newTab();
        TabLayout.Tab computerScienceFragment = questionsTabLayout.newTab();
        TabLayout.Tab biologyFragment = questionsTabLayout.newTab();

        //Paglagay ng title sa mga tabs
        mathematicsFragment.setText("Mathematics");
        computerScienceFragment.setText("Computer Science");
        biologyFragment.setText("Biology");

        //Paglagay ng mga tabs sa tablayout, yung second parameter, yung position
        questionsTabLayout.addTab(mathematicsFragment, 0);
        questionsTabLayout.addTab(computerScienceFragment, 1);
        questionsTabLayout.addTab(biologyFragment, 2);

        questionsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(questionsTabLayout));
        */

        QuestionsPagerAdapter questionsPagerAdapter = new QuestionsPagerAdapter(getChildFragmentManager());
        questionsPagerAdapter.addFragment(mathFragment, "");
        questionsPagerAdapter.addFragment(businessFragment, "");
        questionsPagerAdapter.addFragment(compScieFragment, "");
        questionsPagerAdapter.addFragment(bioFragment, "");
        questionsPagerAdapter.addFragment(enviScieFragment, "");

        questionsViewPager.setAdapter(questionsPagerAdapter);

        questionsTabLayout.setupWithViewPager(questionsViewPager);

        questionsTabLayout.getTabAt(0).setIcon(R.drawable.math_tab_icon);
        questionsTabLayout.getTabAt(1).setIcon(R.drawable.business_tab_icon);
        questionsTabLayout.getTabAt(2).setIcon(R.drawable.cs_tab_icon);
        questionsTabLayout.getTabAt(3).setIcon(R.drawable.bio_tab_icon);
        questionsTabLayout.getTabAt(4).setIcon(R.drawable.nature_tab_icon);
    }
}
