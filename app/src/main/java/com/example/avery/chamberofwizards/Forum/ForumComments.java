package com.example.avery.chamberofwizards.Forum;

public class ForumComments {
    String comment, date, time, user_comment_img, username;

    public ForumComments() {

    }

    public ForumComments(String comment, String date, String time, String user_comment_img, String username) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.user_comment_img = user_comment_img;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_comment_img() {
        return user_comment_img;
    }

    public void setUser_comment_img(String user_comment_img) {
        this.user_comment_img = user_comment_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
