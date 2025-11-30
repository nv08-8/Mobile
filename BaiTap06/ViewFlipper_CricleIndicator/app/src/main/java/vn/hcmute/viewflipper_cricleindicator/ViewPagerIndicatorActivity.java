package vn.hcmute.viewflipper_cricleindicator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import vn.hcmute.viewflipper_cricleindicator.adapters.ImagesViewPagerAdapter;
import vn.hcmute.viewflipper_cricleindicator.models.Images;

public class ViewPagerIndicatorActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ImagesViewPagerAdapter adapter;
    private CircleIndicator circleIndicator;

    private ViewFlipper viewFlipper;

    private List<Images> imagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_indicator);
        viewPager = findViewById(R.id.viewpage);
        circleIndicator = findViewById(R.id.circle_indicator);
        imagesList = getListImages();
        adapter = new ImagesViewPagerAdapter(imagesList);
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);

        //goi runnable
        handler.postDelayed(runnable, 3000);
        //lang nghe viewpager chuyen trang
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled ( int position, float positionOffset, int positionOffsetPixels){
            }
            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    private List<Images> getListImages() {
        List<Images> list = new ArrayList<>();
        list.add(new Images(R.drawable.quangcao));
        list.add(new Images(R.drawable.coffee));
        list.add(new Images(R.drawable.companypizza));
        list.add(new Images(R.drawable.themoingon));
        return list;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getCurrentItem() == imagesList.size()-1) {
                viewPager.setCurrentItem(0);
            }
            else {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
        }
    };
    private final Handler handler = new Handler(Looper.getMainLooper());


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