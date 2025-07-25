package dylan.senior.projeto.dtos.detalhamento;

import dylan.senior.projeto.dtos.listagem.ListagemAvaliacaoDTO;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Tag;

import java.util.List;

public record DetalhamentoReceitaDTO(Long id,
                                     String nome,
                                     List<String> tags,
                                     List<String> ingredientes,
                                     String corpo,
                                     String dtCriacao,
                                     Long id_criador,
                                     String criador,
                                     List<ListagemAvaliacaoDTO> avaliacoes) {


}
