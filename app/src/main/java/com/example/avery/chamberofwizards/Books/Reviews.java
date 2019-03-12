package com.example.avery.chamberofwizards.Books;

public class Reviews
{
    String date, review, time, user_image, username;
    long rating;

    public Reviews()
    {

    }

    public Reviews(String date, String review, String time, String user_image, String username, long rating)
    {
        this.date = date;
        this.review = review;
        this.time = time;
        this.user_image = user_image;
        this.username = username;
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }
}
