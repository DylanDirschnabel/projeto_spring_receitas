package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorTagTest {

    @InjectMocks
    private ValidadorTag validadorTag;

    @Mock
    private TagRepository tagRepository;

    // ---- Testes 'validar' ---- //

    @Test
    @DisplayName("Teste 'validar': dados válidos")
    public void teste1() {
        when(tagRepository.existsByNome("teste")).thenReturn(false);

        assertDoesNotThrow(() -> validadorTag.validar("teste"));
    }
    @Test
    @DisplayName("Teste 'validar': tag já existe")
    public void teste2() {
        when(tagRepository.existsByNome("teste")).thenReturn(true);

        assertEquals("Erro: tag com esse nome já existe", assertThrows(ValidacaoException.class, () -> validadorTag.validar("teste")).getMessage());
    }
    @Test
    @DisplayName("Teste 'validar': nome nulo")
    public void teste3() {
        assertEquals("Erro: nome de tag inválido", assertThrows(ValidacaoException.class, () -> validadorTag.validar(null)).getMessage());
    }
    @Test
    @DisplayName("Teste 'validar': nome vazio")
    public void teste4() {
        assertEquals("Erro: nome de tag inválido", assertThrows(ValidacaoException.class, () -> validadorTag.validar("    ")).getMessage());
    }



}