package vn.hcmute.uploadfile_socket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.uploadfile_socket.network.ApiClient;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private Button btnLogout;
    private TextView tvIdValue, tvUsernameValueProfile, tvFullNameValueProfile, tvEmailValueProfile, tvGenderValueProfile;
    private TextView tvRawResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // show Up button and set title like the screenshots
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Back");
        }

        imgProfile = findViewById(R.id.imgProfile);
        btnLogout = findViewById(R.id.btnLogout);
        tvIdValue = findViewById(R.id.tvIdValue);
        tvUsernameValueProfile = findViewById(R.id.tvUsernameValueProfile);
        tvFullNameValueProfile = findViewById(R.id.tvFullNameValueProfile);
        tvEmailValueProfile = findViewById(R.id.tvEmailValueProfile);
        tvGenderValueProfile = findViewById(R.id.tvGenderValueProfile);

        // Tap on profile image opens UploadFileActivity
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, UploadFileActivity.class);
                startActivity(intent);
            }
        });

        // Minimal logout action: finish activity
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // fetch profile from API (default id=5) -- adjust if you have user id stored
        fetchProfile("5");
    }

    private void fetchProfile(String id) {
        // The ServiceAPI.getProfile endpoint now accepts a plain String query parameter (GET /fetch?id=...)
        ApiClient.serviceapi.getProfile(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ProfileActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String body = response.body().string();
                    Log.d("ProfileActivity", "raw response: " + body);
                    // sometimes server returns escaped JSON string: try to normalize
                    String json = body.trim();
                    if (json.startsWith("\"")) {
                        json = json.substring(1, json.length()-1).replaceAll("\\\\/", "/");
                    }
                    // If server prepends HTML (PHP notices), extract the JSON portion starting with {"success"
                    int jsonStart = json.indexOf("{\"success\"");
                    if (jsonStart == -1) {
                        // fallback: find first JSON object char
                        int brace = json.indexOf('{');
                        if (brace != -1) jsonStart = brace;
                    }
                    if (jsonStart > 0) json = json.substring(jsonStart);
                    // Make a final copy of the normalized JSON so it can be referenced inside the lambda
                    final String normalizedJson = json;
                    JSONObject obj = new JSONObject(normalizedJson);
                    if (!obj.has("result")) return;
                    JSONArray arr = obj.getJSONArray("result");
                    if (arr.length() == 0) {
                        // show raw response + a toast so user knows nothing was returned
                        runOnUiThread(() -> {
                            if (tvRawResponse != null) {
                                tvRawResponse.setVisibility(View.VISIBLE);
                                tvRawResponse.setText(normalizedJson);
                            } else {
                                Log.w("ProfileActivity", "tvRawResponse not found in layout; server response: " + normalizedJson);
                            }
                            Toast.makeText(ProfileActivity.this, "No profile data returned from server", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    JSONObject p = arr.getJSONObject(0);
                    final String sid = p.optString("id", "-");
                    final String username = p.optString("username", "-");
                    final String fname = p.optString("fname", "-");
                    final String email = p.optString("email", "-");
                    final String gender = p.optString("gender", "-");
                    final String images = p.optString("images", "");

                    runOnUiThread(() -> {
                        // Only show human-facing fields; raw JSON left hidden unless server returned no data
                        tvIdValue.setText("ID: " + sid);
                        tvUsernameValueProfile.setText("Username: " + username);
                        tvFullNameValueProfile.setText("Full name: " + fname);
                        tvEmailValueProfile.setText("Email: " + email);
                        tvGenderValueProfile.setText("Gender: " + gender);
                        if (images != null && !images.isEmpty()) {
                            try {
                                Glide.with(ProfileActivity.this).load(images).into(imgProfile);
                            } catch (Exception ignored) {}
                        }
                    });

                } catch (Exception e) {
                    Log.e("ProfileActivity", "parse error", e);
                    Toast.makeText(ProfileActivity.this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
