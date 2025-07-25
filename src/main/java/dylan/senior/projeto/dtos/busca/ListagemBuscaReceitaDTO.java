package dylan.senior.projeto.dtos.busca;

import java.time.LocalDateTime;
import java.util.List;

public record ListagemBuscaReceitaDTO(Long id, String nome, List<String> tags, Double media, LocalDateTime dtCriacao, String criador) {

    public ListagemBuscaReceitaDTO(ListagemSemTagsDTO dados, List<String> tags) {
        this(dados.id(), dados.nome(), tags, dados.media(), dados.dtCriacao(), dados.criador());
    }

}
