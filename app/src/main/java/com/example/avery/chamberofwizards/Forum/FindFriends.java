package com.example.avery.chamberofwizards.Forum;

public class FindFriends
{

    public String profile_image, fullname, course;

    public FindFriends()
    {

    }

    public FindFriends(String profile_image, String fullname, String course)
    {
        this.profile_image = profile_image;
        this.fullname = fullname;
        this.course = course;
    }

    public String getProfile_image()
    {
        return profile_image;
    }

    public void setProfile_image(String profile_image)
    {
        this.profile_image = profile_image;
    }

    public String getFullname()
    {
        return fullname;
    }

    public void setFullname(String fullname)
    {
        this.fullname = fullname;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course)
    {
        this.course = course;
    }
}
