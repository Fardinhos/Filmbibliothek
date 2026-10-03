package com.gruppen.filmdatabase.controller.twofa;

public class SecretKeyGenerator {



    public static String generateSecretKey() {
        String secretKey = "";
        for (int i = 0; i < 6; i++) {
            secretKey += (int) (Math.random() * 10);
        }
        return secretKey;
    }
}
