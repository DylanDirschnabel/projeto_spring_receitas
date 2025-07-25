package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAvaliacao {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public void validar(CadastroAvaliacaoDTO dados) {
        validarSeJaExiste(dados.id_receita(), dados.id_usuario());
        validarNota(dados.nota());
    }

    public void validarSeJaExiste(Long idReceita, Long idUsuario) {
        if(avaliacaoRepository.existsByIdReceitaIdUsuario(idReceita, idUsuario)) {
            throw new ValidacaoException("Erro: usuário já avaliou essa receita");
        }
    }

    public void validarNota(int nota) {
        if(nota < 0 || nota > 10) {
            throw new ValidacaoException("Erro: nota deve estar entre 0 a 10");
        }
    }

}
