package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testGetUserDetails_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setMajor("Computer Science");
        user.setYear("Junior");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(get("/api/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.major").value("Computer Science"))
                .andExpect(jsonPath("$.year").value("Junior"));
    }

    @Test
    void testGetUserDetails_NotFound() throws Exception {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/user/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Given
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setMajor("Computer Science");
        existingUser.setYear("Sophomore");
        
        User updatedUser = new User();
        updatedUser.setMajor("Electrical Engineering");
        updatedUser.setYear("Junior");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/user/{userId}", userId)
                        .contentType("application/json")
                        .content("{\"major\": \"Electrical Engineering\", \"year\": \"Junior\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.major").value("Electrical Engineering"))
                .andExpect(jsonPath("$.year").value("Junior"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        // Given
        Long userId = 1L;
        User updatedUser = new User();
        updatedUser.setMajor("Electrical Engineering");
        updatedUser.setYear("Junior");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/api/user/{userId}", userId)
                        .contentType("application/json")
                        .content("{\"major\": \"Electrical Engineering\", \"year\": \"Junior\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePreference_Success() throws Exception {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Preference preference = new Preference();
        preference.setUser(user);
        preference.setAvailableDays("Monday, Wednesday");
        preference.setSubjectsToLearn("Math");
        preference.setSubjectsToTeach("Physics");

        Preference updatedPreference = new Preference();
        updatedPreference.setAvailableDays("Tuesday, Thursday");
        updatedPreference.setSubjectsToLearn("Chemistry");
        updatedPreference.setSubjectsToTeach("Biology");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        when(preferenceRepository.save(any(Preference.class))).thenReturn(updatedPreference);

        // When & Then
        mockMvc.perform(post("/api/user/{userId}/preference", userId)
                        .contentType("application/json")
                        .content("{\"availableDays\": \"Tuesday, Thursday\", \"subjectsToLearn\": \"Chemistry\", \"subjectsToTeach\": \"Biology\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableDays").value("Tuesday, Thursday"))
                .andExpect(jsonPath("$.subjectsToLearn").value("Chemistry"))
                .andExpect(jsonPath("$.subjectsToTeach").value("Biology"));
    }

    @Test
    void testUpdatePreference_UserNotFound() throws Exception {
        // Given
        Long userId = 1L;
        Preference preference = new Preference();
        preference.setAvailableDays("Monday, Wednesday");
        preference.setSubjectsToLearn("Math");
        preference.setSubjectsToTeach("Physics");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/user/{userId}/preference", userId)
                        .contentType("application/json")
                        .content("{\"availableDays\": \"Monday, Wednesday\", \"subjectsToLearn\": \"Math\", \"subjectsToTeach\": \"Physics\"}"))
                .andExpect(status().isNotFound());
    }
}
