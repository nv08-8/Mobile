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

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewPager2ViewHolder> {

    private List<Images> imageList;

    public ViewPager2Adapter(List<Images> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ViewPager2ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewPager2ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPager2ViewHolder holder, int position) {
        Images images = imageList.get(position);
        if (images == null) {
            return;
        }
        Glide.with(holder.imageView.getContext()).load(images.getImagesId()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if (imageList != null) {
            return imageList.size();
        }
        return 0;
    }

    public class ViewPager2ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewPager2ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}
