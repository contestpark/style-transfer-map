package edu.skku.map.changer.entities;

import android.graphics.Bitmap;

public class Post {
    String name = "";
    Bitmap profile;
    Bitmap image ;
    String heart;

    public Post(String name, Bitmap profile, Bitmap image, String heart){
        this.name = name;
        this.profile = profile;
        this.image = image;
        this.heart = heart;
    }

    public Post() { }

    public Post(String name, Bitmap image, String heart) {
        this.name = name;
        this.image = image;
        this.heart = heart;
    }


    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public Bitmap getProfile() { return this.profile; }
    public void setProfile(Bitmap profile) { this.profile = profile; }
    public Bitmap getImage() { return this.image; }
    public void setImage(Bitmap image) { this.image = image; }
    public String getHeart() { return this.heart; }
    public void setHeart(String heart) { this.heart = heart; }

}