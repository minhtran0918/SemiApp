package org.semi.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonScrollingViewPager extends ViewPager {
    private boolean isPagingEnabled = true;
    public NonScrollingViewPager(@NonNull Context context) {
        super(context);
    }

    public NonScrollingViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isPagingEnabled && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isPagingEnabled && super.onInterceptTouchEvent(ev);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}
