package com.gruppen.filmdatabase.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String lastName;

    @Column(nullable = true, length = 20)
    private Boolean isAdmin;

    @Column(nullable = true, length = 20)
    private LocalDate birthDate;

    //    https://www.javaguides.net/2020/10/lob-jpa-annotation-with-example.html
    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private byte[] profilePicture;

    @ManyToMany
    private List<User> friendsList;

    @ManyToMany
    private List<Film> watchList;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<FilmRating> filmsRatings;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ChatHistory> chatHistories;

    @Column(nullable = true, length = 6)
    private String latestSecretKey;

    @OneToOne(cascade = CascadeType.ALL)
    private UserRights userRights;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sentBy")
    private List<FriendRequest> sentFriendRequests;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sentTo")
    private List<FriendRequest> pendingFriendRequests;



    public User(Long id, String email, String password, String firstName, String lastName, Boolean isAdmin, LocalDate birthDate) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isAdmin = isAdmin;
        this.birthDate = birthDate;
        this.friendsList = new ArrayList<>();
        this.filmsRatings = new ArrayList<>();
        this.watchList = new ArrayList<>();
        this.chatHistories = new ArrayList<>();
        this.userRights = new UserRights();
        this.pendingFriendRequests = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();
    }

    public User(String email, String password, String firstName, String lastName, LocalDate birthDate) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.friendsList = new ArrayList<>();
        this.filmsRatings = new ArrayList<>();
        this.watchList = new ArrayList<>();
        this.chatHistories = new ArrayList<>();
        this.userRights = new UserRights();
        this.pendingFriendRequests = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();

    }

    public User() {
        this.friendsList = new ArrayList<>();
        this.filmsRatings = new ArrayList<>();
        this.watchList = new ArrayList<>();
        this.chatHistories = new ArrayList<>();
        this.userRights = new UserRights();
        this.pendingFriendRequests = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //default methods, implement them again if program does not work for any obvious reason


    public String getUsername() {
        return "";
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<User> friendsList) {
        this.friendsList = friendsList;
    }

    public List<Film> getWatchList() {
        return watchList;
    }

    public void setWatchList(List<Film> watchList) {
        this.watchList = watchList;
    }

    public List<FilmRating> getFilmsRatings() {
        return filmsRatings;
    }

    public void setFilmsRatings(List<FilmRating> filmsRatings) {
        this.filmsRatings = filmsRatings;
    }

    public String getLatestSecretKey() {
        return latestSecretKey;
    }

    public void setLatestSecretKey(String latestSecretKey) {
        this.latestSecretKey = latestSecretKey;
    }

    public List<ChatHistory> getChatHistories() {
        return chatHistories;
    }

    public void setChatHistories(List<ChatHistory> chatHistories) {
        this.chatHistories = chatHistories;
    }

    public UserRights getUserRights() {
        return userRights;
    }

    public void setUserRights(UserRights userRights) {
        this.userRights = userRights;
    }

    public List<FriendRequest> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(List<FriendRequest> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public List<FriendRequest> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public void setPendingFriendRequests(List<FriendRequest> pendingFriendRequests) {
        this.pendingFriendRequests = pendingFriendRequests;
    }
}
