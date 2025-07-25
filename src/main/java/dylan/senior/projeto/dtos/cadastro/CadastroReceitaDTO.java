package dylan.senior.projeto.dtos.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CadastroReceitaDTO(
        @NotNull
        Long id_criador,
        @NotBlank
        String nome,
        @NotBlank
        String corpo,
        @NotNull
        @NotEmpty
        List<String> ingredientes,
        List<String> tags) {

}
