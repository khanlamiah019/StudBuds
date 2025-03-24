package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserDetails_UserFound() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@cooper.edu");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = userController.getUserDetails(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserDetails_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.getUserDetails(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateUser_UserFound() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@cooper.edu");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@cooper.edu");

        ResponseEntity<?> response = userController.updateUser(1L, updatedUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        User returnedUser = (User) response.getBody();
        assertEquals("New Name", returnedUser.getName());
        assertEquals("new@cooper.edu", returnedUser.getEmail());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new@cooper.edu");

        ResponseEntity<?> response = userController.updateUser(1L, updatedUser);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdatePreference_UpdateExistingPreference() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");

        Preference existingPreference = new Preference();
        existingPreference.setAvailableDays("Monday");
        existingPreference.setSubjectsToLearn("Math");
        existingPreference.setSubjectsToTeach("Science");
        existingPreference.setMajor("CS");
        existingPreference.setYear("Sophomore");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(existingPreference));
        when(preferenceRepository.save(any(Preference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Preference updateData = new Preference();
        updateData.setAvailableDays("Tuesday");
        updateData.setSubjectsToLearn("History");
        updateData.setSubjectsToTeach("Physics");
        updateData.setMajor("Engineering");
        updateData.setYear("Junior");

        ResponseEntity<?> response = userController.updatePreference(1L, updateData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Preference savedPreference = (Preference) response.getBody();
        assertEquals("Tuesday", savedPreference.getAvailableDays());
        assertEquals("History", savedPreference.getSubjectsToLearn());
        assertEquals("Physics", savedPreference.getSubjectsToTeach());
        assertEquals("Engineering", savedPreference.getMajor());
        assertEquals("Junior", savedPreference.getYear());
    }

    @Test
    void testUpdatePreference_CreateNewPreference() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(Preference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Preference updateData = new Preference();
        updateData.setAvailableDays("Friday");
        updateData.setSubjectsToLearn("Biology");
        updateData.setSubjectsToTeach("Chemistry");
        updateData.setMajor("Science");
        updateData.setYear("Senior");

        ResponseEntity<?> response = userController.updatePreference(1L, updateData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Preference savedPreference = (Preference) response.getBody();
        assertEquals("Friday", savedPreference.getAvailableDays());
        assertEquals("Biology", savedPreference.getSubjectsToLearn());
        assertEquals("Chemistry", savedPreference.getSubjectsToTeach());
        assertEquals("Science", savedPreference.getMajor());
        assertEquals("Senior", savedPreference.getYear());
        assertEquals(user, savedPreference.getUser());
    }

    @Test
    void testUpdatePreference_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Preference updateData = new Preference();

        ResponseEntity<?> response = userController.updatePreference(1L, updateData);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
