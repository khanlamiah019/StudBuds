package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    @Test
    void testLoginRequestGettersAndSetters() {
        LoginRequest request = new LoginRequest();
        
        request.setEmail("user@example.com");
        request.setPassword("securepassword");
        
        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("securepassword");
    }
}