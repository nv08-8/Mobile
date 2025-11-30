package vn.hcmute.viewflipper_cricleindicator;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ViewFlipperActivity extends AppCompatActivity {
    ViewFlipper viewFlipperMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flipper);
        viewFlipperMain = findViewById(R.id.viewFlipperMain);
        ActionViewFlipperMain();
    }

    //hàm Flipper
    private void ActionViewFlipperMain() {
        // Use local drawables instead of remote URLs to avoid network 404 errors during demo
        List<Integer> arrayListFlipper = new ArrayList<>();
        arrayListFlipper.add(R.drawable.quangcao);
        arrayListFlipper.add(R.drawable.coffee);
        arrayListFlipper.add(R.drawable.companypizza);
        arrayListFlipper.add(R.drawable.themoingon);
        for (int i = 0; i < arrayListFlipper.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            // ensure image view fills parent so Glide doesn't interpret WRAP_CONTENT as device-size request
            ViewFlipper.LayoutParams lp = new ViewFlipper.LayoutParams(
                    ViewFlipper.LayoutParams.MATCH_PARENT,
                    ViewFlipper.LayoutParams.MATCH_PARENT
            );
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            // load local resource id with an explicit override to a reasonable size to avoid large memory requests
            Glide.with(getApplicationContext())
                    .load(arrayListFlipper.get(i))
                    .override(1080, 420)
                    .into(imageView);
            viewFlipperMain.addView(imageView);
        }
        viewFlipperMain.setFlipInterval(3000);
        viewFlipperMain.setAutoStart(true);
        //thiết lập animation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipperMain.setInAnimation(slide_in);
        viewFlipperMain.setOutAnimation(slide_out);
    }
}
