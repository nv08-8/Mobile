package vn.hcmute.sqlite.Models;

import java.io.Serializable;

public class NotesModel implements Serializable{
    private int IdNote;
    private String NameNote;

    public NotesModel(int id, String name) {
        this.IdNote = id;
        this.NameNote = name;
    }

    public int getIdNote() {
        return IdNote;
    }

    public void setIdNote(int idNote) {
        IdNote = idNote;
    }

    public String getNameNote() {
        return NameNote;
    }

    public void setNameNote(String nameNote) {
        NameNote = nameNote;
    }
}
