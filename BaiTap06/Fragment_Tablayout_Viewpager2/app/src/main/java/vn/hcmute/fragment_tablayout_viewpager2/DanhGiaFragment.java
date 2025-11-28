package vn.hcmute.fragment_tablayout_viewpager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.hcmute.fragment_tablayout_viewpager2.databinding.FragmentDanhGiaBinding;

public class DanhGiaFragment extends Fragment {

    private FragmentDanhGiaBinding binding;

    public DanhGiaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDanhGiaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
