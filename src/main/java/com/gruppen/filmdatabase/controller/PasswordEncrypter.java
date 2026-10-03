package com.gruppen.filmdatabase.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncrypter {

    public static void main(String[] args) {
        BCryptPasswordEncoder encrypter = new BCryptPasswordEncoder();
        String rawPassword = "";
        String encryptedPassword = encrypter.encode(rawPassword);

        System.out.println(encryptedPassword);
    }
}
