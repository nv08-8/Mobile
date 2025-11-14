package vn.hcmute.baitap02_1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
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

        ConstraintLayout bg = findViewById(R.id.main);
        ImageButton img2 = findViewById(R.id.imageButton1);
        Switch sw = findViewById(R.id.switch1);
        CheckBox ck1 = findViewById(R.id.checkBox);
        RadioGroup radioGroup = findViewById(R.id.radioGroup1);
        SeekBar seekBar = findViewById(R.id.seekBar);

        ArrayList<Integer> backgrounds = new ArrayList<>();
        backgrounds.add(R.drawable.bg1);
        backgrounds.add(R.drawable.bg2);
        backgrounds.add(R.drawable.bg3);
        backgrounds.add(R.drawable.bg4);
        int index = new Random().nextInt(backgrounds.size());
        bg.setBackgroundResource(backgrounds.get(index));

        img2.setOnClickListener(v -> ShowPopupMenu());

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bg.setBackgroundResource(R.drawable.bg1);
            } else {
                bg.setBackgroundResource(R.drawable.bg2);
            }
        });

        ck1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                bg.setBackgroundResource(R.drawable.bg3);
            } else {
                bg.setBackgroundResource(R.drawable.bg4);
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButton) {
                bg.setBackgroundResource(R.drawable.bg3);
            } else if (checkedId == R.id.radioButton2) {
                bg.setBackgroundResource(R.drawable.bg4);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("AAA", "Giá trị:" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("AAA", "Start");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("AAA", "Stop");
            }
        });
    }

    private void ShowPopupMenu() {
        ImageButton btnButton = findViewById(R.id.imageButton1);
        PopupMenu popupMenu = new PopupMenu(this, btnButton);
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuSetting) {
                Toast.makeText(MainActivity.this, "Bạn đang chọn Setting", Toast.LENGTH_LONG).show();
                return true;
            } else if (itemId == R.id.menuShare) {
                Toast.makeText(MainActivity.this, "Bạn đang chọn Share", Toast.LENGTH_LONG).show();
                return true;
            } else if (itemId == R.id.menuLogout) {
                XacNhanXoa();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuSetting) {
            Toast.makeText(MainActivity.this, "Bạn đang chọn Setting từ Options Menu", Toast.LENGTH_LONG).show();
            return true;
        } else if (itemId == R.id.menuShare) {
            Toast.makeText(MainActivity.this, "Bạn đang chọn Share từ Options Menu", Toast.LENGTH_LONG).show();
            return true;
        } else if (itemId == R.id.menuLogout) {
            XacNhanXoa();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void XacNhanXoa() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Thông báo");
        alert.setMessage("Bạn có muốn đăng xuất không");
        alert.setPositiveButton("Có", (dialog, which) -> {
            Toast.makeText(MainActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        });
        alert.setNegativeButton("Không", (dialog, which) -> {
            // Không làm gì cả
        });
        alert.show();
    }
}
