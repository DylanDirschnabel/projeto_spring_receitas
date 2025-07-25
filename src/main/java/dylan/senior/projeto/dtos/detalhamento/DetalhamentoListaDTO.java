package dylan.senior.projeto.dtos.detalhamento;

import dylan.senior.projeto.entities.Lista;
import dylan.senior.projeto.entities.Receita;

import java.util.List;

public record DetalhamentoListaDTO(Long id, String nome, String descricao, Long id_usuario, List<String> receitas) {

    public DetalhamentoListaDTO(Lista lista) {
        this(lista.getId(), lista.getNome(), lista.getDescricao(), lista.getUsuario().getId(), lista.getReceitas().stream().map(Receita::getNome).toList());
    }

}
