package com.example.avery.chamberofwizards.Notes;

public class Users {
    String course, fullname, profile_image;

    public Users() {

    }

    public Users(String course, String fullname, String profile_image) {
        this.course = course;
        this.fullname = fullname;
        this.profile_image = profile_image;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
