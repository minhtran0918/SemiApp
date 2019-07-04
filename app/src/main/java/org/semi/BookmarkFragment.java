package org.semi;


import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.semi.fragment.BookmarkAllFragment;
import org.semi.fragment.BookmarkCollectionFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {

    private static BookmarkFragment mInstance;
    private View mRootView;
    private ViewPager mRootViewPager;
    private TabLayout mRootTabLayout;

    public static BookmarkFragment getInstance() {
        if (mInstance == null) {
            mInstance = new BookmarkFragment();
        }
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_bookmark, container, false);
        mRootTabLayout = mRootView.findViewById(R.id.tablayout_bookmark_menu);
        mRootViewPager = mRootView.findViewById(R.id.viewpager_bookmark);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPager();
    }

    private void setupViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(BookmarkAllFragment.getInstance(), "Tất cả");
        viewPagerAdapter.addFragment(BookmarkCollectionFragment.getInstance(), "Bộ sưu tập");

        mRootViewPager.setAdapter(viewPagerAdapter);
        mRootTabLayout.setupWithViewPager(mRootViewPager);
        mRootTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mRootViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
}
