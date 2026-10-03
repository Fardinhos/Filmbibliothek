package com.gruppen.filmdatabase.entity;

//https://www.baeldung.com/thymeleaf-enums

public enum UserRightsType {
    ALL("ALL"),
    NONE("NONE"),
    FRIENDS("FRIENDS");

    private final String displayValue;

    private UserRightsType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
