package dylan.senior.projeto.dtos.busca;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BuscaReceitaDTO(
        String nome,
        @NotNull
        List<String> inclusas,
        @NotNull
        List<String> exclusas) {
}
