package dylan.senior.projeto.dtos.busca;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BuscaReceitaDTO(
        @NotNull
        List<String> inclusas,
        @NotNull
        List<String> exclusas) {
}
