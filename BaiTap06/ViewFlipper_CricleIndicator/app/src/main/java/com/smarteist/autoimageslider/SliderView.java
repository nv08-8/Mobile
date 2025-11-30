package com.smarteist.autoimageslider;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

/**
 * Compatibility shim for com.smarteist.autoimageslider.SliderView.
 * This wraps a ViewPager2 and provides minimal API used by the app.
 * It intentionally implements only the methods the project calls, as no-op or basic functionality.
 */
public class SliderView extends FrameLayout {

    public static final int AUTO_CYCLE_DIRECTION_RIGHT = 1;
    public static final int AUTO_CYCLE_DIRECTION_LEFT = 0;

    private final ViewPager2 viewPager;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private int scrollTimeInSec = 3;
    private boolean isAutoCycling = false;
    private int autoCycleDirection = AUTO_CYCLE_DIRECTION_RIGHT;

    private final Runnable autoRunnable = new Runnable() {
        @Override
        public void run() {
            RecyclerView.Adapter<?> adapter = viewPager.getAdapter();
            if (adapter != null && adapter.getItemCount() > 0) {
                int current = viewPager.getCurrentItem();
                int count = adapter.getItemCount();
                int next;
                if (autoCycleDirection == AUTO_CYCLE_DIRECTION_RIGHT) {
                    next = (current + 1) % count;
                } else {
                    next = (current - 1 + count) % count;
                }
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, scrollTimeInSec * 1000L);
            } else {
                // retry later
                handler.postDelayed(this, scrollTimeInSec * 1000L);
            }
        }
    };

    public SliderView(@NonNull Context context) {
        this(context, null);
    }

    public SliderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        viewPager = new ViewPager2(context);
        viewPager.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        addView(viewPager);
    }

    /**
     * Set adapter used by the inner ViewPager2. Accept any Object and cast internally to avoid generic mismatch.
     */
    public void setSliderAdapter(Object adapter) {
        if (adapter instanceof RecyclerView.Adapter) {
            // unchecked cast but safe for runtime
            viewPager.setAdapter((RecyclerView.Adapter) adapter);
        } else {
            throw new IllegalArgumentException("Adapter must be instance of RecyclerView.Adapter");
        }
    }

    // No-op: external users may set an indicator animation type
    public void setIndicatorAnimation(int animationType) {
    }

    // No-op: external users may set a transform
    public void setSliderTransformAnimation(int transformType) {
    }

    public void setAutoCycleDirection(int direction) {
        this.autoCycleDirection = direction;
    }

    public void setIndicatorSelectedColor(int color) {
    }

    public void setIndicatorUnselectedColor(int color) {
    }

    public void startAutoCycle() {
        if (!isAutoCycling) {
            isAutoCycling = true;
            handler.postDelayed(autoRunnable, scrollTimeInSec * 1000L);
        }
    }

    public void stopAutoCycle() {
        if (isAutoCycling) {
            isAutoCycling = false;
            handler.removeCallbacks(autoRunnable);
        }
    }

    public void setScrollTimeInSec(int sec) {
        if (sec > 0) {
            scrollTimeInSec = sec;
            if (isAutoCycling) {
                // restart with new interval
                handler.removeCallbacks(autoRunnable);
                handler.postDelayed(autoRunnable, scrollTimeInSec * 1000L);
            }
        }
    }

    public int getScrollTimeInSec() {
        return scrollTimeInSec;
    }

    // Delegate some methods commonly used
    public int getCurrentItem() {
        return viewPager.getCurrentItem();
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }

}
