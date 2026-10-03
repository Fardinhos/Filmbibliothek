package com.gruppen.filmdatabase.entity;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_film_statistics")
@AllArgsConstructor
@NoArgsConstructor
public class FilmStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose(serialize = false, deserialize = false)
    private Long id;

    @Expose
    private double averageRating;

    @Expose
    private int numberOfRatings;

    @Expose
    private int numberOfViews;

    @Expose
    @OneToOne
    private Film film;

    @OneToMany
    private List<FilmRating> filmRatings;

    public FilmStatistic(Film film) {
        averageRating = 0;
        numberOfRatings = 0;
        numberOfViews = 0;
        this.film = film;
        this.filmRatings = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public List<FilmRating> getFilmRatings() {
        return filmRatings;
    }

    public void setFilmRatings(List<FilmRating> filmRatings) {
        this.filmRatings = filmRatings;
    }


}