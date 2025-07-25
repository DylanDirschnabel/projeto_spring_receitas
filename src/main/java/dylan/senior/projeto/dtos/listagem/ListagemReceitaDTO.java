package dylan.senior.projeto.dtos.listagem;

import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Tag;

import java.util.List;

public record ListagemReceitaDTO(Long id, Long id_criador, String nome, String corpo, List<String> ingredientes, List<String> tags) {

    public ListagemReceitaDTO(Receita receita) {
        this(receita.getId(), receita.getCriador().getId(), receita.getNome(), receita.getCorpo(), receita.getIngredientes(), receita.getTags().stream().map(Tag::getNome).toList());
    }
}
