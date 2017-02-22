package com.pickth.comepennyrenewal.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Kim on 2017-01-30.
 */

public class FABHideOnScroll extends FloatingActionButton.Behavior {
    public FABHideOnScroll(Context context,AttributeSet attrs) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        /**
        if(child.getVisibility() == View.VISIBLE && dyConsumed > 0){
            child.hide();
        }else if(child.getVisibility() == View.GONE && dyConsumed < 0){
            child.show();
        }
         */

        if(dyConsumed > 0){
            // 밑으로 내릴 때
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int fab_bottomMargin = layoutParams.bottomMargin;
            // 20은 Jelly bean 이상일 때 home touch button 사이즈만큼 내리는거
            child.animate().translationY(child.getHeight() + fab_bottomMargin + 20).setInterpolator(new LinearInterpolator()).start();
        }else if(dyConsumed <0){
            // 위로 올릴 때
            child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    /**
     * FAB를 RecyclerView에 의존하도록
     * @param parent
     * @param child
     * @param dependency
     * @return
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if(dependency instanceof RecyclerView)
            return true;

        return false;
    }
}
