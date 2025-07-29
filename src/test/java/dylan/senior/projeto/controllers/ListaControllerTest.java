package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoListaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroListaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoListaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemListaDTO;
import dylan.senior.projeto.entities.Lista;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ListaRepository;
import dylan.senior.projeto.services.ListaService;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListaControllerTest {

    @InjectMocks
    private ListaController listaController;

    @Mock
    private ListaService listaService;

    @Mock
    private ListaRepository listaRepository;

    private Lista lista;

    @BeforeEach
    public void setUp() {
        Usuario usuario = new Usuario(1L, "login", "nome", "senha", new HashSet<>(), new HashSet<>(), new HashSet<>());
        lista = new Lista(1L, "nome", "descricao", usuario, new HashSet<>());
    }

    // ---- Testes 'cadastrar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste1() {

        CadastroListaDTO dados = new CadastroListaDTO("nome", "descricao", 1L);

        when(listaService.criarLista(dados)).thenReturn(lista);
        when(listaRepository.save(any(Lista.class))).thenReturn(lista);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<ListagemListaDTO> response = listaController.cadastrar(dados, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().descricao());
                }
        );


        verify(listaService).criarLista(dados);
        verify(listaRepository).save(any(Lista.class));

    }

    // ---- Testes 'listar' ---- //

    @Test
    @DisplayName("Teste 'listar'")
    public void teste2() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Lista> pageListas = new PageImpl<>(List.of(lista));
        when(listaRepository.findAll(pageable)).thenReturn(pageListas);


        ResponseEntity<Page<ListagemListaDTO>> response = listaController.listar(pageable);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> assertNotNull(response.getBody()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(1, response.getBody().getTotalElements());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().getContent().get(0).nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().getContent().get(0).descricao());
                }
        );

        verify(listaRepository).findAll(pageable);

    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': lista válida")
    public void teste3() {

        when(listaRepository.findById(1L)).thenReturn(Optional.of(lista));

        ResponseEntity<DetalhamentoListaDTO> response = listaController.detalhar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().descricao());
                }
        );

        verify(listaRepository).findById(1L);
    }
    @Test
    @DisplayName("Teste 'detalhar': lista não encontrada")
    public void teste4() {
        when(listaRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Lista não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': lista válida")
    public void teste5() {

        AlteracaoListaDTO dados = new AlteracaoListaDTO("nome", "descricao");

        when(listaService.alterar(dados, 1L)).thenReturn(lista);

        ResponseEntity<ListagemListaDTO> response = listaController.alterar(1L, dados);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().descricao());
                }
        );

        verify(listaService).alterar(dados, 1L);

    }

    // ---- Testes 'deletar' ---- //

    @Test
    @DisplayName("Testes 'deletar': lista válida")
    public void teste6() {

        when(listaRepository.findById(1L)).thenReturn(Optional.of(lista));

        ResponseEntity<String> response = listaController.deletar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Lista deletada com sucesso!", response.getBody());
                }
        );

        verify(listaRepository).findById(1L);
        verify(listaRepository).delete(lista);
    }
    @Test
    @DisplayName("Testes 'deletar': lista não encontrada")
    public void teste7() {
        when(listaRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Lista não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'adicionarReceita' ---- //

    @Test
    @DisplayName("Teste 'adicionarReceita'")
    public void teste8() {
        when(listaService.adicionarReceita(1L, 1L)).thenReturn(lista);

        ResponseEntity<DetalhamentoListaDTO> response = listaController.adicionarReceita(1L, 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().descricao());
                }
        );

        verify(listaService).adicionarReceita(1L, 1L);
    }

    // ---- Testes 'removerReceita' ---- //

    @Test
    @DisplayName("Teste 'removerReceita'")
    public void teste9() {
        when(listaService.removerReceita(1L, 1L)).thenReturn(lista);

        ResponseEntity<DetalhamentoListaDTO> response = listaController.removerReceita(1L, 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().descricao());
                }
        );

        verify(listaService).removerReceita(1L, 1L);
    }

    // ---- Testes 'listarPorUsuario' ---- //

    @Test
    @DisplayName("Teste 'listarPorUsuario'")
    public void teste10() {
        when(listaService.listarPorUsuario(1L)).thenReturn(List.of(new ListagemListaDTO(lista)));

        ResponseEntity<List<ListagemListaDTO>> response = listaController.listarPorUsuario(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> assertNotNull(response.getBody()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(1, response.getBody().size());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().get(0).nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("descricao", response.getBody().get(0).descricao());
                }
        );

        verify(listaService).listarPorUsuario(1L);
    }

}