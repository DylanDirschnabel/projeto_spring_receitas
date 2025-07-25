package dylan.senior.projeto.dtos.listagem;

import dylan.senior.projeto.entities.Tag;

public record ListagemTagDTO(Long id, String nome) {

    public ListagemTagDTO(Tag tag) {
        this(tag.getId(), tag.getNome());
    }

}
