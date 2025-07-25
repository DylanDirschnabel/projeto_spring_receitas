package dylan.senior.projeto.dtos.listagem;

import dylan.senior.projeto.entities.Avaliacao;

public record ListagemAvaliacaoDTO(Long id,
                                   int nota,
                                   String usuario,
                                   Long id_usuario,
                                   String comentario) {

    public ListagemAvaliacaoDTO(Avaliacao avaliacao) {
        this(   avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getUsuario().getNome(),
                avaliacao.getUsuario().getId(),
                avaliacao.getComentario());
    }

}
