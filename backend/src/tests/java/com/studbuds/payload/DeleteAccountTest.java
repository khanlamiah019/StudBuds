package com.studbuds.payload;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DeleteAccountRequestTest {

    @Test
    void testDeleteAccountRequestGettersAndSetters() {
        DeleteAccountRequest request = new DeleteAccountRequest();
        
        request.setEmail("user@example.com");
        request.setPassword("password123");
        
        assertThat(request.getEmail()).isEqualTo("user@example.com");
        assertThat(request.getPassword()).isEqualTo("password123");
    }
}
