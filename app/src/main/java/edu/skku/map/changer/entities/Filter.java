package edu.skku.map.changer.entities;

public class Filter {
    int image = -1;
    int color = -1;
    String name = "";

    public Filter(int image, int color, String name){
        this.image = image;
        this.color = color;
        this.name = name;
    }

    public Filter() { }


    public int getImage() { return this.image; }
    public void setImage(int image) { this.image = image; }
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
    public int getColor() { return this.color; }
    public void setColor(int color) { this.color = color; }
}