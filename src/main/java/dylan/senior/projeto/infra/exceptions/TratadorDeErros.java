package dylan.senior.projeto.infra.exceptions;

import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErro400(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<MensagemErro> tratarErroRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest().body(new MensagemErro("Dados inválidos", ex.getMessage()));
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<MensagemErro> tratarErroEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {
        return ResponseEntity.badRequest().body(new MensagemErro("Entidade não encontrada", ex.getMessage()));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<MensagemErro> tratarErroDadosInvalidos(SQLException ex) {
        return ResponseEntity.badRequest().body(new MensagemErro("Dados inválidos", ex.getMessage()));
    }


    public record MensagemErro(String erro, String mensagem) {}

    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }


}
