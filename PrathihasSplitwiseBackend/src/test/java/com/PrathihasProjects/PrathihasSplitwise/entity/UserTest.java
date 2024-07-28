package com.PrathihasProjects.PrathihasSplitwise.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class UserTest {

    @Test
    void testUserConstructor() {
        User user = new User("testUser", "testPassword");
        assertEquals("testUser", user.getUsername(), "testing getUsername in constructor");
        assertEquals("testPassword", user.getPassword(), "testing getPassword in constructor");
    }

    @Test
    void testSetUsername() {
        User user = new User();
        user.setUsername("newUser");
        assertEquals("newUser", user.getUsername(), "testing setUsername");
    }

    @Test
    void testSetPassword() {
        User user = new User();
        user.setPassword("newPassword");
        assertEquals("newPassword", user.getPassword(), "testing setPassword");
    }

    @Test
    void testToString() {
        User user = new User("testUser", "testPassword");
        String expected = "User{username='testUser', password='testPassword'}";
        assertEquals(expected, user.toString(), "testing toString");
    }

    @Test
    void testGetUsername() {
        User user = new User("testUser", "testPassword");
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void testGetPassword() {
        User user = new User("testUser", "testPassword");
        assertEquals("testPassword", user.getPassword());
    }
}
