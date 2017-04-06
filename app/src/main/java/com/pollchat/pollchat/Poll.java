package com.pollchat.pollchat;

/**
 * Created by John on 31-Oct-16.
 */
public class Poll {

    private String name, image, created_date, first_row_username, first_row_userimg, first_row_votecounter, poll_question , second_row_username, second_row_userimg, third_row_userimg, third_row_username, fourth_row_userimg, fourth_row_username;

    public Poll() {

    }

    public Poll(String name, String photo, String image, String created_date, String first_row_username, String first_row_userimg, String first_row_votecounter, String poll_question, String second_row_username, String second_row_userimg, String third_row_userimg, String third_row_username, String fourth_row_userimg, String fouth_row_username) {
        this.name = name;
        this.image = image;
        this.created_date = created_date;
        this.first_row_username = first_row_username;

        this.first_row_userimg = first_row_userimg;
        this.first_row_votecounter = first_row_votecounter;
        this.poll_question = poll_question;
        this.second_row_username = second_row_username;
        this.second_row_userimg = second_row_userimg;
        this.third_row_userimg = third_row_userimg;
        this.third_row_username = third_row_username;
        this.fourth_row_userimg = fourth_row_userimg;
        this.fourth_row_username = fouth_row_username;
    }


    public String getFirst_row_username() {
        return first_row_username;
    }

    public void setFirst_row_username(String first_row_username) {
        this.first_row_username = first_row_username;
    }

    public String getFirst_row_userimg() {
        return first_row_userimg;
    }

    public void setFirst_row_userimg(String first_row_userimg) {
        this.first_row_userimg = first_row_userimg;
    }

    public String getFirst_row_votecounter() {
        return first_row_votecounter;
    }

    public void setFirst_row_votecounter(String first_row_votecounter) {
        this.first_row_votecounter = first_row_votecounter;
    }

    public String getPoll_question() {
        return poll_question;
    }

    public void setPoll_question(String poll_question) {
        this.poll_question = poll_question;
    }

    public String getSecond_row_username() {
        return second_row_username;
    }

    public void setSecond_row_username(String second_row_username) {
        this.second_row_username = second_row_username;
    }

    public String getSecond_row_userimg() {
        return second_row_userimg;
    }

    public void setSecond_row_userimg(String second_row_userimg) {
        this.second_row_userimg = second_row_userimg;
    }

    public String getThird_row_userimg() {
        return third_row_userimg;
    }

    public void setThird_row_userimg(String third_row_userimg) {
        this.third_row_userimg = third_row_userimg;
    }

    public String getThird_row_username() {
        return third_row_username;
    }

    public void setThird_row_username(String third_row_username) {
        this.third_row_username = third_row_username;
    }

    public String getFourth_row_userimg() {
        return fourth_row_userimg;
    }

    public void setFourth_row_userimg(String fourth_row_userimg) {
        this.fourth_row_userimg = fourth_row_userimg;
    }

    public String getFourth_row_username() {
        return fourth_row_username;
    }

    public void setFourth_row_username(String fourth_row_username) {
        this.fourth_row_username = fourth_row_username;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
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
}
