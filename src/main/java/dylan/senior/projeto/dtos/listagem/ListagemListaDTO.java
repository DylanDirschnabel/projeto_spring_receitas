package dylan.senior.projeto.dtos.listagem;

import dylan.senior.projeto.entities.Lista;

public record ListagemListaDTO(
        long id,
        String nome,
        String descricao,
        long id_usuario) {

    public ListagemListaDTO(Lista lista) {
        this(lista.getId(), lista.getNome(), lista.getDescricao(), lista.getUsuario().getId());
    }

}
