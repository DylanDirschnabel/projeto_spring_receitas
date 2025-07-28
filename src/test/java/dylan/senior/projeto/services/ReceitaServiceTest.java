package dylan.senior.projeto.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import dylan.senior.projeto.dtos.alteracao.AlteracaoReceitaDTO;
import dylan.senior.projeto.dtos.busca.BuscaReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemBuscaReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroGeradoReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroReceitaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoReceitaDTO;
import dylan.senior.projeto.entities.Avaliacao;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Tag;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.TagRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceitaServiceTest {

    @InjectMocks
    private ReceitaService receitaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OpenAiChatModel chatModel;

    private Avaliacao avaliacao;

    private Usuario usuario;

    private Receita receita;

    private Tag tag;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        receita = new Receita(new ArrayList<String>(), "corpo", "nome", usuario, LocalDateTime.now());
        tag = new Tag();
        avaliacao = new Avaliacao(5L, 5, LocalDateTime.now(), "Ok", usuario, receita);
    }

    // ---- Testes 'criarReceita' ---- //

    @Test
    @DisplayName("Teste 'criarReceita': dados válidos")
    public void teste1() {
        ArrayList<String> ingredientes = new ArrayList<>();
        ingredientes.add("ingrediente");
        ArrayList<String> tags = new ArrayList<>();
        tags.add("tag");

        usuario.setNome("ana");

        CadastroReceitaDTO dados = new CadastroReceitaDTO(4L, "teste", "abc", ingredientes, tags);

        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(usuario));

        Receita resultado = receitaService.criarReceita(dados);

        assertAll("testes",
                () -> assertEquals("ana", resultado.getCriador().getNome()),
                () -> assertEquals("abc", resultado.getCorpo()),
                () -> assertEquals("teste", resultado.getNome()),
                () -> assertEquals(1, resultado.getTags().size()),
                () -> assertEquals(1, resultado.getIngredientes().size())
        );
    }
    @Test
    @DisplayName("Teste 'criarReceita': usuário não encontrado")
    public void teste2() {
        ArrayList<String> ingredientes = new ArrayList<>();
        ingredientes.add("ingrediente");
        ArrayList<String> tags = new ArrayList<>();
        tags.add("tag");

        CadastroReceitaDTO dados = new CadastroReceitaDTO(4L, "teste", "abc", ingredientes, tags);

        when(usuarioRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> receitaService.criarReceita(dados)).getMessage());
    }

    // ---- Testes 'adicionarTag' ---- //

    @Test
    @DisplayName("Teste 'adicionarTag': tag existente")
    public void teste3() {

        when(tagRepository.existsByNome("Doce")).thenReturn(true);
        when(tagRepository.findByNome("Doce")).thenReturn(Optional.of(tag));

        receitaService.adicionarTag(receita, "Doce");

        verify(tagRepository).findByNome("Doce");
        verify(tagRepository).existsByNome("Doce");
        assertEquals(1, receita.getTags().size());

    }
    @Test
    @DisplayName("Teste 'adicionarTag': tag nova")
    public void teste4() {

        when(tagRepository.existsByNome("Doce")).thenReturn(false);

        receitaService.adicionarTag(receita, "Doce");

        verify(tagRepository).existsByNome("Doce");
        verify(tagRepository).save(tag);
        assertEquals(1, receita.getTags().size());

    }
    @Test
    @DisplayName("Teste 'adicionarTag': receita nula")
    public void teste5() {

        assertEquals("Dados inválidos para adição de tag na receita", assertThrows(ValidacaoException.class, () -> receitaService.adicionarTag(null, "Doce")).getMessage());

    }
    @Test
    @DisplayName("Teste 'adicionarTag': tag nula")
    public void teste6() {

        assertEquals("Dados inválidos para adição de tag na receita", assertThrows(ValidacaoException.class, () -> receitaService.adicionarTag(receita, null)).getMessage());

    }
    @Test
    @DisplayName("Teste 'adicionarTag': tag vazia")
    public void teste7() {

        assertEquals("Dados inválidos para adição de tag na receita", assertThrows(ValidacaoException.class, () -> receitaService.adicionarTag(receita, "    ")).getMessage());

    }

    // ---- Testes 'removerTag' ---- //

    @Test
    @DisplayName("Teste 'removerTag': dados válidos")
    public void teste8() {
        receita.getTags().add(tag);

        when(tagRepository.findByNome("Doce")).thenReturn(Optional.of(tag));

        receitaService.removerTag(receita, "Doce");

        verify(tagRepository).findByNome("Doce");
        assertEquals(0, receita.getTags().size());
    }
    @Test
    @DisplayName("Teste 'removerTag': tag não encontrada")
    public void teste9() {

        when(tagRepository.findByNome("Doce")).thenReturn(Optional.empty());

        assertEquals("Tag não encontrada com nome 'Doce'.", assertThrows(EntidadeNaoEncontradaException.class, () -> receitaService.removerTag(receita, "Doce")).getMessage());
    }
    @Test
    @DisplayName("Teste 'removerTag': receita nula")
    public void teste10() {

        assertEquals("Dados inválidos para remoção de tag da receita", assertThrows(ValidacaoException.class, () -> receitaService.removerTag(null, "Doce")).getMessage());

    }
    @Test
    @DisplayName("Teste 'removerTag': tag nula")
    public void teste11() {

        assertEquals("Dados inválidos para remoção de tag da receita", assertThrows(ValidacaoException.class, () -> receitaService.removerTag(receita, null)).getMessage());

    }

    // ---- Testes 'adicionarIngrediente' ---- //

    @Test
    @DisplayName("Teste 'adicionarIngrediente': dados válidos")
    public void teste12() {

        receitaService.adicionarIngrediente(receita, "2 Ovos");

        assertEquals(1, receita.getIngredientes().size());
    }
    @Test
    @DisplayName("Teste 'adicionarIngrediente': receita nula")
    public void teste13() {

        assertEquals("Dados inválidos para adição de ingrediente da receita", assertThrows(ValidacaoException.class, () -> receitaService.adicionarIngrediente(null, "ingrediente")).getMessage());

    }
    @Test
    @DisplayName("Teste 'adicionarIngrediente': ingrediente nulo")
    public void teste14() {

        assertEquals("Dados inválidos para adição de ingrediente da receita", assertThrows(ValidacaoException.class, () -> receitaService.adicionarIngrediente(receita, null)).getMessage());

    }

    // ---- Testes 'removerIngrediente' ---- //

    @Test
    @DisplayName("Teste 'removerIngrediente': dados válidos")
    public void teste15() {
        receita.getIngredientes().add("ingrediente");

        receitaService.removerIngrediente(receita, "ingrediente");

        assertEquals(0, receita.getIngredientes().size());
    }
    @Test
    @DisplayName("Teste 'removerIngrediente': receita não contém ingrediente")
    public void teste16() {

        assertEquals("Receita não contém o ingrediente 'ingrediente'.", assertThrows(ValidacaoException.class, () -> receitaService.removerIngrediente(receita, "ingrediente")).getMessage());

    }
    @Test
    @DisplayName("Teste 'removerIngrediente': receita nula")
    public void teste17() {

        assertEquals("Dados inválidos para remoção de ingrediente da receita", assertThrows(ValidacaoException.class, () -> receitaService.removerIngrediente(null, "ingrediente")).getMessage());

    }
    @Test
    @DisplayName("Teste 'removerIngrediente': ingrediente nulo")
    public void teste18() {

        assertEquals("Dados inválidos para remoção de ingrediente da receita", assertThrows(ValidacaoException.class, () -> receitaService.removerIngrediente(receita, null)).getMessage());

    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': receita válida")
    void teste19() {


        usuario.setNome("Maria");
        receita.getAvaliacoes().add(avaliacao);
        String dt = receita.getDtCriacao().toString();

        DetalhamentoReceitaDTO dados = receitaService.detalhar(receita);
        assertAll("Testes",
                () -> assertEquals("nome", dados.nome()),
                () -> assertEquals("corpo", dados.corpo()),
                () -> assertEquals(dt, dados.dtCriacao()),
                () -> assertEquals("Maria", dados.criador()),
                () -> assertEquals(1, dados.avaliacoes().size()),
                () -> assertEquals(0, dados.tags().size()),
                () -> assertEquals(0, dados.ingredientes().size())
                );
    }
    @Test
    @DisplayName("Teste 'detalhar': receita nula")
    void teste20() {

        assertNull(receitaService.detalhar(null));

    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': dados válidos")
    void teste21() {
        AlteracaoReceitaDTO dados = new AlteracaoReceitaDTO("novo nome", "novo corpo");

        when(receitaRepository.findById(3L)).thenReturn(Optional.of(receita));

        receitaService.alterar(3L, dados);

        assertAll("Testes",
                () -> assertEquals("novo nome", receita.getNome()),
                () -> assertEquals("novo corpo", receita.getCorpo())
                );
    }
    @Test
    @DisplayName("Teste 'alterar': receita não encontrada")
    void teste22() {

        when(receitaRepository.findById(3L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 3.", assertThrows(EntidadeNaoEncontradaException.class, () -> receitaService.alterar(3L, null)).getMessage());

    }
    @Test
    @DisplayName("Teste 'alterar': dados nulos")
    void teste23() {

        when(receitaRepository.findById(3L)).thenReturn(Optional.of(receita));

        assertEquals(receita, receitaService.alterar(3L, null));

    }
    @Test
    @DisplayName("Teste 'alterar': campos nulos")
    void teste24() {
        AlteracaoReceitaDTO dados = new AlteracaoReceitaDTO(null, null);

        when(receitaRepository.findById(3L)).thenReturn(Optional.of(receita));

        receitaService.alterar(3L, dados);

        assertAll("Testes",
                () -> assertEquals("nome", receita.getNome()),
                () -> assertEquals("corpo", receita.getCorpo())
        );
    }
    @Test
    @DisplayName("Teste 'alterar': campos vazios")
    void teste25() {
        AlteracaoReceitaDTO dados = new AlteracaoReceitaDTO("    ", "         ");

        when(receitaRepository.findById(3L)).thenReturn(Optional.of(receita));

        receitaService.alterar(3L, dados);

        assertAll("Testes",
                () -> assertEquals("nome", receita.getNome()),
                () -> assertEquals("corpo", receita.getCorpo())
        );
    }

    // ---- Testes 'buscaInclusivaTags' ---- //


    @Test
    @DisplayName("Teste 'buscaInclusiva': dados válidos")
    void teste26() {
        // Arrange
        List<String> inclusas = List.of("doce", "rápido");
        List<String> exclusas = List.of("glúten");

        List<String> tags = List.of("doce", "rápido");

        BuscaReceitaDTO dados = new BuscaReceitaDTO(inclusas, exclusas);

        when(receitaRepository.buscaInclusivaTags(
                inclusas.stream().map(String::toLowerCase).toList(),
                exclusas.stream().map(String::toLowerCase).toList()))
                .thenReturn(List.of(new ListagemSemTagsDTO(1L, "Bolo de chocolate", 5d, LocalDateTime.now(), "Maria")));

        when(receitaRepository.findTagsById(1L)).thenReturn(tags);

        // Act
        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaInclusiva(dados);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Bolo de chocolate", resultado.get(0).nome());
        assertEquals(tags, resultado.get(0).tags());
    }
    @Test
    @DisplayName("Teste 'buscaInclusiva': dados nulos")
    public void teste27() {

        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaInclusiva(null);
        assertAll("Testes",
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
                );

    }

    // ---- Testes 'buscaExclusivaTags' ---- //

    @Test
    @DisplayName("Teste 'buscaExclusiva': dados válidos")
    void teste28() {
        // Arrange
        List<String> inclusas = List.of("doce", "rápido");
        List<String> exclusas = List.of("glúten");

        List<String> tags = List.of("doce", "rápido");

        BuscaReceitaDTO dados = new BuscaReceitaDTO(inclusas, exclusas);

        when(receitaRepository.buscaExclusivaTags(
                inclusas.stream().map(String::toLowerCase).toList(),
                exclusas.stream().map(String::toLowerCase).toList(),
                inclusas.size()))
                .thenReturn(List.of(new ListagemSemTagsDTO(1L, "Bolo de chocolate", 5d, LocalDateTime.now(), "Maria")));

        when(receitaRepository.findTagsById(1L)).thenReturn(tags);

        // Act
        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaExclusiva(dados);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Bolo de chocolate", resultado.get(0).nome());
        assertEquals(tags, resultado.get(0).tags());
    }
    @Test
    @DisplayName("Teste 'buscaExclusiva': dados nulos")
    public void teste29() {

        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaExclusiva(null);
        assertAll("Testes",
                () -> assertNotNull(resultado),
                () -> assertTrue(resultado.isEmpty())
        );

    }

    // ---- Testes 'buscarPorNome' ---- //

    @Test
    @DisplayName("Teste 'buscarPorNome': dados válidos")
    void teste30() {
        // Arrange
        List<Receita> receitas = List.of(receita);
        List<String> tags = List.of("doce", "chocolate");

        when(receitaRepository.buscaPorNome("Bolo")).thenReturn(List.of(new ListagemSemTagsDTO(1L, "Bolo de Teste", 5d, LocalDateTime.now(), "Maria")));
        when(receitaRepository.findTagsById(1L)).thenReturn(tags);

        // Act
        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaPorNome("Bolo");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Bolo de Teste", resultado.get(0).nome());
        assertEquals(tags, resultado.get(0).tags());
    }
    @Test
    @DisplayName("Teste 'buscarPorNome': nome nulo")
    void teste31() {
        // Arrange
        List<Receita> receitas = List.of(receita);
        List<String> tags = List.of("doce", "chocolate");

        when(receitaRepository.buscaPorNome("")).thenReturn(List.of(new ListagemSemTagsDTO(1L, "Bolo de Teste", 5d, LocalDateTime.now(), "Maria")));
        when(receitaRepository.findTagsById(1L)).thenReturn(tags);

        // Act
        List<ListagemBuscaReceitaDTO> resultado = receitaService.buscaPorNome(null);

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("Bolo de Teste", resultado.get(0).nome());
        assertEquals(tags, resultado.get(0).tags());
    }

    // ---- Testes 'gerar' ---- //

    @Test
    @DisplayName("Teste 'gerar': dados válidos")
    public void teste32() throws JsonProcessingException {

        // Arrange
        CadastroGeradoReceitaDTO entrada = new CadastroGeradoReceitaDTO(
                1L,
                "Bolo de Cenoura sem glúten",
                List.of("bolo", "cenoura", "saudável"),
                "sem glúten!"
        );

        String jsonSimulado = """
        {
            "id_criador": 1,
            "nome": "Bolo de Cenoura",
            "corpo": "Misture os ingredientes e asse por 40 minutos.",
            "ingredientes": ["cenoura", "farinha de arroz", "ovos", "açúcar"],
            "tags": ["bolo", "cenoura", "saudável", "IA"]
        }
        """;

        when(chatModel.call(any(Prompt.class)).getResult().getOutput().getText()).thenReturn(jsonSimulado);


        // Act
        CadastroReceitaDTO resultado = receitaService.gerar(entrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id_criador());
        assertEquals("Bolo de Cenoura", resultado.nome());
        assertTrue(resultado.tags().contains("IA"));

    }
    @Test
    @DisplayName("Teste 'gerar': sem tags")
    public void teste33() throws JsonProcessingException {

        // Arrange
        CadastroGeradoReceitaDTO entrada = new CadastroGeradoReceitaDTO(
                1L,
                "Bolo de Cenoura sem glúten",
                null,
                "sem glúten!"
        );

        String jsonSimulado = """
        {
            "id_criador": 1,
            "nome": "Bolo de Cenoura",
            "corpo": "Misture os ingredientes e asse por 40 minutos.",
            "ingredientes": ["cenoura", "farinha de arroz", "ovos", "açúcar"],
            "tags": ["bolo", "cenoura", "saudável", "IA"]
        }
        """;

        when(chatModel.call(any(Prompt.class)).getResult().getOutput().getText()).thenReturn(jsonSimulado);


        // Act
        CadastroReceitaDTO resultado = receitaService.gerar(entrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.id_criador());
        assertEquals("Bolo de Cenoura", resultado.nome());
        assertTrue(resultado.tags().contains("IA"));

    }










}