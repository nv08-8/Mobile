package vn.hcmute.viewflipper_cricleindicator.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import vn.hcmute.viewflipper_cricleindicator.R;

public class SliderAdapterHolder extends RecyclerView.ViewHolder {
    public final ImageView imageView;

    public SliderAdapterHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imgView);
    }
}

