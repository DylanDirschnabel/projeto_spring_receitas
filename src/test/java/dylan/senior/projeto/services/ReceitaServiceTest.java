package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.cadastro.CadastroReceitaDTO;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.repositories.TagRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
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
class ReceitaServiceTest {

    @InjectMocks
    private ReceitaService receitaService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TagRepository tagRepository;

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
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

}