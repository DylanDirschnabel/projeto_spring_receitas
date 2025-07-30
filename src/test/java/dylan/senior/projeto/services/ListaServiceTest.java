package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoListaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroListaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemListaDTO;
import dylan.senior.projeto.entities.Lista;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ListaRepository;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ListaServiceTest {

    @InjectMocks
    private ListaService listaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReceitaRepository receitaRepository;

    @Mock
    private ListaRepository listaRepository;

    @Mock
    private ValidadorUsuario validadorUsuario;

    private Usuario usuario;
    private Receita receita;
    private Lista lista;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        lista = new Lista();
        lista.setUsuario(usuario);
        receita = new Receita();
    }

    // ---- Testes 'criarLista' ---- //

    @Test
    @DisplayName("Teste 'criarLista': dados válidos")
    public void teste1() {
        CadastroListaDTO dados = new CadastroListaDTO("teste", null, 5L);
        usuario.setNome("ana");

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(usuario));

        Lista lista = listaService.criarLista(dados);

        assertAll("testes",
                () -> assertEquals("teste", lista.getNome()),
                () -> assertNull(lista.getDescricao()),
                () -> assertEquals("ana", lista.getUsuario().getNome())
                );
    }
    @Test
    @DisplayName("Teste 'criarLista': usuário não encontrado")
    public void teste2() {
        CadastroListaDTO dados = new CadastroListaDTO("teste", null, 5L);
        usuario.setNome("ana");

        when(usuarioRepository.findById(5L)).thenReturn(Optional.empty());

        assertEquals("Usuário não encontrado de id 5.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.criarLista(dados)).getMessage());
    }

    // ---- Testes 'adicionarReceita' ---- //

    @Test
    @DisplayName("Teste 'adicionarReceita': dados válidos")
    public void teste3() {

        when(receitaRepository.findById(5L)).thenReturn(Optional.of(receita));
        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));

        Lista resultado = listaService.adicionarReceita(5L, 4L);

        assertEquals(resultado, lista);
    }
    @Test
    @DisplayName("Teste 'adicionarReceita': receita não encontrada")
    public void teste4() {
        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));
        when(receitaRepository.findById(5L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 5.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.adicionarReceita(5L, 4L)).getMessage());
    }
    @Test
    @DisplayName("Teste 'adicionarReceita': lista não encontrada")
    public void teste5() {

        when(listaRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Lista não encontrada de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.adicionarReceita(5L, 4L)).getMessage());
    }

    // ---- Testes 'removerReceita' ---- //

    @Test
    @DisplayName("Teste 'removerReceita': dados válidos")
    public void teste6() {

        when(receitaRepository.findById(5L)).thenReturn(Optional.of(receita));
        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));

        Lista resultado = listaService.removerReceita(5L, 4L);

        assertEquals(resultado, lista);
    }
    @Test
    @DisplayName("Teste 'removerReceita': receita não encontrada")
    public void teste7() {

        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));
        when(receitaRepository.findById(5L)).thenReturn(Optional.empty());

        assertEquals("Receita não encontrada de id 5.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.removerReceita(5L, 4L)).getMessage());
    }
    @Test
    @DisplayName("Teste 'removerReceita': lista não encontrada")
    public void teste8() {

        when(listaRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Lista não encontrada de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.removerReceita(5L, 4L)).getMessage());
    }

    // ---- Testes 'alterar' ---- //

    @Test
    @DisplayName("Teste 'alterar': dados válidos")
    public void teste9() {
        AlteracaoListaDTO dados = new AlteracaoListaDTO("novo", "new");

        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));

        Lista resultado = listaService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("novo", resultado.getNome()),
                () -> assertEquals("new", lista.getDescricao()),
                () -> assertEquals(resultado, lista)
                );
    }
    @Test
    @DisplayName("Teste 'alterar': lista não encontrada")
    public void teste10() {
        AlteracaoListaDTO dados = new AlteracaoListaDTO("novo", "new");

        when(listaRepository.findById(4L)).thenReturn(Optional.empty());

        assertEquals("Lista não encontrada de id 4.", assertThrows(EntidadeNaoEncontradaException.class, () -> listaService.alterar(dados, 4L)).getMessage());
    }
    @Test
    @DisplayName("Teste 'alterar': nome e descrição vazios")
    public void teste11() {
        AlteracaoListaDTO dados = new AlteracaoListaDTO("     ", "");
        lista.setNome("novo");
        lista.setDescricao("new");

        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));

        Lista resultado = listaService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("novo", resultado.getNome()),
                () -> assertEquals("new", lista.getDescricao()),
                () -> assertEquals(resultado, lista)
        );
    }
    @Test
    @DisplayName("Teste 'alterar': nome e descrição nulos")
    public void teste12() {
        AlteracaoListaDTO dados = new AlteracaoListaDTO(null, null);
        lista.setNome("novo");
        lista.setDescricao("new");

        when(listaRepository.findById(4L)).thenReturn(Optional.of(lista));

        Lista resultado = listaService.alterar(dados, 4L);

        assertAll("testes",
                () -> assertEquals("novo", resultado.getNome()),
                () -> assertEquals("new", lista.getDescricao()),
                () -> assertEquals(resultado, lista)
        );
    }

    // ---- Testes 'listarPorUsuario' ---- //

    @Test
    @DisplayName("Teste 'listarPorUsuario'")
    public void teste13() {
        List<ListagemListaDTO> esperado = new ArrayList<>();

        when(listaService.listarPorUsuario(4L)).thenReturn(esperado);

        List<ListagemListaDTO> resultado = listaService.listarPorUsuario(4L);

        assertEquals(esperado, resultado);
    }

}