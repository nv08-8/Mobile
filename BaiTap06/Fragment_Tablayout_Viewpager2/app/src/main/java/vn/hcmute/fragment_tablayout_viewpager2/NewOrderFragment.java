package vn.hcmute.fragment_tablayout_viewpager2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vn.hcmute.fragment_tablayout_viewpager2.databinding.FragmentNewOrderBinding;

public class NewOrderFragment extends Fragment {

    private FragmentNewOrderBinding binding;

    public NewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewOrderBinding.inflate(inflater, container, false);
        // Vị trí load dữ liệu: Adapter, RecyclerView,...
        return binding.getRoot();
    }
}
