package com.gruppen.filmdatabase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_films_errors")
@Data
@AllArgsConstructor
public class FilmError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Film film;

    @ManyToOne
    private User reportedBy;

    private String errorMessage;
    private LocalDateTime reportedAt;

    private Boolean isResolved;


    public FilmError(User reportedBy, Film film, LocalDateTime reportedAt, String errorMessage) {
        this.film = film;
        this.reportedBy = reportedBy;
        this.reportedAt = reportedAt;
        this.errorMessage = errorMessage;
        this.isResolved = false;
    }

    public FilmError() {
        this.isResolved = false;
    }
}
