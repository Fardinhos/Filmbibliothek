package com.gruppen.filmdatabase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tbl_user_rights")
@Data
@AllArgsConstructor
public class UserRights {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UserRightsType showFriends;

    private UserRightsType showWatch;

    private UserRightsType showRating;

    public UserRights() {
        this.showFriends = UserRightsType.ALL;
        this.showWatch = UserRightsType.ALL;
        this.showRating = UserRightsType.ALL;
    }

    @OneToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRightsType getShowFriends() {
        return showFriends;
    }

    public void setShowFriends(UserRightsType showFriendsList) {
        this.showFriends = showFriendsList;
    }

    public UserRightsType getShowWatch() {
        return showWatch;
    }

    public void setShowWatch(UserRightsType showWatchList) {
        this.showWatch = showWatchList;
    }

    public UserRightsType getShowRating() {
        return showRating;
    }

    public void setShowRating(UserRightsType showRatings) {
        this.showRating = showRatings;
    }
}
