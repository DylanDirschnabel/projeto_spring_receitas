package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.autenticacao.LoginDTO;
import dylan.senior.projeto.dtos.autenticacao.LoginRespostaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.security.TokenService;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private ValidadorUsuario validadorUsuario;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario(1L, "usuario", "Usuário Teste", "senhaCodificada", new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    // ---- Testes 'login' ---- //

    @Test
    @DisplayName("Teste 'login': dados válidos")
    public void teste1() {

        LoginDTO loginDTO = new LoginDTO("usuario", "senha123");


        when(usuarioRepository.findByLogin("usuario")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "senhaCodificada")).thenReturn(true);
        when(tokenService.generateToken(usuario)).thenReturn("token123");

        ResponseEntity resposta = authController.login(loginDTO);

        // Assert
        assertEquals("200 OK", resposta.getStatusCode().toString());
        LoginRespostaDTO corpo = (LoginRespostaDTO) resposta.getBody();
        assertNotNull(corpo);
        assertEquals("Usuário Teste", corpo.nome());
        assertEquals("token123", corpo.token());
    }
    @Test
    @DisplayName("Teste 'login': dados inválidos")
    public void teste2() {
        LoginDTO loginDTO = new LoginDTO("usuario", "senhaErrada");

        when(usuarioRepository.findByLogin("usuario")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senhaErrada", "senhaCodificada")).thenReturn(false);

        ResponseEntity resposta = authController.login(loginDTO);

        assertEquals("400 BAD_REQUEST", resposta.getStatusCode().toString());
    }

    // ---- Testes 'register' ---- //

    @Test
    @DisplayName("Teste 'register': dados válidos")
    public void teste3() {
        CadastroUsuarioDTO cadastroDTO = new CadastroUsuarioDTO("novoUsuario", "senha123", "Novo Usuário");

        when(usuarioRepository.findByLogin("novoUsuario")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCodificada");
        when(tokenService.generateToken(any())).thenReturn("tokenNovo");

        ResponseEntity resposta = authController.register(cadastroDTO);

        assertEquals("200 OK", resposta.getStatusCode().toString());
        LoginRespostaDTO corpo = (LoginRespostaDTO) resposta.getBody();
        assertNotNull(corpo);
        assertEquals("Novo Usuário", corpo.nome());
        assertEquals("tokenNovo", corpo.token());

        verify(usuarioRepository).save(any(Usuario.class));
        verify(validadorUsuario).validar(cadastroDTO);
    }
    @Test
    @DisplayName("Teste 'register': login existente")
    public void teste4() {
        CadastroUsuarioDTO cadastroDTO = new CadastroUsuarioDTO("usuarioExistente", "senha123", "Usuário Existente");

        when(usuarioRepository.findByLogin("usuarioExistente")).thenReturn(Optional.of(usuario));

        ResponseEntity resposta = authController.register(cadastroDTO);

        assertEquals("400 BAD_REQUEST", resposta.getStatusCode().toString());
        verify(validadorUsuario).validar(cadastroDTO);
    }


}