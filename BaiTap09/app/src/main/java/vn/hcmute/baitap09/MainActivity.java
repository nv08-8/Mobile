package vn.hcmute.baitap09;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * MainActivity - Entry point của ứng dụng
 *
 * Cung cấp 2 tùy chọn:
 * 1. Mở chat với role Customer
 * 2. Mở chat với role Manager
 */
public class MainActivity extends AppCompatActivity {

    private Button btnCustomerChat;
    private Button btnManagerChat;

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

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnCustomerChat = findViewById(R.id.btnCustomerChat);
        btnManagerChat = findViewById(R.id.btnManagerChat);
    }

    private void setupListeners() {
        // Open customer chat
        btnCustomerChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        // Open manager chat
        btnManagerChat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManagerChatActivity.class);
            startActivity(intent);
        });
    }
}