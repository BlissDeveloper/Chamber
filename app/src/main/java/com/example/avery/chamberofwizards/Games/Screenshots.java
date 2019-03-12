package com.example.avery.chamberofwizards.Games;

public class Screenshots {
    String screenshot_url, game_id;

    public Screenshots() {

    }

    public Screenshots(String screenshot_url, String game_id) {
        this.screenshot_url = screenshot_url;
        this.game_id = game_id;
    }

    public String getScreenshot_url() {
        return screenshot_url;
    }

    public void setScreenshot_url(String screenshot_url) {
        this.screenshot_url = screenshot_url;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }
}
