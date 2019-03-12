package com.example.avery.chamberofwizards.Events;

public class Events {
    String event_audience, event_date_announced, event_description,
    event_end_layout, event_end_time, event_location, event_start_layout,
    event_start_time, event_title, selected_day_display, selected_month_display,
    announcer, announcer_image;

    public Events() {

    }

    public Events(String event_audience, String event_date_announced, String event_description, String event_end_layout, String event_end_time, String event_location, String event_start_layout, String event_start_time, String event_title, String selected_day_display, String selected_month_display, String announcer, String announcer_image) {
        this.event_audience = event_audience;
        this.event_date_announced = event_date_announced;
        this.event_description = event_description;
        this.event_end_layout = event_end_layout;
        this.event_end_time = event_end_time;
        this.event_location = event_location;
        this.event_start_layout = event_start_layout;
        this.event_start_time = event_start_time;
        this.event_title = event_title;
        this.selected_day_display = selected_day_display;
        this.selected_month_display = selected_month_display;
        this.announcer = announcer;
        this.announcer_image = announcer_image;
    }

    public String getEvent_audience() {
        return event_audience;
    }

    public void setEvent_audience(String event_audience) {
        this.event_audience = event_audience;
    }

    public String getEvent_date_announced() {
        return event_date_announced;
    }

    public void setEvent_date_announced(String event_date_announced) {
        this.event_date_announced = event_date_announced;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getEvent_end_layout() {
        return event_end_layout;
    }

    public void setEvent_end_layout(String event_end_layout) {
        this.event_end_layout = event_end_layout;
    }

    public String getEvent_end_time() {
        return event_end_time;
    }

    public void setEvent_end_time(String event_end_time) {
        this.event_end_time = event_end_time;
    }

    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public String getEvent_start_layout() {
        return event_start_layout;
    }

    public void setEvent_start_layout(String event_start_layout) {
        this.event_start_layout = event_start_layout;
    }

    public String getEvent_start_time() {
        return event_start_time;
    }

    public void setEvent_start_time(String event_start_time) {
        this.event_start_time = event_start_time;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getSelected_day_display() {
        return selected_day_display;
    }

    public void setSelected_day_display(String selected_day_display) {
        this.selected_day_display = selected_day_display;
    }

    public String getSelected_month_display() {
        return selected_month_display;
    }

    public void setSelected_month_display(String selected_month_display) {
        this.selected_month_display = selected_month_display;
    }

    public String getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    public String getAnnouncer_image() {
        return announcer_image;
    }

    public void setAnnouncer_image(String announcer_image) {
        this.announcer_image = announcer_image;
    }
}
