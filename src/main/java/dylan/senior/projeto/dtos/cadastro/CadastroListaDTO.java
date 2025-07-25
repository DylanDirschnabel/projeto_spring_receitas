package dylan.senior.projeto.dtos.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroListaDTO(
        @NotBlank
        String nome,
        String descricao,
        @NotNull
        Long id_usuario) {
}
