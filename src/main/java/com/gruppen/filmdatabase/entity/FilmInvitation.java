package com.gruppen.filmdatabase.entity;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_film_invitations")
@AllArgsConstructor
@NoArgsConstructor
public class FilmInvitation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User invitingUser;

    @ManyToOne
    private User invitedUser;

    @ManyToOne
    private Film film;

    private LocalDateTime invitedAt;

    private String remark;

    private FilmInvitationStatus status;

    public FilmInvitation(User invitingUser, User invitedUser, Film film, LocalDateTime invitedAt, String remark) {
        this.invitingUser = invitingUser;
        this.invitedUser = invitedUser;
        this.film = film;
        this.invitedAt = invitedAt;
        this.remark = remark;
        this.status = FilmInvitationStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(User invitingUser) {
        this.invitingUser = invitingUser;
    }

    public User getInvitedUser() {
        return invitedUser;
    }

    public void setInvitedUser(User invitedUser) {
        this.invitedUser = invitedUser;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;

    }

    public LocalDateTime getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public FilmInvitationStatus getStatus() {
        return status;
    }

    public void setStatus(FilmInvitationStatus status) {
        this.status = status;
    }
}

