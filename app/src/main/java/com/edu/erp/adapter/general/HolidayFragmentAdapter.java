package com.edu.erp.adapter.general;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.edu.erp.fragments.AllHolidayListFragment;
import com.edu.erp.fragments.UpcomingHolidayListFragment;

public class HolidayFragmentAdapter extends FragmentStatePagerAdapter {

    public HolidayFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UpcomingHolidayListFragment();
            case 1:
                return new AllHolidayListFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
