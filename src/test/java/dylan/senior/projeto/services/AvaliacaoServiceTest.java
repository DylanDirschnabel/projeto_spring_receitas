package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoAvaliacaoDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.entities.Avaliacao;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorAvaliacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private ValidadorAvaliacao validadorAvaliacao;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    private Receita receita;

    private Usuario usuario;

    private Avaliacao avaliacao;

    @BeforeEach
    public void setUp() {
        receita = new Receita();
        usuario = new Usuario();
        avaliacao = new Avaliacao();
    }

    // ---- Testes 'criarAvaliacao' ---- //

    @Test
    @DisplayName("Teste 'criarAvaliacao': dados válidos")
    public void teste1() {
        CadastroAvaliacaoDTO dados = new CadastroAvaliacaoDTO(7, "teste", 5L, 4L);
        usuario.setNome("ana");
        receita.setNome("morango");

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(receitaRepository.findById(4L)).thenReturn(Optional.of(receita));

        Avaliacao resultado = avaliacaoService.criarAvaliacao(dados);

        assertAll("testes",
                () -> assertEquals("ana", resultado.getUsuario().getNome()),
                () -> assertEquals("morango", resultado.getReceita().getNome()),
                () -> assertEquals(7, resultado.getNota()),
                () -> assertEquals("teste", resultado.getComentario())
        );
    }
    @Test
    @DisplayName("Teste 'criarAvaliacao': usuário não cadastrado")
    public void teste2() {
        CadastroAvaliacaoDTO dados = new CadastroAvaliacaoDTO(7, "teste", 5L, 4L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 5.", assertThrows(EntidadeNaoEncontradaException.class, () -> avaliacaoService.criarAvaliacao(dados)).getMessage());
    }
    @Test
    @DisplayName("Teste 'criarAvaliacao': receita não cadastrada")
    public void teste3() {
        CadastroAvaliacaoDTO dados = new CadastroAvaliacaoDTO(7, "teste", 5L, 4L);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));
        when(receitaRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> avaliacaoService.criarAvaliacao(dados)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': dados válidos")
    public void teste4() {
        AlteracaoAvaliacaoDTO dados = new AlteracaoAvaliacaoDTO(9, "teste");

        when(avaliacaoRepository.findById(5L)).thenReturn(Optional.of(avaliacao));

        avaliacaoService.alterar(5L, dados);

        assertAll("testes",
                () -> assertEquals(9, avaliacao.getNota()),
                () -> assertEquals("teste", avaliacao.getComentario())
                );
    }
    @Test
    @DisplayName("Teste 'alterar': id não encontrado")
    public void teste5() {
        AlteracaoAvaliacaoDTO dados = new AlteracaoAvaliacaoDTO(-1, "teste");

        when(avaliacaoRepository.findById(5L)).thenReturn(Optional.empty());

       assertEquals("Avaliação não encontrada com id 5.", assertThrows(EntidadeNaoEncontradaException.class, () -> avaliacaoService.alterar(5L, dados)).getMessage());
    }
    @Test
    @DisplayName("Teste 'alterar': nota nula e comentário blank")
    public void teste6() {
        avaliacao.setNota(7);
        avaliacao.setComentario("teste");

        AlteracaoAvaliacaoDTO dados = new AlteracaoAvaliacaoDTO(null, "    ");

        when(avaliacaoRepository.findById(5L)).thenReturn(Optional.of(avaliacao));

        avaliacaoService.alterar(5L, dados);

        assertAll("testes",
                () -> assertEquals(7, avaliacao.getNota()),
                () -> assertEquals("teste", avaliacao.getComentario())
        );
    }
    @Test
    @DisplayName("Teste 'alterar': nota nula e comentário nulo")
    public void teste7() {
        avaliacao.setNota(7);
        avaliacao.setComentario("teste");

        AlteracaoAvaliacaoDTO dados = new AlteracaoAvaliacaoDTO(null, null);

        when(avaliacaoRepository.findById(5L)).thenReturn(Optional.of(avaliacao));

        avaliacaoService.alterar(5L, dados);

        assertAll("testes",
                () -> assertEquals(7, avaliacao.getNota()),
                () -> assertEquals("teste", avaliacao.getComentario())
        );
    }

}