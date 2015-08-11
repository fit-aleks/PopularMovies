package com.fitaleks.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderkulikovskiy on 07.07.15.
 */
public class MoviesFragment extends Fragment {

    @Bind(R.id.friendPager)
    ViewPager viewPager;
    @Bind(R.id.friendSlideTab)
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_all_movies, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.MOVIES), getString(R.string.fragment_title_movies));
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.TV_SERIES), getString(R.string.fragment_title_tv));
        adapter.addFragment(MoviesListFragment.newInstance(MoviesListFragment.FragmentListTypes.FAVOURITES), getString(R.string.fragment_title_favourite));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


}
