package com.gruppen.filmdatabase.entity;

import java.time.LocalDate;

public class UserStatistic {

    private LocalDate from;
    private LocalDate until;


    private String favoriteCategory;

    private String favoriteActor;


    private Film favoriteFilm;

    private Double watchTime;

    public UserStatistic(LocalDate from,
                         LocalDate until,
                         String favoriteActor,
                         String favoriteCategory,
                         Film favoriteFilm,
                         Double watchTime) {
        this.from = from;
        this.until = until;
        this.favoriteCategory = favoriteCategory;
        this.favoriteFilm = favoriteFilm;
        this.watchTime = watchTime;
        this.favoriteActor = favoriteActor;
    }


    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getUntil() {
        return until;
    }

    public void setUntil(LocalDate until) {
        this.until = until;
    }


    public String getFavoriteCategory() {
        return favoriteCategory;
    }

    public void setFavoriteCategory(String favoriteCategory) {
        this.favoriteCategory = favoriteCategory;
    }

    public Film getFavoriteFilm() {
        return favoriteFilm;
    }

    public void setFavoriteFilm(Film favoriteFilm) {
        this.favoriteFilm = favoriteFilm;
    }

    public Double getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(Double watchTime) {
        this.watchTime = watchTime;
    }

    public String getFavoriteActor() {
        return favoriteActor;
    }

    public void setFavoriteActor(String favoriteActor) {
        this.favoriteActor = favoriteActor;
    }
}
