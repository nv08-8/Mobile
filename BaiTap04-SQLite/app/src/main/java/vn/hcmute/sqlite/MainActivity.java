package vn.hcmute.sqlite;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import vn.hcmute.sqlite.Adapters.NotesAdapter;
import vn.hcmute.sqlite.DB.DatabaseHandle;
import vn.hcmute.sqlite.Models.NotesModel;

public class MainActivity extends AppCompatActivity {
    //khai báo biến toàn cục
     DatabaseHandle databaseHandle;
     ListView listView;
     ArrayList<NotesModel> arrayList;
     NotesAdapter notesAdapter;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

         //ánh xạ listview và gọi adapter
         listView = findViewById(R.id.listView1);
         arrayList = new ArrayList<>();
         notesAdapter = new NotesAdapter(this, R.layout.item_notes, arrayList);
         listView.setAdapter(notesAdapter);

         //gọi hàm databaseSQLite
         InitDatabaseSQLite();
         createDatabaseSQLite();
         databaseSQLite();
     }

     private void createDatabaseSQLite() {
         //thêm dữ liệu vào bảng
         databaseHandle.queryData("INSERT INTO " + DatabaseHandle.TABLE_NAME + " VALUES (null, 'Ví dụ SQLite 1')");
         databaseHandle.queryData("INSERT INTO " + DatabaseHandle.TABLE_NAME + " VALUES (null, 'Ví dụ SQLite 2')");
     }

     private void InitDatabaseSQLite() {
         //khởi tạo database
         databaseHandle = new DatabaseHandle(this);
     }
    public void databaseSQLite() {
         //Lấy dữ liệu
        Cursor cursor = databaseHandle.getData("SELECT * FROM " + DatabaseHandle.TABLE_NAME);
        arrayList.clear();
        while (cursor.moveToNext()) {
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            arrayList.add(new NotesModel(id, name));
        }
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu, menu);
         return super.onCreateOptionsMenu(menu);
    }

    //bắt sự kiện cho menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuAddNotes) {
            DialogThem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogThem() {
        //thêm dialog
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_notes);
        //ánh xạ trong dialog
        EditText editText = dialog.findViewById(R.id.editTextName);
        Button buttonAdd = dialog.findViewById(R.id.buttonEdit);
        Button buttonHuy = dialog.findViewById(R.id.buttonHuy);
        //bắt sự kiện cho nút thêm và hủy
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHandle.queryData("INSERT INTO " + DatabaseHandle.TABLE_NAME + " VALUES (null, '" + name + "')");
                    Toast.makeText(MainActivity.this, "Đã thêm notes", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    databaseSQLite(); //gọi hàm load lại dữ liệu
                }
            }

        });
        buttonHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

    }

    //hàm dialog cập nhật
    public void DialogCapNhatNotes(String tenCV, final int id) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_notes);

        EditText editText = dialog.findViewById(R.id.editTextNameUpdate);
        Button buttonUpdate = dialog.findViewById(R.id.buttonUpdate);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

        editText.setText(tenCV);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenMoi = editText.getText().toString().trim();
                if (tenMoi.equals("")) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHandle.queryData("UPDATE " + DatabaseHandle.TABLE_NAME + " SET " + DatabaseHandle.COL_NAME + " = '" + tenMoi + "' WHERE " + DatabaseHandle.COL_ID + " = " + id);
                    Toast.makeText(MainActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    databaseSQLite();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    //hàm dialog xóa
    public void DialogDelete(String name, final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn xóa Notes " + name + " này không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHandle.queryData("DELETE FROM " + DatabaseHandle.TABLE_NAME + " WHERE " + DatabaseHandle.COL_ID + " = " + id);
                Toast.makeText(MainActivity.this, "Đã xóa Notes " + name+ " thành công", Toast.LENGTH_SHORT).show();
                databaseSQLite();//gọi hàm load lại dữ liệu
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
