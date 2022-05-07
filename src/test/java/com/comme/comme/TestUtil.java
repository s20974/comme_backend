package com.comme.comme;

import com.comme.comme.user.User;

public class TestUtil {
    public static User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setSurname("test-surnae");
        user.setPassword("P@ssw0rd");
        user.setImage("image.png");
        return user;
    }
}
