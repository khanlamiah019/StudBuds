package com.studbuds.repository;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreferenceRepositoryTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @InjectMocks
    private PreferenceRepositoryTest preferenceRepositoryTest;

    @Test
    void testFindByUser() {
        User user = new User();
        user.setId(1L);
        
        Preference preference = new Preference();
        preference.setId(1L);
        preference.setUser(user);
        
        when(preferenceRepository.findByUser(user)).thenReturn(Optional.of(preference));
        
        Optional<Preference> foundPreference = preferenceRepository.findByUser(user);
        
        assertThat(foundPreference).isPresent();
        assertThat(foundPreference.get().getUser()).isEqualTo(user);
    }
}
