package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SignupRequestTest {

    @Test
    void testSignupRequestGettersAndSetters() {
        SignupRequest request = new SignupRequest();
        
        request.setName("John Doe");
        request.setEmail("johndoe@example.com");
        request.setPassword("securepassword");
        request.setMajor("Computer Science");
        request.setYear("Sophomore");
        
        assertThat(request.getName()).isEqualTo("John Doe");
        assertThat(request.getEmail()).isEqualTo("johndoe@example.com");
        assertThat(request.getPassword()).isEqualTo("securepassword");
        assertThat(request.getMajor()).isEqualTo("Computer Science");
        assertThat(request.getYear()).isEqualTo("Sophomore");
    }
}
