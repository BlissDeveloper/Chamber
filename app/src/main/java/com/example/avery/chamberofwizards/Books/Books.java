package com.example.avery.chamberofwizards.Books;

public class Books {

    private String book_cover, book_title, username;

    public Books() {

    }

    public Books(String book_cover, String book_title, String username) {
        this.book_cover = book_cover;
        this.book_title = book_title;
        this.username = username;
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
}
