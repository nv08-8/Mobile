package vn.hcmute.recycleview_indicator_search.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.hcmute.recycleview_indicator_search.R;
import vn.hcmute.recycleview_indicator_search.models.IconModel;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconHolder> {
    private Context context;
    private List<IconModel> arrayList;
    public IconAdapter(Context context,List<IconModel> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_icon_promotion,parent,false);
        return new IconHolder(view);
    }
    @Override
    public void onBindViewHolder(IconHolder holder, int position) {

        holder.ivImgIcon.setImageResource(arrayList.get(position).getImgId());
        holder.tvIcon.setText(arrayList.get(position).getDesc());
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class IconHolder extends RecyclerView.ViewHolder {
        ImageView ivImgIcon;
        TextView tvIcon;
        public IconHolder(View itemView) {
            super(itemView);
            ivImgIcon = itemView.findViewById(R.id.ivImgIcon);
            tvIcon = itemView.findViewById(R.id.tvIcon);
        }
    }
    public void setListenerList (List<IconModel> list){
        this.arrayList = list;
        notifyDataSetChanged();
    }
}
