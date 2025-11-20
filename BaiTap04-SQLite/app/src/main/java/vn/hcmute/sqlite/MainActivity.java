package vn.hcmute.sqlite;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

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

        listView = findViewById(R.id.listView1);
        arrayList = new ArrayList<>();
        notesAdapter = new NotesAdapter(this, R.layout.item_notes, arrayList);
        listView.setAdapter(notesAdapter);

        //khởi tạo database
        databaseHandle = new DatabaseHandle(this);

        // Load notes to check if the database is empty
        loadNotes();

        // If the list is empty, it's likely the first run, so add sample data
        if (arrayList.isEmpty()) {
            createSampleData();
            // Reload notes after adding data
            loadNotes();
        }
    }

    private void createSampleData() {
        databaseHandle.queryData("INSERT INTO " + DatabaseHandle.TABLE_NAME + " (NameNote) VALUES ('Ví dụ 1')");
        databaseHandle.queryData("INSERT INTO " + DatabaseHandle.TABLE_NAME + " (NameNote) VALUES ('Ví dụ 2')");
    }

    private void loadNotes() {
        Cursor cursor = databaseHandle.getData("SELECT * FROM " + DatabaseHandle.TABLE_NAME);
        arrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            arrayList.add(new NotesModel(id, name));
        }
        notesAdapter.notifyDataSetChanged();
        cursor.close();
    }
}
