package dylan.senior.projeto.dtos.detalhamento;

import dylan.senior.projeto.entities.Avaliacao;

public record DetalhamentoAvaliacaoDTO(Long id, int nota, String comentario, Long id_usuario, Long id_receita, String data) {

    public DetalhamentoAvaliacaoDTO(Avaliacao avaliacao) {
        this(avaliacao.getId(), avaliacao.getNota(), avaliacao.getComentario(), avaliacao.getUsuario().getId(), avaliacao.getReceita().getId(), avaliacao.getDtCriacao().toString());
    }

}
