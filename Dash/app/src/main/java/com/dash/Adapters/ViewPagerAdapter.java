/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Furthermore this project is licensed under the firebase.google.com/terms and
 * firebase.google.com/terms/crashlytics.
 *
 */

package com.dash.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dash.Fragments.DashFragment;
import com.dash.Fragments.RedditFragment;
import com.dash.Fragments.TrendsFragment;
import com.dash.Fragments.TwitterFragment;

/**
 * Create and sort the Fragments in the Tablayout
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final Fragment[] mChildFragments;

    /**
     * Set the different fragments in the tablayout
     *
     * @param fragmentManager manager for the fragments
     */
    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mChildFragments = new Fragment[]{
                new DashFragment(),     //0
                new RedditFragment(),   //1
                new TwitterFragment(),  //2
                new TrendsFragment()    //3
        };
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position position of the fragment
     * @return the fragments associated with the specified position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mChildFragments[position];
    }

    /**
     * Return the number of views available.
     *
     * @return the number of fragments
     */
    @Override
    public int getCount() {
        return mChildFragments.length; //three fragments
    }

    /**
     * Return a title string to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page where the last the last eight characters, "Fragment",
     * are cut of the title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        String title = getItem(position).getClass().getName();
        return title.subSequence(title.lastIndexOf(".") + 1, title.length() - 8);
    }
}
