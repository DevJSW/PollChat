package com.pollchat.pollchat;

/**
 * Created by John on 31-Oct-16.
 */
public class Letters {

    private String title, story, image, photo, name, pin, community, location, faculty, community_sent, time, reads, uid;

    public Letters() {

    }

    public Letters(String title, String story, String image, String photo, String name, String pin, String community, String faculty, String community_sent, String time, String reads, String uid) {

        this.title = title;
        this.story = story;
        this.image = image;
        this.photo = photo;
        this.name = name;
        this.pin = pin;
        this.community = community;
        this.faculty = faculty;
        this.community_sent = community_sent;
        this.time = time;
        this.reads = reads;
        this.uid = uid;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String campus) {
        this.pin = pin;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCommunity_sent() {
        return community_sent;
    }

    public void setCommunity_sent(String community_sent) {
        this.community_sent = community_sent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReads() {
        return reads;
    }

    public void setReads(String reads) {
        this.reads = reads;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
