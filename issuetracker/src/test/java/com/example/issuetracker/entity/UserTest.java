package com.example.issuetracker.entity;

import com.example.issuetracker.entity.Role;
import com.example.issuetracker.entity.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreation() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void testRoleEnum() {
        assertEquals("ADMIN", Role.ADMIN.name());
        assertEquals("USER", Role.USER.name());
    }
}