package com.gruppen.filmdatabase.entity;

//https://www.baeldung.com/thymeleaf-enums
public enum FilmInvitationStatus {
    ACCEPTED("Accepted"),
    DECLINED("Declined"),
    PENDING("Pending");

    private final String displayValue;

    private FilmInvitationStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
