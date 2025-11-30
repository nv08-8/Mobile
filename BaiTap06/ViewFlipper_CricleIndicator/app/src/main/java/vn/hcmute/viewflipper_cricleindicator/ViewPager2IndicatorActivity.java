package vn.hcmute.viewflipper_cricleindicator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import me.relex.circleindicator.CircleIndicator3;
import vn.hcmute.viewflipper_cricleindicator.adapters.ImagesViewPager2Adapter;
import vn.hcmute.viewflipper_cricleindicator.models.Images;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2IndicatorActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ImagesViewPager2Adapter sliderAdapter;
    private CircleIndicator3 circleIndicator3;
    private List<Images> imagesList;

    // Auto-scroll
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (imagesList == null || imagesList.isEmpty()) return;
            int current = viewPager2.getCurrentItem();
            if (current == imagesList.size() - 1) {
                viewPager2.setCurrentItem(0);
            } else {
                viewPager2.setCurrentItem(current + 1);
            }
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the ViewPager2 layout and IDs present in the project
        setContentView(R.layout.activity_view_pager2);

        viewPager2 = findViewById(R.id.viewPager);
        circleIndicator3 = findViewById(R.id.indicator);

        imagesList = getListImages();
        sliderAdapter = new ImagesViewPager2Adapter(imagesList);
        viewPager2.setAdapter(sliderAdapter);

        // Attach indicator to ViewPager2
        circleIndicator3.setViewPager(viewPager2);

        // Start auto-scroll
        handler.postDelayed(runnable, 3000);

        // Reset auto-scroll when user manually changes page
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });

        // Optional: page transformer for nicer animation
        viewPager2.setPageTransformer(new DepthPageTransformer());
    }

    private List<Images> getListImages() {
        List<Images> list = new ArrayList<>();
        list.add(new Images(R.drawable.quangcao));
        list.add(new Images(R.drawable.coffee));
        list.add(new Images(R.drawable.companypizza));
        list.add(new Images(R.drawable.themoingon));
        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    // DepthPageTransformer taken/adjusted from Android docs for ViewPager2
    public static class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(android.view.View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            } else if (position <= 1) { // (0,1]
                view.setAlpha(1 - position);
                view.setTranslationX(pageWidth * -position);
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                view.setAlpha(0f);
            }
        }
    }

    // ZoomOutPageTransformer (alternative) adapted for ViewPager2 — matches slide example
    public static class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(android.view.View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                view.setAlpha(0f);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                float alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA);
                view.setAlpha(alpha);
            } else { // (1,+Infinity]
                view.setAlpha(0f);
            }
        }
    }
}
