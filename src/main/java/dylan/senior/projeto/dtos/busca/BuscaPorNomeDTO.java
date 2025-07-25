package dylan.senior.projeto.dtos.busca;

import jakarta.validation.constraints.NotNull;

public record BuscaPorNomeDTO(@NotNull String nome) {
}
