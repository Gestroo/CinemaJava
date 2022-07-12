package com.cinema.entity;

public class Genre {
    int id;
    String title;
    String desc;

    public Genre(int id, String title){
        this.id=id;
        this.title=title;
    }

    public Genre(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
