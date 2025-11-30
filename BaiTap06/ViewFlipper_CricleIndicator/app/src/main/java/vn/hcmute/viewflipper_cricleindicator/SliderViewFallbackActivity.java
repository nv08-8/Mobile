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

public class SliderViewFallbackActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private ImagesViewPager2Adapter adapter;
    private CircleIndicator3 indicator;
    private List<Images> imagesList;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (imagesList == null || imagesList.isEmpty()) return;
            int current = viewPager2.getCurrentItem();
            if (current == imagesList.size() - 1) viewPager2.setCurrentItem(0);
            else viewPager2.setCurrentItem(current + 1);
            handler.postDelayed(this, 3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_view_fallback);

        viewPager2 = findViewById(R.id.viewPagerSlider);
        indicator = findViewById(R.id.indicatorSlider);

        imagesList = new ArrayList<>();
        imagesList.add(new Images(R.drawable.shoppe1));
        imagesList.add(new Images(R.drawable.shoppe2));
        imagesList.add(new Images(R.drawable.shoppe3));
        imagesList.add(new Images(R.drawable.shoppe4));

        adapter = new ImagesViewPager2Adapter(imagesList);
        viewPager2.setAdapter(adapter);
        indicator.setViewPager(viewPager2);

        handler.postDelayed(runnable, 3000);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });
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
}

