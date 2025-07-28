package dylan.senior.projeto.repositories;

import dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO;
import dylan.senior.projeto.entities.Receita;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReceitaRepositoryTest {

    @Mock
    private ReceitaRepository receitaRepository;

    private Receita receita;

    private List<ListagemSemTagsDTO> lista;

    @BeforeEach
    public void setUp() {
        receita = new Receita();
        lista = new ArrayList<>();
    }

    @Test
    @DisplayName("buscaPorNome teste - should retrieve 2 items")
    public void teste1() {
        ListagemSemTagsDTO r1 = new ListagemSemTagsDTO(1l, "teste", 5d, LocalDateTime.now(), "criador");
        ListagemSemTagsDTO r2 = new ListagemSemTagsDTO(2l, "testee", 5d, LocalDateTime.now(), "criador");
        lista.add(r1);
        lista.add(r2);

        when(receitaRepository.buscaPorNome("teste")).thenReturn(lista);

        assertEquals(2, receitaRepository.buscaPorNome("teste").size());
    }

    @Test
    @DisplayName("buscaExclusivaTags teste - should retrieve 2 items")
    public void teste2() {

    }

}