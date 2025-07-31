package dylan.senior.projeto.infra.security;

import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.repositories.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    // ---- Testes 'loadUserByUsername' ---- //

    @Test
    @DisplayName("Teste 'loadUserByUsername': usuário existe")
    void teste1() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setLogin("dylan");
        usuario.setSenha("senha123");

        when(usuarioRepository.findByLogin("dylan")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = userDetailsService.loadUserByUsername("dylan");

        assertNotNull(userDetails);
        assertEquals("dylan", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
        assertEquals(new ArrayList<>(), userDetails.getAuthorities().stream().toList());
    }
    @Test
    @DisplayName("Teste 'loadUserByUsername': usuário inexistente")
    void teste2() {
        when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inexistente");
        });
    }


}