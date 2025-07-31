package dylan.senior.projeto.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {


    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Teste 'passwordEncoder'")
    void teste1() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.matches("senha123", encoder.encode("senha123")));
    }




}