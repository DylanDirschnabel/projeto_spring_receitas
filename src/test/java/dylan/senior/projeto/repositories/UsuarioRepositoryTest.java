package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Mock
    public UsuarioRepository usuarioRepository;

    public Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
    }

    @Test
    @DisplayName("ExistsByLogin Test - should be true")
    public void teste1() {

        when(usuarioRepository.existsByLogin("teste")).thenReturn(true);

        assertTrue(usuarioRepository.existsByLogin("teste"));
    }

    @Test
    @DisplayName("ExistsByLogin Test - should not be true")
    public void teste2() {

        when(usuarioRepository.existsByLogin("teste")).thenReturn(false);

        assertFalse(usuarioRepository.existsByLogin("teste"));
    }


}