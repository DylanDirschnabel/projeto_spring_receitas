package dylan.senior.projeto.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dylan.senior.projeto.dtos.alteracao.AlteracaoReceitaDTO;
import dylan.senior.projeto.dtos.busca.BuscaPorNomeDTO;
import dylan.senior.projeto.dtos.busca.BuscaReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemBuscaReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroGeradoReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroReceitaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoReceitaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemReceitaDTO;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.services.ReceitaService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceitaControllerTest {

    @InjectMocks
    private ReceitaController receitaController;

    @Mock
    private ReceitaService receitaService;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private ValidadorUsuario validadorUsuario;

    private Receita receita;

    @BeforeEach
    public void setUp() {
        Usuario usuario = new Usuario(1L, "login", "nome", "senha", new HashSet<>(), new HashSet<>(), new HashSet<>());
        receita = new Receita(1L, new ArrayList<>(), "corpo", "nome", usuario, LocalDateTime.now(), new HashSet<>(), new HashSet<>());
    }

    // ---- Testes 'cadastrar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste1() {

        CadastroReceitaDTO dados = new CadastroReceitaDTO(1L, "nome", "corpo", List.of("i"), List.of("t"));

        when(receitaService.criarReceita(dados)).thenReturn(receita);
        when(receitaRepository.save(any(Receita.class))).thenReturn(receita);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<ListagemReceitaDTO> response = receitaController.cadastrar(dados, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );


        verify(receitaService).criarReceita(dados);
        verify(receitaRepository).save(any(Receita.class));

    }

    // ---- Testes 'listar' ---- //

    @Test
    @DisplayName("Teste 'listar'")
    public void teste2() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Receita> page = new PageImpl<>(List.of(receita));
        when(receitaRepository.findAll(pageable)).thenReturn(page);


        ResponseEntity<Page<ListagemReceitaDTO>> response = receitaController.listar(pageable);

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
                    assertEquals("corpo", response.getBody().getContent().get(0).corpo());
                }
        );

        verify(receitaRepository).findAll(pageable);

    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': receita válida")
    public void teste3() {

        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));
        when(receitaService.detalhar(receita)).thenReturn(new DetalhamentoReceitaDTO(null, "nome", null, null,"corpo", null, null, null ,null));

        ResponseEntity<DetalhamentoReceitaDTO> response = receitaController.detalhar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaRepository).findById(1L);
    }
    @Test
    @DisplayName("Teste 'detalhar': lista não encontrada")
    public void teste4() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> receitaController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': lista válida")
    public void teste5() {

        AlteracaoReceitaDTO dados = new AlteracaoReceitaDTO("nome", "corpo");

        when(receitaService.alterar(1L, dados)).thenReturn(receita);

        ResponseEntity<ListagemReceitaDTO> response = receitaController.alterar(1L, dados);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).alterar(1L, dados);

    }

    // ---- Testes 'deletar' ---- //

    @Test
    @DisplayName("Testes 'deletar': lista válida")
    public void teste6() {

        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        ResponseEntity<String> response = receitaController.deletar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Receita deletada com sucesso!", response.getBody());
                }
        );

        verify(receitaRepository).findById(1L);
        verify(receitaRepository).delete(receita);
    }
    @Test
    @DisplayName("Testes 'deletar': lista não encontrada")
    public void teste7() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> receitaController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'adicionarTag' ---- //

    @Test
    @DisplayName("Teste 'adicionarTag'")
    public void teste8() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        ResponseEntity<ListagemReceitaDTO> response = receitaController.adicionarTag("tag", 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).adicionarTag(receita, "tag");

    }

    // ---- Testes 'removerTag' ---- //

    @Test
    @DisplayName("Teste 'removerrTag'")
    public void teste9() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        ResponseEntity<ListagemReceitaDTO> response = receitaController.removerTag("tag", 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).removerTag(receita, "tag");

    }

    // ---- Testes 'adicionarIngrediente' ---- //

    @Test
    @DisplayName("Teste 'adicionarIngrediente'")
    public void teste10() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        ResponseEntity<ListagemReceitaDTO> response = receitaController.adicionarIngrediente("ingrediente", 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).adicionarIngrediente(receita, "ingrediente");
    }

    // ---- Testes 'removerIngrediente' ---- //

    @Test
    @DisplayName("Teste 'removerIngrediente'")
    public void teste11() {
        when(receitaRepository.findById(1L)).thenReturn(Optional.of(receita));

        ResponseEntity<ListagemReceitaDTO> response = receitaController.removerIngrediente("ingrediente", 1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).removerIngrediente(receita, "ingrediente");
    }

    // ---- Testes 'buscaPorNome' ---- //

    @Test
    @DisplayName("Teste 'buscaPorNome'")
    public void teste12() {

        when(receitaService.buscaPorNome("nome")).thenReturn(List.of(new ListagemBuscaReceitaDTO(1L, "nome", new ArrayList<>(), 5d, LocalDateTime.now(), "criador")));

        ResponseEntity<List<ListagemBuscaReceitaDTO>> response = receitaController.buscaPorNome(new BuscaPorNomeDTO("nome"));

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
                    assertEquals(5, response.getBody().get(0).media());
                }
        );

        verify(receitaService).buscaPorNome("nome");
    }

    // ---- Testes 'buscaExclusiva' ---- //

    @Test
    @DisplayName("Teste 'buscaExclusiva'")
    public void teste13() {

        BuscaReceitaDTO dados = new BuscaReceitaDTO(null, new ArrayList<>(), new ArrayList<>());
        when(receitaService.buscaExclusiva(dados)).thenReturn(List.of(new ListagemBuscaReceitaDTO(1L, "nome", new ArrayList<>(), 5d, LocalDateTime.now(), "criador")));

        ResponseEntity<List<ListagemBuscaReceitaDTO>> response = receitaController.buscaExclusiva(dados);

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
                    assertEquals(5, response.getBody().get(0).media());
                }
        );

        verify(receitaService).buscaExclusiva(dados);
    }

    // ---- Testes 'buscaInclusiva' ---- //

    @Test
    @DisplayName("Teste 'buscaExclusiva'")
    public void teste14() {

        BuscaReceitaDTO dados = new BuscaReceitaDTO(null, new ArrayList<>(), new ArrayList<>());
        when(receitaService.buscaInclusiva(dados)).thenReturn(List.of(new ListagemBuscaReceitaDTO(1L, "nome", new ArrayList<>(), 5d, LocalDateTime.now(), "criador")));

        ResponseEntity<List<ListagemBuscaReceitaDTO>> response = receitaController.buscaInclusiva(dados);

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
                    assertEquals(5, response.getBody().get(0).media());
                }
        );

        verify(receitaService).buscaInclusiva(dados);
    }

    // ---- Testes 'gerar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste15() throws JsonProcessingException {

        CadastroReceitaDTO dados = new CadastroReceitaDTO(1L, "nome", "corpo", List.of("i"), List.of("t"));
        CadastroGeradoReceitaDTO dadosGerar = new CadastroGeradoReceitaDTO(1L, null, null, null);

        when(receitaService.gerar(dadosGerar)).thenReturn(dados);
        when(receitaService.criarReceita(dados)).thenReturn(receita);
        when(receitaRepository.save(any(Receita.class))).thenReturn(receita);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<ListagemReceitaDTO> response = receitaController.gerar(dadosGerar, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("corpo", response.getBody().corpo());
                }
        );

        verify(receitaService).gerar(dadosGerar);
        verify(receitaService).criarReceita(dados);
        verify(receitaRepository).save(any(Receita.class));

    }

    // ---- Testes 'buscaRecomendada' ---- //

    @Test
    @DisplayName("Teste 'buscaRecomendada'")
    public void teste16() {

        when(receitaService.buscaPorRecomendacao(1L)).thenReturn(List.of(new ListagemBuscaReceitaDTO(1L, "nome", new ArrayList<>(), 5d, LocalDateTime.now(), "criador")));

        ResponseEntity<List<ListagemBuscaReceitaDTO>> response = receitaController.buscaRecomendada(1L);

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
                    assertEquals(5, response.getBody().get(0).media());
                }
        );

        verify(receitaService).buscaPorRecomendacao(1L);
    }

}