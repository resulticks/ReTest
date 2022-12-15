package io.mob.resu.reandroidsdk;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RePagerAdapter extends FragmentPagerAdapter {
    List<Fragment> mFragmentCollection = new ArrayList<>();
    List<String> mTitleCollection = new ArrayList<>();

    public RePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(String title, Fragment fragment) {
        try {
            mTitleCollection.add(title);
            mFragmentCollection.add(fragment);
        } catch (Exception e) {

        }

    }

    //Needed for
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleCollection.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentCollection.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentCollection.size();
    }
}
