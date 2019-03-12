package com.example.avery.chamberofwizards.Games;

public class Games {
    String download_link, game_title, title_screen_image, package_name;
    float average_rating;
    int number_of_reviews;
    long fileSize;

    public Games() {

    }

    public Games(String download_link, String game_title, String title_screen_image, String package_name, float average_rating, int number_of_reviews, long fileSize) {
        this.download_link = download_link;
        this.game_title = game_title;
        this.title_screen_image = title_screen_image;
        this.package_name = package_name;
        this.average_rating = average_rating;
        this.number_of_reviews = number_of_reviews;
        this.fileSize = fileSize;
    }

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }

    public String getGame_title() {
        return game_title;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
    }

    public String getTitle_screen_image() {
        return title_screen_image;
    }

    public void setTitle_screen_image(String title_screen_image) {
        this.title_screen_image = title_screen_image;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public float getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(float average_rating) {
        this.average_rating = average_rating;
    }

    public int getNumber_of_reviews() {
        return number_of_reviews;
    }

    public void setNumber_of_reviews(int number_of_reviews) {
        this.number_of_reviews = number_of_reviews;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
