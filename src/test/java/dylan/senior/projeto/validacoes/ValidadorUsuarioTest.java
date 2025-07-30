package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorUsuarioTest {

    @InjectMocks
    private ValidadorUsuario validadorUsuario;

    @Mock
    private UsuarioRepository usuarioRepository;

    // ---- Testes 'validar' ---- //

    @Test
    @DisplayName("Teste 'validar': dados válidos")
    public void teste1() {
        CadastroUsuarioDTO dados = new CadastroUsuarioDTO("anaaaaaa", "12123123@#A12", "ana");
        assertDoesNotThrow(() -> validadorUsuario.validar(dados));
    }
    @Test
    @DisplayName("Teste 'validar': dados nulos")
    public void teste2() {
        assertEquals("Dados inválidos para cadastro de usuário", assertThrows(ValidacaoException.class, () -> validadorUsuario.validar(null)).getMessage());
    }

    // ---- Testes 'validarLogin' ---- //

    @Test
    @DisplayName("Teste 'validarLogin': dados válidos")
    public void teste3() {

        when(usuarioRepository.existsByLogin("ana123456")).thenReturn(false);

        validadorUsuario.validarLogin("ana123456");

        verify(usuarioRepository).existsByLogin("ana123456");

    }
    @Test
    @DisplayName("Teste 'validarLogin': login já existe")
    public void teste4() {
        when(usuarioRepository.existsByLogin("ana123456")).thenReturn(true);

        assertEquals("Erro: login já cadastrado", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarLogin("ana123456")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarLogin': login menor que 8 caracteres")
    public void teste5() {
        when(usuarioRepository.existsByLogin("ana")).thenReturn(false);

        assertEquals("Erro: login deve ter entre 8 a 50 caracteres", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarLogin("ana")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarLogin': login maior que 50 caracteres")
    public void teste6() {
        when(usuarioRepository.existsByLogin("123456789012345678901234567890123456789012345678901")).thenReturn(false);

        assertEquals("Erro: login deve ter entre 8 a 50 caracteres", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarLogin("123456789012345678901234567890123456789012345678901")).getMessage());
    }

    // ---- Testes 'validarSenha' ---- //

    @Test
    @DisplayName("Teste 'validarSenha': senha válida")
    public void teste7() {
        assertDoesNotThrow(() -> validadorUsuario.validarSenha("SenhaForte14321@!$"));
    }
    @Test
    @DisplayName("Teste 'validarSenha': senha menor que 8 caracteres")
    public void teste8() {
        assertEquals("Erro: senha deve ter entre 8 a 50 caracteres", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarSenha("senha")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarSenha': senha maior que 50 caracteres")
    public void teste9() {
        assertEquals("Erro: senha deve ter entre 8 a 50 caracteres", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarSenha("123456789012345678901234567890123456789012345678901")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarSenha': senha com nenhum número")
    public void teste10() {
        assertEquals("Erro: senha deve conter pelo menos um número", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarSenha("senhaaaaaa")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarSenha': senha sem letra maiúscula")
    public void teste11() {
        assertEquals("Erro: senha deve conter pelo menos uma letra em maiúsculo", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarSenha("senhaaaaaa1")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validarSenha': senha sem caractere especial")
    public void teste12() {
        assertEquals("Erro: senha deve conter pelo menos algum desses caracteres: [!, @, #, $, %, &, ?]", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarSenha("senhaaaaaa1A")).getMessage());
    }

    // ---- Testes 'validarAutenticacao' ---- //

    @Test
    @DisplayName("Teste 'validarAutenticacao': usuário válido")
    public void teste13() {

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(usuarioMock);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertDoesNotThrow(() -> validadorUsuario.validarAutenticacao(1L));

    }
    @Test
    @DisplayName("Teste 'validarAutenticacao': usuário inválido")
    public void teste14() {

        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(usuarioMock);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        assertEquals("Erro: usuário inválido", assertThrows(ValidacaoException.class, () -> validadorUsuario.validarAutenticacao(2L)).getMessage());
    }

}