package vn.hcmute.viewflipper_cricleindicator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

import vn.hcmute.viewflipper_cricleindicator.R;

public class AutoImagesSliderAdapter extends SliderViewAdapter<AutoImagesSliderAdapter.SliderAdapterVH> {

    private final Context context;
    private final ArrayList<Integer> data;

    public AutoImagesSliderAdapter(Context context, ArrayList<Integer> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        int resId = data.get(position);
        Glide.with(viewHolder.itemView.getContext()).load(resId).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    public static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}

