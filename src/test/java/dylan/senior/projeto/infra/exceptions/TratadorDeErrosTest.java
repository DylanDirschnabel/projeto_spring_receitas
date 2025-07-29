package dylan.senior.projeto.infra.exceptions;

import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TratadorDeErrosTest {


    private final TratadorDeErros tratador = new TratadorDeErros();

    @Test
    void deveTratarErroDeValidacao() {
        // Mock do FieldError
        FieldError fieldError = new FieldError("objeto", "campo", "mensagem de erro");

        // Mock da exceção
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<?> response = tratador.tratarErro400(ex);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
        var body = (List<?>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
    }

    @Test
    void deveTratarValidacaoException() {
        ValidacaoException ex = new ValidacaoException("Erro de regra de negócio");

        ResponseEntity<?> response = tratador.tratarErroRegraDeNegocio(ex);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
        var body = (TratadorDeErros.MensagemErro) response.getBody();
        assertNotNull(body);
        assertEquals("Dados inválidos", body.erro());
        assertEquals("Erro de regra de negócio", body.mensagem());
    }

    @Test
    void deveTratarEntidadeNaoEncontradaException() {
        EntidadeNaoEncontradaException ex = new EntidadeNaoEncontradaException("Entidade X não encontrada");

        ResponseEntity<?> response = tratador.tratarErroEntidadeNaoEncontrada(ex);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
        var body = (TratadorDeErros.MensagemErro) response.getBody();
        assertNotNull(body);
        assertEquals("Entidade não encontrada", body.erro());
        assertEquals("Entidade X não encontrada", body.mensagem());
    }

    @Test
    void deveTratarSQLException() {
        SQLException ex = new SQLException("Erro no banco de dados");

        ResponseEntity<?> response = tratador.tratarErroDadosInvalidos(ex);

        assertEquals("400 BAD_REQUEST", response.getStatusCode().toString());
        var body = (TratadorDeErros.MensagemErro) response.getBody();
        assertNotNull(body);
        assertEquals("Dados inválidos", body.erro());
        assertEquals("Erro no banco de dados", body.mensagem());
    }
}

