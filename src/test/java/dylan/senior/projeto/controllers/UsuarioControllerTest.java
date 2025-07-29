package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoUsuarioDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoUsuarioDTO;
import dylan.senior.projeto.dtos.listagem.ListagemUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.services.UsuarioService;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValidadorUsuario validadorUsuario;

    @Mock
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
    }

    // ---- Testes 'cadastrar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste1() {

        CadastroUsuarioDTO dados = new CadastroUsuarioDTO("ana@gmail", "superSenha123!", "Ana Teste");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<DetalhamentoUsuarioDTO> response = usuarioController.cadastrar(dados, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ana Teste", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("superSenha123!", response.getBody().senha());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("ana@gmail", response.getBody().login());
                }
        );


        verify(validadorUsuario).validar(dados);
        verify(usuarioRepository).save(any(Usuario.class));

    }

    // ---- Testes 'listar' ---- //

    @Test
    @DisplayName("Teste 'listar'")
    public void teste2() {
        usuario.setNome("Ana Teste");
        usuario.setLogin("ana@gmail");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Usuario> pageUsers = new PageImpl<>(List.of(usuario));
        when(usuarioRepository.findAll(pageable)).thenReturn(pageUsers);


        ResponseEntity<Page<ListagemUsuarioDTO>> response = usuarioController.listar(pageable);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> assertNotNull(response.getBody()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(1, response.getBody().getTotalElements());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ana Teste", response.getBody().getContent().get(0).nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("ana@gmail", response.getBody().getContent().get(0).login());
                }

        );

        verify(usuarioRepository).findAll(pageable);
    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': usuário válido")
    public void teste3() {
        usuario.setNome("Ana Teste");
        usuario.setLogin("ana@gmail");
        usuario.setSenha("superSenha123!");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<DetalhamentoUsuarioDTO> response = usuarioController.detalhar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ana Teste", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("ana@gmail", response.getBody().login());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("superSenha123!", response.getBody().senha());
                }
        );

        verify(usuarioRepository).findById(1L);
    }
    @Test
    @DisplayName("Teste 'detalhar': usuário não encontrado")
    public void teste4() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': usuário válido")
    public void teste5() {
        usuario.setNome("Ana Teste");
        usuario.setLogin("ana@gmail");
        usuario.setSenha("superSenha123!");
        AlteracaoUsuarioDTO dados = new AlteracaoUsuarioDTO("ana@gmail", "Ana Teste", "superSenha123!");

        when(usuarioService.alterar(dados, 1L)).thenReturn(usuario);

        ResponseEntity<DetalhamentoUsuarioDTO> response = usuarioController.alterar(dados, 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ana Teste", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("ana@gmail", response.getBody().login());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("superSenha123!", response.getBody().senha());
                }
                );

        verify(usuarioService).alterar(dados, 1L);

    }

    // ---- Testes 'deletar' ---- //

    @Test
    @DisplayName("Testes 'deletar': usuário válido")
    public void teste6() {

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        ResponseEntity<String> response = usuarioController.deletar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Usuário deletado com sucesso!", response.getBody());
                }
                );

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).delete(usuario);
    }
    @Test
    @DisplayName("Testes 'deletar': usuário não encontrado")
    public void teste7() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> usuarioController.detalhar(1L)).getMessage());
    }


}