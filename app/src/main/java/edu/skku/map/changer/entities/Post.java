package edu.skku.map.changer.entities;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

public class Post {
    String name = "";
    Bitmap profile;
    Bitmap image ;
    String heart;
    String date;
    String id = "";

    public Post(String name, Bitmap profile, Bitmap image, int heart, String date){
        this.name = name;
        this.profile = profile;
        this.image = image;
        this.heart = String.valueOf(heart);
        this.date = date;
    }

    public Post(String name, Bitmap profile, Bitmap image, int heart){
        this.name = name;
        this.profile = profile;
        this.image = image;
        this.heart = String.valueOf(heart);
    }

    public Post(String name, String date, int heart) {
        this.name = name;
        this.date = date;
        this.heart = String.valueOf(heart);
    }

    public Post(Map<String, Object> map) {
        if (map.get("name") != null) this.name = map.get("name").toString();
        else this.name = "";
        if (map.get("heart") != null) this.heart = map.get("heart").toString();
        else this.heart = "";
        if (map.get("date") != null) this.date = map.get("date").toString();
        else this.date = "";
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("heart", heart);
        result.put("date", date);

        return result;
    }

    public void updateHeart(int num)
    {
        int temp = Integer.parseInt(this.heart);
        heart = String.valueOf(temp + num);
    }



    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public Bitmap getProfile() { return this.profile; }
    public void setProfile(Bitmap profile) { this.profile = profile; }
    public Bitmap getImage() { return this.image; }
    public void setImage(Bitmap image) { this.image = image; }
    public String  getHeart() { return this.heart; }
    public void setHeart(String  heart) { this.heart = heart; }
    public String getDate() { return this.date; }
    public void setDate(String date) { this.date = date; }
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

}