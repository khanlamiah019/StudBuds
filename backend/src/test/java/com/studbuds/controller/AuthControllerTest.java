package com.studbuds.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UserRepository userRepository;
    @Mock private PreferenceRepository preferenceRepository;
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
        signupReq.setFirebaseToken("dummy-token");

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

    // New user creation (with ID set manually in mock)
    User newUser = new User();
    newUser.setId(99L);
    newUser.setEmail("test@cooper.edu");
    newUser.setFirebaseUid("firebase123");
    when(userRepository.saveAndFlush(any(User.class))).thenReturn(newUser);

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
    assertEquals(99L, body.get("userId")); // ✅ this should now pass
}

    @Test
    void login_noLocalUser() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);
        when(userRepository.findByFirebaseUid("uid-xyz")).thenReturn(Optional.empty());

        ResponseEntity<?> resp = authController.login(loginReq, null);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("No local account found. Please sign up first.", resp.getBody());
    }
}
