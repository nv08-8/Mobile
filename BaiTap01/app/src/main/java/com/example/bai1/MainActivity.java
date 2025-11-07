package com.example.bai1;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bai1.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Yêu cầu 4: In ra số chẵn và số lẻ từ ArrayList
        ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        logEvenAndOddNumbers(numbers);

        // Yêu cầu 5: Đảo ngược và in hoa chuỗi
        binding.reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = binding.inputText.getText().toString();
                if (!inputText.isEmpty()) {
                    String reversedString = reverseString(inputText);
                    binding.reversedText.setText(reversedString);
                    Toast.makeText(MainActivity.this, reversedString, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.githubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nv08-8"));
                startActivity(intent);
            }
        });

        binding.emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("email", "23162074@student.hcmute.edu.vn");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Đã sao chép Email vào Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logEvenAndOddNumbers(ArrayList<Integer> numbers) {
        ArrayList<Integer> evenNumbers = new ArrayList<>();
        ArrayList<Integer> oddNumbers = new ArrayList<>();

        for (int number : numbers) {
            if (number % 2 == 0) {
                evenNumbers.add(number);
            } else {
                oddNumbers.add(number);
            }
        }

        Log.d("EvenNumbers", evenNumbers.toString());
        Log.d("OddNumbers", oddNumbers.toString());
    }

    private String reverseString(String input) {
        String[] words = input.split("\\s+");
        Collections.reverse(Arrays.asList(words));
        return String.join(" ", words).toUpperCase();
    }
}