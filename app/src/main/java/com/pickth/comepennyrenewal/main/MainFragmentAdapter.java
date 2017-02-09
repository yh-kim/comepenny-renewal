package com.pickth.comepennyrenewal.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.booth.BoothFragment;
import com.pickth.comepennyrenewal.idea.IdeaFragment;

/**
 * 메인 어답터
 * Created by Kim on 2016-11-11.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"내 정보", "쿠폰"};
    private int switch_Icons[] = {R.drawable.selector_tab_homo, R.drawable.selector_tab_category};
    private Fragment[] fragments = new Fragment[]{IdeaFragment.newInstance(), BoothFragment.newInstance()};

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
    */
}
