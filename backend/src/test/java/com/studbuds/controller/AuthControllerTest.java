package com.studbuds.controller;

import com.google.firebase.auth.*;
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

        loginReq = new LoginRequest();
        loginReq.setFirebaseToken("good-token");

        deleteReq = new DeleteAccountRequest();
        deleteReq.setFirebaseToken("good-token");
    }

    // ─── SIGNUP ───────────────────────────────────────────────────────────────

   @Test
void signUp_success() throws Exception {
    when(userRepository.findByEmailIgnoreCase(anyString()))
        .thenReturn(Optional.empty());

    FirebaseToken mockToken = mock(FirebaseToken.class);
    when(mockToken.getUid()).thenReturn("uid-xyz");
    when(firebaseAuth.verifyIdToken(anyString())).thenReturn(mockToken);

    when(userRepository.findByFirebaseUid("uid-xyz"))
        .thenReturn(Optional.empty());

    when(userRepository.save(any(User.class)))
        .thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(99L);
            return u;
        });

    when(preferenceRepository.save(any(Preference.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    signupReq.setFirebaseToken("dummy-token");

    ResponseEntity<?> resp = authController.signUp(signupReq);
    assertEquals(HttpStatus.CREATED, resp.getStatusCode());

    @SuppressWarnings("unchecked")
    Map<String, ?> body = (Map<String, ?>) resp.getBody();
    assertEquals("User registered successfully.", body.get("message"));
    assertEquals(99L, body.get("userId"));
}
    // ─── LOGIN ────────────────────────────────────────────────────────────────

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
