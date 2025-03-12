package com.studbuds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters (including CSRF)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private PreferenceRepository preferenceRepository;
    
    @MockBean
    private MatchRepository matchRepository;
    
    @MockBean
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    public void setup() {
        // Clear the registered emails for a clean test environment.
        SignupRequest.registeredEmails.clear();
    }
    
    @Test
    public void testSignUp_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@cooper.edu");
        signupRequest.setPassword("password123");
        signupRequest.setMajor("Computer Science");
        signupRequest.setYear("2025");
        
        // Simulate that user does not exist
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.empty());
        // Simulate password encoding and saving
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User registered successfully")));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testSignUp_EmailAlreadyExists() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@cooper.edu");
        signupRequest.setPassword("password123");
        signupRequest.setMajor("Computer Science");
        signupRequest.setYear("2025");
        
        // Simulate email already present in the repository
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(new User()));
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email already in use")));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testLogin_UserNotFound() throws Exception {
        // Pre-populate so that the setter in LoginRequest does not throw.
        SignupRequest.registeredEmails.add("nonexistent@cooper.edu");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@cooper.edu");
        loginRequest.setPassword("password123");
        
        when(userRepository.findByEmail("nonexistent@cooper.edu")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
        
        verify(userRepository, times(1)).findByEmail("nonexistent@cooper.edu");
    }
    
    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        SignupRequest.registeredEmails.add("test@cooper.edu");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@cooper.edu");
        loginRequest.setPassword("wrongpassword");
        
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid credentials")));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }
    
    @Test
    public void testLogin_Success() throws Exception {
        SignupRequest.registeredEmails.add("test@cooper.edu");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@cooper.edu");
        loginRequest.setPassword("password123");
        
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(jsonPath("$.userId").value(1));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }
    
    @Test
    public void testDeleteAccount_UserNotFound() throws Exception {
        SignupRequest.registeredEmails.add("nonexistent@cooper.edu");
        
        DeleteAccountRequest deleteRequest = new DeleteAccountRequest();
        deleteRequest.setEmail("nonexistent@cooper.edu");
        deleteRequest.setPassword("password123");
        
        when(userRepository.findByEmail("nonexistent@cooper.edu")).thenReturn(Optional.empty());
        
        mockMvc.perform(delete("/api/auth/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
        
        verify(userRepository, times(1)).findByEmail("nonexistent@cooper.edu");
    }
    
    @Test
    public void testDeleteAccount_InvalidCredentials() throws Exception {
        SignupRequest.registeredEmails.add("test@cooper.edu");
        
        DeleteAccountRequest deleteRequest = new DeleteAccountRequest();
        deleteRequest.setEmail("test@cooper.edu");
        deleteRequest.setPassword("wrongpassword");
        
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);
        
        mockMvc.perform(delete("/api/auth/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid credentials")));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }
    
    @Test
    public void testDeleteAccount_Success() throws Exception {
        SignupRequest.registeredEmails.add("test@cooper.edu");
        
        DeleteAccountRequest deleteRequest = new DeleteAccountRequest();
        deleteRequest.setEmail("test@cooper.edu");
        deleteRequest.setPassword("password123");
        
        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setPassword("encodedPassword");
        
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        // Simulate no preference associated with the user
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.empty());
        // Simulate that matchRepository.findAllByUser(user) returns an empty list
        when(matchRepository.findAllByUser(user)).thenReturn(Collections.emptyList());
        
        mockMvc.perform(delete("/api/auth/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Account deleted successfully")));
        
        verify(userRepository, times(1)).findByEmail("test@cooper.edu");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
        verify(preferenceRepository, times(1)).findByUser(user);
        verify(matchRepository, times(1)).findAllByUser(user);
        verify(userRepository, times(1)).delete(user);
    }
}
