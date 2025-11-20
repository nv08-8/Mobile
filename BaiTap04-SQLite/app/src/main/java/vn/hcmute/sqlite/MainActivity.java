package vn.hcmute.sqlite;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.hcmute.sqlite.DB.DatabaseHandle;

public class MainActivity extends AppCompatActivity {
    //khai báo biến toàn cục

     DatabaseHandle databaseHandle;
     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         //gọi hàm databaseSQLite
         InitDatabaseSQLite();
         //createDatabaseSQLite();
         databaseSQLite();
     }

     private void createDatabaseSQLite() {
         //thêm dữ liệu vào bảng
         databaseHandle.QueryData("INSERT INTO Notes VALUES (null, ' Ví dụ SQLite 1')");
         databaseHandle.QueryData("INSERT INTO Notes VALUES (null, ' Ví dụ SQLite 2')");
     }

     private void InitDatabaseSQLite() {
         //khởi tạo database
         databaseHandle = new DatabaseHandle(this, "notes.sqlite", null, 1);
         //tạo bảng nếu chưa tồn tại
         databaseHandle.QueryData("CREATE TABLE IF NOT EXISTS Notes(Id INTEGER PRIMARY KEY AUTOINCREMENT, Content VARCHAR)");
     }
    private void databaseSQLite() {
         //Lấy dữ liệu
        Cursor cursor = databaseHandle.GetData("SELECT * FROM Notes");
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }
    }
}