package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorAvaliacaoTest {

    @InjectMocks
    private ValidadorAvaliacao validadorAvaliacao;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    // ---- Testes 'validar' ---- //

    @Test
    @DisplayName("Teste 'validar': dados válidos")
    public void teste1() {
        CadastroAvaliacaoDTO dados = new CadastroAvaliacaoDTO(4, "Ok", 1L, 2L);
        assertDoesNotThrow(() -> validadorAvaliacao.validar(dados));
    }
    @Test
    @DisplayName("Teste 'validar': dados nulos")
    public void teste2() {
        assertEquals("Erro: dados inválidos para cadastro de avaliação", assertThrows(ValidacaoException.class, () -> validadorAvaliacao.validar(null)).getMessage());
    }

    // ---- Testes 'validarSeJaExiste' ---- //

    @Test
    @DisplayName("Validar 'validarSeJaExiste': não existe")
    public void teste3() {

        when(avaliacaoRepository.existsByIdReceitaIdUsuario(4L, 3L)).thenReturn(false);

        assertDoesNotThrow(() -> validadorAvaliacao.validarSeJaExiste(4L, 3L));
    }
    @Test
    @DisplayName("Validar 'validarSeJaExiste': já existe")
    public void teste4() {

        when(avaliacaoRepository.existsByIdReceitaIdUsuario(4L, 3L)).thenReturn(true);

        assertEquals("Erro: usuário já avaliou essa receita", assertThrows(ValidacaoException.class, () -> validadorAvaliacao.validarSeJaExiste(4L, 3L)).getMessage());
    }

    // ---- Testes 'validarNota' ---- //
    @Test
    @DisplayName("Validar 'validarNota': nota válida")
    public void teste5() {
        assertDoesNotThrow(() -> validadorAvaliacao.validarNota(5));
    }
    @Test
    @DisplayName("Validar 'validarNota': nota menor que 0")
    public void teste6() {
        assertEquals("Erro: nota deve estar entre 0 a 10", assertThrows(ValidacaoException.class, () -> validadorAvaliacao.validarNota(-1)).getMessage());
    }
    @Test
    @DisplayName("Validar 'validarNota': nota maior que 10")
    public void teste7() {
        assertEquals("Erro: nota deve estar entre 0 a 10", assertThrows(ValidacaoException.class, () -> validadorAvaliacao.validarNota(11)).getMessage());
    }


}