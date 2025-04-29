package com.studbuds.controller;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
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
import static org.mockito.ArgumentMatchers.*;
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

        FirebaseAuthException notFoundEx = mock(FirebaseAuthException.class);
        when(notFoundEx.getAuthErrorCode())
            .thenReturn(AuthErrorCode.USER_NOT_FOUND);
        when(firebaseAuth.getUserByEmail(anyString()))
            .thenThrow(notFoundEx);

        UserRecord record = mock(UserRecord.class);
        when(record.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.createUser(any(CreateRequest.class)))
            .thenReturn(record);

        when(userRepository.save(any(User.class)))
            .thenAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(99L);
                return u;
            });
        when(preferenceRepository.save(any(Preference.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        ResponseEntity<?> resp = authController.signUp(signupReq);
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,?> body = (Map<String,?>)resp.getBody();
        assertEquals("User registered successfully.", body.get("message"));
        assertEquals(99L, body.get("userId"));
    }

    @Test
    void signUp_badEmail() {
        signupReq.setEmail("foo@gmail.com");
        ResponseEntity<?> resp = authController.signUp(signupReq);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Email must be a @cooper.edu address", resp.getBody());
    }

    @Test
    void signUp_duplicateLocalEmail() {
        when(userRepository.findByEmailIgnoreCase(anyString()))
            .thenReturn(Optional.of(new User()));
        ResponseEntity<?> resp = authController.signUp(signupReq);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals("Email already in use. Please log in instead.", resp.getBody());
    }

    @Test
    void signUp_firebaseGetUser_otherError() throws Exception {
        when(userRepository.findByEmailIgnoreCase(anyString()))
            .thenReturn(Optional.empty());

        FirebaseAuthException otherEx = mock(FirebaseAuthException.class);
        when(otherEx.getAuthErrorCode())
            .thenReturn(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        when(otherEx.getMessage()).thenReturn("boom");
        when(firebaseAuth.getUserByEmail(anyString()))
            .thenThrow(otherEx);

        ResponseEntity<?> resp = authController.signUp(signupReq);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(((String)resp.getBody()).contains("Firebase error"));
    }

    @Test
    void signUp_firebaseCreate_fail() throws Exception {
        when(userRepository.findByEmailIgnoreCase(anyString()))
            .thenReturn(Optional.empty());

        FirebaseAuthException nf = mock(FirebaseAuthException.class);
        when(nf.getAuthErrorCode()).thenReturn(AuthErrorCode.USER_NOT_FOUND);
        when(firebaseAuth.getUserByEmail(anyString()))
            .thenThrow(nf);

        FirebaseAuthException createEx = mock(FirebaseAuthException.class);
        when(createEx.getMessage()).thenReturn("pw too weak");
        when(firebaseAuth.createUser(any(CreateRequest.class)))
            .thenThrow(createEx);

        ResponseEntity<?> resp = authController.signUp(signupReq);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertTrue(((String)resp.getBody()).contains("Firebase error"));
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @Test
    void login_success() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        User u = new User(); u.setId(55L);
        when(userRepository.findByFirebaseUid("uid-xyz"))
            .thenReturn(Optional.of(u));

        ResponseEntity<?> resp = authController.login(loginReq, null);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,?> body = (Map<String,?>)resp.getBody();
        assertEquals("Login successful.", body.get("message"));
        assertEquals(55L, body.get("userId"));
    }

    @Test
    void login_missingToken() {
        ResponseEntity<?> resp = authController.login(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Missing Firebase token", resp.getBody());
    }

    @Test
    void login_invalidToken() throws Exception {
        // Only stub the verify call; exception content isn't inspected
        FirebaseAuthException invalidEx = mock(FirebaseAuthException.class);
        when(firebaseAuth.verifyIdToken(anyString()))
            .thenThrow(invalidEx);

        ResponseEntity<?> resp = authController.login(loginReq, null);
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertEquals("Invalid Firebase token.", resp.getBody());
    }

    @Test
    void login_noLocalUser() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        when(userRepository.findByFirebaseUid("uid-xyz"))
            .thenReturn(Optional.empty());

        ResponseEntity<?> resp = authController.login(loginReq, null);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertEquals("No local account found. Please sign up first.", resp.getBody());
    }

    // ─── DELETE ACCOUNT ──────────────────────────────────────────────────────

    @Test
    void deleteAccount_success() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        User u = new User();
        when(userRepository.findByFirebaseUid("uid-xyz"))
            .thenReturn(Optional.of(u));
        when(preferenceRepository.findByUser(u))
            .thenReturn(Optional.of(new Preference()));

        ResponseEntity<?> resp = authController.deleteAccount(deleteReq, null);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,?> body = (Map<String,?>)resp.getBody();
        assertEquals("Account deleted successfully.", body.get("message"));

        verify(firebaseAuth).deleteUser("uid-xyz");
        verify(preferenceRepository).delete(any(Preference.class));
        verify(userRepository).delete(u);
    }

    @Test
    void deleteAccount_missingToken() {
        ResponseEntity<?> resp = authController.deleteAccount(null, null);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,String> body = (Map<String,String>)resp.getBody();
        assertEquals("Missing Firebase token", body.get("message"));
    }

    @Test
    void deleteAccount_userNotFound() throws Exception {
        FirebaseToken token = mock(FirebaseToken.class);
        when(token.getUid()).thenReturn("uid-xyz");
        when(firebaseAuth.verifyIdToken("good-token")).thenReturn(token);

        when(userRepository.findByFirebaseUid("uid-xyz"))
            .thenReturn(Optional.empty());

        ResponseEntity<?> resp = authController.deleteAccount(deleteReq, null);
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,String> body = (Map<String,String>)resp.getBody();
        assertEquals("User not found.", body.get("message"));
    }

    @Test
    void deleteAccount_firebaseError() throws Exception {
        // Only stub message, since controller only uses getMessage()
        FirebaseAuthException err = mock(FirebaseAuthException.class);
        when(err.getMessage()).thenReturn("boom");
        when(firebaseAuth.verifyIdToken("good-token")).thenThrow(err);

        ResponseEntity<?> resp = authController.deleteAccount(deleteReq, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String,String> body = (Map<String,String>)resp.getBody();
        assertTrue(body.get("message").contains("Firebase error: boom"));
    }
}
