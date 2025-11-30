package com.smarteist.autoimageslider;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Compatibility shim for com.smarteist.autoimageslider.SliderViewAdapter.
 *
 * Provides the small subset of the original library API used by this project:
 * - an abstract onCreateViewHolder(ViewGroup) without viewType
 * - the normal onBindViewHolder(ViewHolder, int) to be implemented by subclasses
 * - getCount() which is bridged to RecyclerView's getItemCount()
 *
 * This class delegates the RecyclerView required signatures to the simpler abstract
 * methods so existing adapter code (written against the library) can remain unchanged.
 */
public abstract class SliderViewAdapter<VH extends SliderViewAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {

    // Subclasses (your adapters) implement this simpler creation method
    public abstract VH onCreateViewHolder(ViewGroup parent);

    @Override
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent);
    }

    // Subclasses must implement binding logic (same signature as RecyclerView.Adapter)
    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        // Forward payload-less bind to the simple override
        onBindViewHolder(holder, position);
    }

    // Library used getCount(); bridge it to RecyclerView's getItemCount()
    public abstract int getCount();

    @Override
    public final int getItemCount() {
        int c = getCount();
        return c >= 0 ? c : 0;
    }

    // Simple ViewHolder compatible type
    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

