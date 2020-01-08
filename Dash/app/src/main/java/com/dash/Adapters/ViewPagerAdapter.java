package com.dash.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dash.Fragments.DashFragment;
import com.dash.Fragments.RedditFragment;
import com.dash.Fragments.TrendsFragment;
import com.dash.Fragments.TwitterFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final Fragment[] mChildFragments;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mChildFragments = new Fragment[]{
                new DashFragment(),     //0
                new RedditFragment(),   //1
                new TwitterFragment(),  //2
                new TrendsFragment()    //3
        };
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mChildFragments[position];
    }

    @Override
    public int getCount() {
        return mChildFragments.length; //three fragments
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".") + 1, title.length() - 8);
    }
}
