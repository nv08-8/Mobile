package vn.hcmute.viewflipper_cricleindicator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ImagesViewPageAdapter extends PagerAdapter {

    private List<Images> imageList;
    public ImagesViewPageAdapter(List<Images>imagesList) {
        this.imageList = imagesList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_image, container, false);
        ImageView imageView = view.findViewById(R.id.imgView);
        Images images = imageList.get(position);
        imageView.setImageResource(images.getImagesId());
        container.addView(view);
        return view;
    }
    @Override
    public int getCount() {
        if (imageList != null) {
            return imageList.size();
        }
        return 0;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

