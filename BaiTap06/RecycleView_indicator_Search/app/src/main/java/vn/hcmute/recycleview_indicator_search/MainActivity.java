package vn.hcmute.recycleview_indicator_search;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.recycleview_indicator_search.adapters.IconAdapter;
import vn.hcmute.recycleview_indicator_search.models.IconModel;
import vn.hcmute.recycleview_indicator_search.LinePagerIndicatorDecoration;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rcIcon;
    private IconAdapter iconAdapter;
    private ArrayList<IconModel> arrayList1;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rcIcon = findViewById(R.id.rcIcon);
        arrayList1 = new ArrayList<>();

        arrayList1.add(new IconModel(R.drawable.icon1, "jfdjfdjf djfdh"));
        arrayList1.add(new IconModel(R.drawable.icon2, "sdfdf sfdsf"));
        arrayList1.add(new IconModel(R.drawable.icon3, "sdfdf sfds"));
        arrayList1.add(new IconModel(R.drawable.icon4, "dfgfhyh sxdff"));
        arrayList1.add(new IconModel(R.drawable.icon5, "jfdjfdjf djfdh"));
        arrayList1.add(new IconModel(R.drawable.icon6, "sdfdf sfdsf"));
        arrayList1.add(new IconModel(R.drawable.icon7, "sdfdf sfds"));
        arrayList1.add(new IconModel(R.drawable.icon8, "dfgfhyh sxdff"));
        arrayList1.add(new IconModel(R.drawable.icon9, "dfgfhyh sxdff"));
        arrayList1.add(new IconModel(R.drawable.icon1, "jfdjfdjf djfdh"));
        arrayList1.add(new IconModel(R.drawable.icon2, "sdfdf sfdsf"));
        arrayList1.add(new IconModel(R.drawable.icon3, "sdfdf sfds"));
        arrayList1.add(new IconModel(R.drawable.icon4, "dfgfhyh sxdff"));
        arrayList1.add(new IconModel(R.drawable.icon5, "jfdjfdjf djfdh"));
        arrayList1.add(new IconModel(R.drawable.icon6, "sdfdf sfdsf"));
        arrayList1.add(new IconModel(R.drawable.icon7, "sdfdf sfds"));
        arrayList1.add(new IconModel(R.drawable.icon8, "dfgfhyh sxdff"));
        arrayList1.add(new IconModel(R.drawable.icon9, "dfgfhyh sxdff"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        rcIcon.setLayoutManager(linearLayoutManager);
        iconAdapter = new IconAdapter(getApplicationContext(), arrayList1);
        rcIcon.setAdapter(iconAdapter);
        rcIcon.addItemDecoration(new LinePagerIndicatorDecoration());

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search here...");
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterListener(newText);
                return true;
            }
        });
    }
    private void filterListener(String text) {
        List<IconModel> list = new ArrayList<>();
        for (IconModel iconModel: arrayList1) {
            if(iconModel.getDesc().toLowerCase().contains(text.toLowerCase())){
                list.add(iconModel);
            }
        }

        if(list.isEmpty()){
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
        } else {
            iconAdapter.setListenerList(list);
        }
    }
}
