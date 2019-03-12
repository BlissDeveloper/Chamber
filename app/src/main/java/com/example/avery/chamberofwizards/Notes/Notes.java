package com.example.avery.chamberofwizards.Notes;

public class Notes {

    private String note_content, note_date, note_time, note_title, uid;

    public Notes() {

    }

    public Notes(String note_content, String note_date, String note_time, String note_title, String uid) {
        this.note_content = note_content;
        this.note_date = note_date;
        this.note_time = note_time;
        this.note_title = note_title;
        this.uid = uid;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public String getNote_date() {
        return note_date;
    }

    public void setNote_date(String note_date) {
        this.note_date = note_date;
    }

    public String getNote_time() {
        return note_time;
    }

    public void setNote_time(String note_time) {
        this.note_time = note_time;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
