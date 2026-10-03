package com.gruppen.filmdatabase.entity;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;
import java.util.List;

@Entity
@Table(name = "tbl_films")
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose(serialize = false, deserialize = false)
    private Long id;

    @Expose
    private String name;

    private String category;

    private String length;

    @Column(nullable = true, name = "pub_date")
    private String pubDate;

    private String director;

    private String scriptAuthor;

    private String cast;

    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private byte[] banner;


    @OneToMany(mappedBy = "film",cascade = CascadeType.ALL)
    @Expose(serialize = false, deserialize = false)
    private List<FilmError> filmErrors;


    public Film(String name, String category, String length, String pubDate, String director, String cast,
                String scriptAuthor) {

        this.name = name;
        this.category = category;
        this.length = length;
        this.pubDate = pubDate;
        this.director = director;
        this.cast = cast;
        this.scriptAuthor = scriptAuthor;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScriptAuthor() {
        return scriptAuthor;
    }

    public void setScriptAuthor(String scriptAuthor) {
        this.scriptAuthor = scriptAuthor;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public byte[] getBanner() {
        return banner;
    }

    public void setBanner(byte[] banner) {
        this.banner = banner;
    }

    public List<FilmError> getFilmErrors() {
        return filmErrors;
    }

    public void setFilmErrors(List<FilmError> filmErrors) {
        this.filmErrors = filmErrors;
    }


}
