package com.example.avery.chamberofwizards.Books;

public class MoreBooks {
    String book_cover, book_title, username;
    float average_rating;

    public MoreBooks() {

    }

    public MoreBooks(String book_cover, String book_title, String username, float average_rating) {
        this.book_cover = book_cover;
        this.book_title = book_title;
        this.username = username;
        this.average_rating = average_rating;
    }

    public String getBook_cover() {
        return book_cover;
    }

    public void setBook_cover(String book_cover) {
        this.book_cover = book_cover;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(float average_rating) {
        this.average_rating = average_rating;
    }
}
