package com.pollchat.pollchat;

/**
 * Created by John on 31-Oct-16.
 */
public class Comment {

    private String comment, image, name, time, photo;

    public Comment() {

    }

    public Comment(String comment, String image, String name, String time, String photo) {
        this.comment = comment;
        this.image = image;
        this.name = name;
        this.time = time;
        this.photo = photo;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
