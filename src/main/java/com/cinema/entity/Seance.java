package com.cinema.entity;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class Seance {
    int id;
    String film;
    String hall;
    LocalDateTime seanceDate;
    Date date;
    Time time;
    int cost;
    String genre;
    String restriction;

    public LocalDateTime getSeanceDate() {
        return seanceDate;
    }

    public void setSeanceDate(LocalDateTime seanceDate) {
        this.seanceDate = seanceDate;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }



    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilm() {
        return film;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public Seance(){}
}
