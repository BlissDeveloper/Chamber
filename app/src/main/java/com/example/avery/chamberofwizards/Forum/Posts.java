package com.example.avery.chamberofwizards.Forum;

public class Posts
{
    public String uid, time, date, image_url, description,profile_image, user_fullname;

    public Posts()
    {

    }

    public Posts(String uid, String time, String date, String image_url, String description, String profile_image, String user_fullname)
    {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.image_url = image_url;
        this.description = description;
        this.profile_image = profile_image;
        this.user_fullname = user_fullname;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getImage_url()
    {
        return image_url;
    }

    public void setImage_url(String image_url)
    {
        this.image_url = image_url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProfile_image()
    {
        return profile_image;
    }

    public void setProfile_image(String profile_image)
    {
        this.profile_image = profile_image;
    }

    public String getUser_fullname()
    {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname)
    {
        this.user_fullname = user_fullname;
    }
}
