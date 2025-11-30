package vn.hcmute.viewflipper_cricleindicator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.hcmute.viewflipper_cricleindicator.R;
import vn.hcmute.viewflipper_cricleindicator.models.Images;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapterHolder> {

    private final List<Images> imageList;

    public SliderAdapter(List<Images> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public SliderAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new SliderAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapterHolder holder, int position) {
        Images images = imageList.get(position);
        if (images == null) return;
        Glide.with(holder.imageView.getContext()).load(images.getImagesId()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList != null ? imageList.size() : 0;
    }
}
