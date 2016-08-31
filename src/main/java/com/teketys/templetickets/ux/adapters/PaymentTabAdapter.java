package com.teketys.templetickets.ux.adapters;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.teketys.templetickets.ux.fragments.CCAvenueFragment;

public class PaymentTabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PaymentTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CCAvenueFragment tab1 = new CCAvenueFragment();
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
