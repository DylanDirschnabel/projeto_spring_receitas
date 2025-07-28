package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorTag {

    @Autowired
    private TagRepository tagRepository;

    public void validar(String nome) {
        if(nome == null || nome.isBlank()) {
            throw new ValidacaoException("Erro: nome de tag inválido");
        }

        if(tagRepository.existsByNome(nome)) {
            throw new ValidacaoException("Erro: tag com esse nome já existe");
        }
    }

}
