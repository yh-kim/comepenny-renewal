package com.pickth.comepennyrenewal.myinfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pickth.comepennyrenewal.R;
import com.pickth.comepennyrenewal.myinfo.fragment.MyInfoLikeFragement;
import com.pickth.comepennyrenewal.myinfo.fragment.MyInfoWriteFragement;

/**
 * 메인 어답터
 * Created by Kim on 2016-11-11.
 */

public class MyInfoFragmentAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private int switch_Icons[] = {R.drawable.selector_tab_homo, R.drawable.selector_tab_category};
    private Fragment[] fragments = new Fragment[]{MyInfoWriteFragement.newInstance(), MyInfoLikeFragement.newInstance()};

    public MyInfoFragmentAdapter(FragmentManager fm) {
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
}
