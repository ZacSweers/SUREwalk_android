package org.utexas.surewalk.controllers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private final ViewPager mViewPager;
        private final ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
        private SherlockFragmentActivity mParent;

        public FragmentAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
            mParent = activity;
        }
        

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getArguments().getString("title");
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            final InputMethodManager imm = (InputMethodManager) mParent.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mViewPager.getWindowToken(), 0);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
   }