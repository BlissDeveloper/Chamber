package com.example.avery.chamberofwizards.Forum;

public class ChamberNotifications {
    String commenter, commenter_image, date, post_key, poster, time;

    public ChamberNotifications() {

    }

    public ChamberNotifications(String commenter, String commenter_image, String date, String post_key, String poster, String time) {
        this.commenter = commenter;
        this.commenter_image = commenter_image;
        this.date = date;
        this.post_key = post_key;
        this.poster = poster;
        this.time = time;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getCommenter_image() {
        return commenter_image;
    }

    public void setCommenter_image(String commenter_image) {
        this.commenter_image = commenter_image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPost_key() {
        return post_key;
    }

    public void setPost_key(String post_key) {
        this.post_key = post_key;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
