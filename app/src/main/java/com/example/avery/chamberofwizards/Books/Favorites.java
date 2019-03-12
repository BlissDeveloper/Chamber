package com.example.avery.chamberofwizards.Books;

public class Favorites {

    String book_cover, book_title;

    public Favorites() {

    }

    public Favorites(String book_cover, String book_title) {
        this.book_cover = book_cover;
        this.book_title = book_title;
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
}

