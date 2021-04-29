package com.example.smart_control.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.smart_control.ui.user.fragment.OtomasiFragment;
import com.example.smart_control.ui.user.fragment.RunFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
// Top Rated fragment activity
                return new OtomasiFragment();
            case 1:
// Games fragment activity
                return new RunFragment();
            case 2:
// Movies fragment activity
                return new RunFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
// get item count - equal to number of tabs
        return 3;
    }

}