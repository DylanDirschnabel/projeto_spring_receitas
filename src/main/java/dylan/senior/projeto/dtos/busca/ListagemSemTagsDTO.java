package dylan.senior.projeto.dtos.busca;

import dylan.senior.projeto.entities.Receita;

import java.time.LocalDateTime;
import java.util.List;

public record ListagemSemTagsDTO(Long id, String nome, Double media, LocalDateTime dtCriacao, String criador) {
}
