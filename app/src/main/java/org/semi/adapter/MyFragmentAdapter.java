package org.semi.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> titleList;

    public MyFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    public MyFragmentAdapter addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
        return this;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }


}
