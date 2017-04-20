package com.pollchat.pollchat;

/**
 * Created by John on 31-Oct-16.
 */
public class People {

    private String name, image, bio;

    public People() {

    }

    public People(String name, String image, String bio) {
        this.name = name;

        this.image = image;
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
