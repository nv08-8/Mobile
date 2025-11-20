package vn.hcmute.sqlite.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vn.hcmute.sqlite.MainActivity;
import vn.hcmute.sqlite.Models.NotesModel;
import vn.hcmute.sqlite.R;

public class NotesAdapter extends BaseAdapter {
    //Khai báo biến toàn cục
    private MainActivity context;
    private int layout;
    private ArrayList<NotesModel> notesList;

    //Tạo contructor
    public NotesAdapter(MainActivity context, int layout, ArrayList<NotesModel> notesList) {
        this.context = context;
        this.layout = layout;
        this.notesList = notesList;
    }

    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Tạo viewHolder
    private static class ViewHolder {
        TextView textViewNote;
        ImageView imageViewEdit, imageViewDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layout, parent, false);
            viewHolder.textViewNote = convertView.findViewById(R.id.textViewNote);
            viewHolder.imageViewDelete = convertView.findViewById(R.id.imageViewDelete);
            viewHolder.imageViewEdit = convertView.findViewById(R.id.imageViewEdit);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final NotesModel notes = notesList.get(position);
        viewHolder.textViewNote.setText(notes.getNameNote());

        //bắt sự kiện nút cập nhật
        viewHolder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gọi Dialog trong MainActivity.java
                context.DialogCapNhatNotes(notes.getNameNote(), notes.getIdNote());
            }
        });

        //bắt sự kiện xóa notes
        viewHolder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.DialogDelete(notes.getNameNote(), notes.getIdNote());
            }
        });

        return convertView;
    }
}
