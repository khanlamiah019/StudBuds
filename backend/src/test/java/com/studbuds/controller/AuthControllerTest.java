package com.studbuds.controller;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.SwipeRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UserRepository userRepository;
    @Mock private PreferenceRepository preferenceRepository;
    @Mock private SwipeRepository swipeRepository;
    @Mock private MatchRepository matchRepository;
    @Mock private FirebaseAuth firebaseAuth;

    @InjectMocks private AuthController authController;

    private SignupRequest signupReq;
    private LoginRequest loginReq;
    private DeleteAccountRequest deleteReq;

    @BeforeEach
    void init() {
        signupReq = new SignupRequest();
        signupReq.setEmail("student@cooper.edu");
        signupReq.setPassword("strongPass123");
        signupReq.setName("Student Name");
        signupReq.setMajor("EE");
        signupReq.setYear("Junior");
        signupReq.setFirebaseToken("mock-token");

        loginReq = new LoginRequest();
        loginReq.setFirebaseToken("good-token");

        deleteReq = new DeleteAccountRequest();
        deleteReq.setFirebaseToken("good-token");
    }

   @Test
void signUp_success() throws Exception {
    // Mock Firebase token verification
    FirebaseToken mockToken = mock(FirebaseToken.class);
    when(mockToken.getUid()).thenReturn("firebase123");
    when(firebaseAuth.verifyIdToken(anyString())).thenReturn(mockToken);

    // Simulate email cleanup: user with same email already exists
    User existingUser = new User();
    existingUser.setId(88L);
    existingUser.setEmail("test@cooper.edu");
    when(userRepository.findByEmailIgnoreCase("test@cooper.edu")).thenReturn(Optional.of(existingUser));
    when(swipeRepository.findByFromUser(existingUser)).thenReturn(Collections.emptyList());
    when(swipeRepository.findByToUser(existingUser)).thenReturn(Collections.emptyList());
    when(matchRepository.findByUser1OrUser2(existingUser, existingUser)).thenReturn(Collections.emptyList());

    // No UID conflict
    when(userRepository.findByFirebaseUid("firebase123")).thenReturn(Optional.empty());

    // Simulate saveAndFlush to return user with ID
    when(userRepository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
        User savedUser = invocation.getArgument(0);
        savedUser.setId(99L); // ✅ simulate generated ID
        return savedUser;
    });

    // Preference creation
    when(preferenceRepository.save(any(Preference.class))).thenReturn(new Preference());

    // Prepare request body
    SignupRequest req = new SignupRequest();
    req.setEmail("test@cooper.edu");
    req.setPassword("123456789");
    req.setName("Celina");
    req.setFirebaseToken("mock-token");
    req.setMajor("Electrical Engineering");
    req.setYear("2025");

    // Perform signup
    ResponseEntity<?> response = authController.signUp(req);

    // Assert success
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, Object> body = (Map<String, Object>) response.getBody();
    assertEquals("User registered successfully.", body.get("message"));
    assertEquals(99L, body.get("userId")); // ✅ now passes
}
    
    @Test
    void login_success() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        User user = new User();
        user.setId(55L);
        when(userRepository.findByFirebaseUid("uid-xyz")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.login(loginReq, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertEquals("Login successful.", body.get("message"));
        assertEquals(55L, body.get("userId"));
    }

    @Test
    void login_missingToken() {
        ResponseEntity<?> response = authController.login(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Missing Firebase token", response.getBody());
    }

    @Test
    void login_invalidToken() throws Exception {
        when(firebaseAuth.verifyIdToken(anyString())).thenThrow(mock(FirebaseAuthException.class));
        ResponseEntity<?> response = authController.login(loginReq, null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Firebase token.", response.getBody());
    }

    @Test
    void login_noLocalUser() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);
        when(userRepository.findByFirebaseUid("uid-xyz")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(loginReq, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No local account found. Please sign up first.", response.getBody());
    }

    @Test
    void deleteAccount_success() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        User user = new User();
        when(userRepository.findByFirebaseUid("uid-xyz")).thenReturn(Optional.of(user));
        when(matchRepository.findByUser1OrUser2(user, user)).thenReturn(Collections.emptyList());
        when(swipeRepository.findByFromUser(user)).thenReturn(Collections.emptyList());
        when(swipeRepository.findByToUser(user)).thenReturn(Collections.emptyList());
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(new Preference()));

        ResponseEntity<?> response = authController.deleteAccount(deleteReq, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertEquals("Account deleted successfully.", body.get("message"));
    }

    @Test
    void deleteAccount_userNotFound() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);
        when(userRepository.findByFirebaseUid("uid-xyz")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.deleteAccount(deleteReq, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertEquals("User not found in local DB.", body.get("message"));
    }

    @Test
    void deleteAccount_firebaseError() throws Exception {
        FirebaseAuthException err = mock(FirebaseAuthException.class);
        when(err.getMessage()).thenReturn("boom");
        when(firebaseAuth.verifyIdToken("good-token")).thenThrow(err);

        ResponseEntity<?> response = authController.deleteAccount(deleteReq, null);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, ?> body = (Map<String, ?>) response.getBody();
        assertTrue(((String) body.get("message")).contains("Invalid Firebase token"));
    }
}
