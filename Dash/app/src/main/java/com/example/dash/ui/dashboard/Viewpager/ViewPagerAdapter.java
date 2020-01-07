package com.example.dash.ui.dashboard.Viewpager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.dash.ui.dashboard.Viewpager.Fragments.*;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final Fragment[] childFragments;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        childFragments = new Fragment[]{
                new DashFragment(),     //0
                new RedditFragment(),   //1
                new TwitterFragment(),  //2
                new TrendsFragment()    //3
        };
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return childFragments[position];
    }

    @Override
    public int getCount() {
        return childFragments.length; //three fragments
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".") + 1, title.length() - 8);
    }
}
