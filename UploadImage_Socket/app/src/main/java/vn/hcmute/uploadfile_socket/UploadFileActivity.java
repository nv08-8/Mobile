package vn.hcmute.uploadfile_socket;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;

import vn.hcmute.uploadfile_socket.cons.Const;
import vn.hcmute.uploadfile_socket.network.ApiClient;

public class UploadFileActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    public static final String TAG = UploadFileActivity.class.getName();

    private Button btnChoose, btnUpload;
    private ImageView imageViewChoose, imageViewUpload;
    private EditText editTextUserName;
    private TextView textViewUserName;
    private Uri mUri;
    private ProgressDialog mProgressDialog;

    public static String[] storage_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            // show selected image in the large image view to match the desired UI
                            imageViewUpload.setImageBitmap(bitmap);
                            // also try Glide (if present) for better scaling
                            try {
                                Glide.with(UploadFileActivity.this).load(uri).into(imageViewUpload);
                            } catch (Exception ignored) {}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        // set up action bar to show Up navigation with title "Back"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Back");
        }

        AnhXa();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait Upload....");

        btnChoose.setOnClickListener(v -> checkPermission());

        btnUpload.setOnClickListener(v -> {
            if (mUri != null) {
                UploadImage1();
            } else {
                Toast.makeText(UploadFileActivity.this, "Please choose an image first", Toast.LENGTH_SHORT).show();
            }
        });

        // Make tapping the avatar images open the picker too (profile circle -> upload images flow)
        imageViewChoose.setOnClickListener(v -> checkPermission());
        imageViewUpload.setOnClickListener(v -> checkPermission());
    }

    private void AnhXa() {
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageViewChoose = findViewById(R.id.imgChoose);
        imageViewUpload = findViewById(R.id.imgMultipart);
        editTextUserName = findViewById(R.id.editUserName);
        textViewUserName = findViewById(R.id.tvUserName);
    }

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        String permissionToCheck;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionToCheck = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permissionToCheck = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permissionToCheck) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            ActivityCompat.requestPermissions(this, permissions(), MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void UploadImage1() {
        mProgressDialog.show();

        String username = editTextUserName.getText() != null ? editTextUserName.getText().toString().trim() : "";
        RequestBody requestBodyUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);

        String IMAGE_PATH = null;
        if (mUri != null) IMAGE_PATH = RealPathUtil.getRealPath(this, mUri);
        File file = null;
        boolean isTempFile = false;
        try {
            if (IMAGE_PATH != null && !IMAGE_PATH.isEmpty()) {
                file = new File(IMAGE_PATH);
            } else if (mUri != null) {
                // fallback: copy content from Uri to a temp file in cache
                file = createFileFromUri(mUri);
                isTempFile = true;
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                return;
            }
        } catch (IOException e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
            Toast.makeText(this, "Unable to read selected file", Toast.LENGTH_SHORT).show();
            return;
        }

        // create final copies so they can be referenced from the inner callback class
        final File uploadFile = file;
        final boolean uploadIsTemp = isTempFile;

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), uploadFile);
        MultipartBody.Part partbodyavatar = MultipartBody.Part.createFormData(Const.MY_IMAGES, uploadFile.getName(), requestFile);

        // Provide the profile id so the server can associate the uploaded image with a user document in MongoDB
        RequestBody idPart = RequestBody.create(MultipartBody.FORM, "5");

        // Call the API that returns a generic ResponseBody
        ApiClient.serviceapi.uploadLocal(idPart, partbodyavatar).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                String serverMessage = "";
                try {
                    if (response.body() != null) serverMessage = response.body().string();
                    else if (response.errorBody() != null) serverMessage = response.errorBody().string();
                } catch (IOException e) {
                    serverMessage = "(unable to read response)";
                }
                android.util.Log.d(TAG, "Upload response code=" + response.code() + " body=" + serverMessage);

                // If we saved a temp file in cache, delete it to avoid filling cache
                if (uploadIsTemp && uploadFile != null) {
                    try {
                        File parent = uploadFile.getParentFile();
                        if (parent != null && parent.getAbsolutePath().equals(getCacheDir().getAbsolutePath())) {
                            uploadFile.delete();
                        }
                    } catch (Exception ignored) {}
                }

                if (response.isSuccessful()) {
                    Toast.makeText(UploadFileActivity.this, "Thành công: " + serverMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadFileActivity.this, "Thất bại: " + serverMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                android.util.Log.e(TAG, "Upload failed: " + t.getMessage(), t);
                // Try to delete temp file if any
                if (uploadIsTemp && uploadFile != null) {
                    try {
                        File parent = uploadFile.getParentFile();
                        if (parent != null && parent.getAbsolutePath().equals(getCacheDir().getAbsolutePath())) {
                            uploadFile.delete();
                        }
                    } catch (Exception ignored) {}
                }
                Toast.makeText(UploadFileActivity.this, "Gọi API thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Copy content from the provided Uri into a temporary file in app cache and return it
    private File createFileFromUri(Uri uri) throws IOException {
        if (uri == null) throw new IOException("uri is null");
        String fileName = "upload_" + System.currentTimeMillis();
        File outputDir = getCacheDir();
        File outputFile = File.createTempFile(fileName, ".jpg", outputDir);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            if (inputStream == null) throw new IOException("Unable to open input stream from URI");
            byte[] buffer = new byte[8192];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }

        return outputFile;
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
