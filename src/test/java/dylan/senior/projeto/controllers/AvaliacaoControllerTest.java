package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoAvaliacaoDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoAvaliacaoDTO;
import dylan.senior.projeto.dtos.listagem.ListagemAvaliacaoDTO;
import dylan.senior.projeto.entities.Avaliacao;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import dylan.senior.projeto.services.AvaliacaoService;
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
class AvaliacaoControllerTest {

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    @Mock
    private AvaliacaoService avaliacaoService;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    private Avaliacao avaliacao;

    @BeforeEach
    public void setUp() {
        Usuario usuario = new Usuario();
        Receita receita = new Receita();
        avaliacao = new Avaliacao(8, "Ok", usuario, receita);
    }

    // ---- Testes 'cadastrar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste1() {

        CadastroAvaliacaoDTO dados = new CadastroAvaliacaoDTO(8, "Ok", 1L, 2L);

        when(avaliacaoService.criarAvaliacao(dados)).thenReturn(avaliacao);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<DetalhamentoAvaliacaoDTO> response = avaliacaoController.cadastrar(dados, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(8, response.getBody().nota());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ok", response.getBody().comentario());
                }
        );


        verify(avaliacaoService).criarAvaliacao(dados);
        verify(avaliacaoRepository).save(any(Avaliacao.class));

    }

    // ---- Testes 'listar' ---- //

    @Test
    @DisplayName("Teste 'listar'")
    public void teste2() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Avaliacao> pageAvaliacoes = new PageImpl<>(List.of(avaliacao));
        when(avaliacaoRepository.findAll(pageable)).thenReturn(pageAvaliacoes);


        ResponseEntity<Page<ListagemAvaliacaoDTO>> response = avaliacaoController.listar(pageable);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> assertNotNull(response.getBody()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(1, response.getBody().getTotalElements());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(8, response.getBody().getContent().get(0).nota());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ok", response.getBody().getContent().get(0).comentario());
                }
        );

        verify(avaliacaoRepository).findAll(pageable);

    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': avaliação válida")
    public void teste3() {

        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        ResponseEntity<DetalhamentoAvaliacaoDTO> response = avaliacaoController.detalhar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(8, response.getBody().nota());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ok", response.getBody().comentario());
                }
        );

        verify(avaliacaoRepository).findById(1L);
    }
    @Test
    @DisplayName("Teste 'detalhar': avaliação não encontrada")
    public void teste4() {
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Avaliação não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> avaliacaoController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': avaliação válida")
    public void teste5() {

        AlteracaoAvaliacaoDTO dados = new AlteracaoAvaliacaoDTO(9, "Bom!");

        when(avaliacaoService.alterar(1L, dados)).thenReturn(avaliacao);

        ResponseEntity<ListagemAvaliacaoDTO> response = avaliacaoController.alterar(1L, dados);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(8, response.getBody().nota());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Ok", response.getBody().comentario());
                }
        );

        verify(avaliacaoService).alterar(1L, dados);

    }

    // ---- Testes 'deletar' ---- //

    @Test
    @DisplayName("Testes 'deletar': avaliação válida")
    public void teste6() {

        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

        ResponseEntity<String> response = avaliacaoController.deletar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Avaliação deletada com sucesso!", response.getBody());
                }
        );

        verify(avaliacaoRepository).findById(1L);
        verify(avaliacaoRepository).delete(avaliacao);
    }
    @Test
    @DisplayName("Testes 'deletar': avaliação não encontrada")
    public void teste7() {
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Avaliação não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> avaliacaoController.detalhar(1L)).getMessage());
    }

}