package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValidadorUsuario validadorUsuario;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': dados inválidos")
    public void teste1() {
        AlteracaoUsuarioDTO dados = new AlteracaoUsuarioDTO("alterado", "com", "sucesso");

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("sucesso")).thenReturn("sucesso!");

        Usuario resultado = usuarioService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("alterado", resultado.getLogin()),
                () -> assertEquals("com", usuario.getNome()),
                () -> assertEquals("sucesso!", usuario.getSenha()),
                () -> assertEquals(resultado, usuario)
                );
    }
    @Test
    @DisplayName("Teste 'alterar': usuário não encontrado")
    public void teste2() {
        AlteracaoUsuarioDTO dados = new AlteracaoUsuarioDTO("alterado", "com", "sucesso");

        when(usuarioRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioService.alterar(dados, 4L)).getMessage());
    }
    @Test
    @DisplayName("Teste 'alterar': dados vazios")
    public void teste3() {
        AlteracaoUsuarioDTO dados = new AlteracaoUsuarioDTO("       ", " ", "");
        usuario.setLogin("alterado");
        usuario.setNome("com");
        usuario.setSenha("sucesso");

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("alterado", resultado.getLogin()),
                () -> assertEquals("com", usuario.getNome()),
                () -> assertEquals("sucesso", usuario.getSenha()),
                () -> assertEquals(resultado, usuario)
        );
    }
    @Test
    @DisplayName("Teste 'alterar': dados vazios")
    public void teste4() {
        AlteracaoUsuarioDTO dados = new AlteracaoUsuarioDTO(null, null, null);
        usuario.setLogin("alterado");
        usuario.setNome("com");
        usuario.setSenha("sucesso");

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("alterado", resultado.getLogin()),
                () -> assertEquals("com", usuario.getNome()),
                () -> assertEquals("sucesso", usuario.getSenha()),
                () -> assertEquals(resultado, usuario)
        );
    }
}