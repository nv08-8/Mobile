package vn.hcmute.viewflipper_cricleindicator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnViewFlipper = findViewById(R.id.btnViewFlipper);
        Button btnViewPager = findViewById(R.id.btnViewPager);
        Button btnViewPager2 = findViewById(R.id.btnViewPager2);
        Button btnSliderView = findViewById(R.id.btnSliderView);

        btnViewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewFlipperActivity.class));
            }
        });

        btnViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewPagerIndicatorActivity.class));
            }
        });

        btnViewPager2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViewPager2IndicatorActivity.class));
            }
        });

        btnSliderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SliderViewActivity.class));
            }
        });
    }
}
