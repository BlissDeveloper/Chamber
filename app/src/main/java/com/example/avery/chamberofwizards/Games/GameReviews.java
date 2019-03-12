package com.example.avery.chamberofwizards.Games;

public class GameReviews {
    String gameKey, game_review, game_reviewer_course, game_reviewer_image, game_reviewer_name;
    float game_rating;

    public GameReviews() {

    }

    public GameReviews(String gameKey, String game_review, String game_reviewer_course, String game_reviewer_image, String game_reviewer_name, float game_rating) {
        this.gameKey = gameKey;
        this.game_review = game_review;
        this.game_reviewer_course = game_reviewer_course;
        this.game_reviewer_image = game_reviewer_image;
        this.game_reviewer_name = game_reviewer_name;
        this.game_rating = game_rating;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getGame_review() {
        return game_review;
    }

    public void setGame_review(String game_review) {
        this.game_review = game_review;
    }

    public String getGame_reviewer_course() {
        return game_reviewer_course;
    }

    public void setGame_reviewer_course(String game_reviewer_course) {
        this.game_reviewer_course = game_reviewer_course;
    }

    public String getGame_reviewer_image() {
        return game_reviewer_image;
    }

    public void setGame_reviewer_image(String game_reviewer_image) {
        this.game_reviewer_image = game_reviewer_image;
    }

    public String getGame_reviewer_name() {
        return game_reviewer_name;
    }

    public void setGame_reviewer_name(String game_reviewer_name) {
        this.game_reviewer_name = game_reviewer_name;
    }

    public float getGame_rating() {
        return game_rating;
    }

    public void setGame_rating(float game_rating) {
        this.game_rating = game_rating;
    }
}
