package com.studbuds.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private FirebaseAuth firebaseAuth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignUp_Success() throws FirebaseAuthException {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@cooper.edu");
        signupRequest.setPassword("password123");
        signupRequest.setMajor("Computer Science");
        signupRequest.setYear("Sophomore");

        // Simulate that email is not already in use.
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        UserRecord mockUserRecord = mock(UserRecord.class);
        when(mockUserRecord.getDisplayName()).thenReturn("Test User");
        when(mockUserRecord.getEmail()).thenReturn("test@cooper.edu");
        when(mockUserRecord.getUid()).thenReturn("firebase-uid-123");
        when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class)))
                .thenReturn(mockUserRecord);

        // Simulate saving user and preference.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(preferenceRepository.save(any(Preference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = authController.signUp(signupRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
    }

    @Test
    void testSignUp_EmailAlreadyInUse() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@cooper.edu");
        signupRequest.setPassword("password123");
        signupRequest.setMajor("Computer Science");
        signupRequest.setYear("Sophomore");

        User existingUser = new User();
        existingUser.setEmail("test@cooper.edu");
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(existingUser));

        ResponseEntity<?> response = authController.signUp(signupRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email already in use.", response.getBody());
    }

    @Test
    void testLogin_Success() throws FirebaseAuthException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setFirebaseToken("valid-token");

        FirebaseToken mockDecodedToken = mock(FirebaseToken.class);
        when(mockDecodedToken.getEmail()).thenReturn("test@cooper.edu");
        when(firebaseAuth.verifyIdToken("valid-token")).thenReturn(mockDecodedToken);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.login(loginRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseMap = (Map<?, ?>) response.getBody();
        assertEquals("Login successful.", responseMap.get("message"));
        assertEquals(1L, responseMap.get("userId"));
    }

    @Test
    void testDeleteAccount_Success() throws FirebaseAuthException {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setEmail("test@cooper.edu");
        request.setFirebaseToken("valid-token");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@cooper.edu");
        user.setFirebaseUid("firebase-uid-123");

        when(userRepository.findByEmail("test@cooper.edu")).thenReturn(Optional.of(user));
        doNothing().when(firebaseAuth).deleteUser("firebase-uid-123");
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(new Preference()));
        doNothing().when(matchRepository).deleteAll(anyList());
        doNothing().when(userRepository).delete(user);

        ResponseEntity<?> response = authController.deleteAccount(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Account deleted successfully.", response.getBody());
    }

    @Test
    void testDeleteAccount_UserNotFound() {
        DeleteAccountRequest request = new DeleteAccountRequest();
        request.setEmail("notfound@cooper.edu");
        request.setFirebaseToken("valid-token");

        when(userRepository.findByEmail("notfound@cooper.edu")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.deleteAccount(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }
}