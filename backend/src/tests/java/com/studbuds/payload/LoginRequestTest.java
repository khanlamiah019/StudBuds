import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LoginRequestTest {

    @Test
    void testLoginRequest() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String testEmail = "test@example.com";
        String testPassword = "securepassword";

        // Act
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        // Assert
        assertEquals(testEmail, loginRequest.getEmail());
        assertEquals(testPassword, loginRequest.getPassword());
    }
}
