package com.nombee;

/**
 * Created by erikotsuda on 6/21/16.
 */
public class UserPost {
    private String userName;
    private double stars;
    private String comment;
    private String liqueurName;
    private int liqueurId;

    UserPost(String userName, double stars, String comment, String liqueurName, int liqueurId){
        this.userName = userName;
        this.stars = stars;
        this.comment = comment;
        this.liqueurName = liqueurName;
        this.liqueurId = liqueurId;
    }

    public String getUserName(){
        return this.userName;
    }

    public double getStars(){
        return this.stars;
    }

    public String getComment(){
        return this.comment;
    }

    public String getLiqueurName(){
        return this.liqueurName;
    }

    public int getLiqueurId(){
        return this.liqueurId;
    }
}
