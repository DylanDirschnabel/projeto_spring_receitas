package dylan.senior.projeto.dtos.cadastro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastroAvaliacaoDTO(
        @NotNull
        int nota,
        String comentario,
        @NotNull
        Long id_usuario,
        @NotNull
        Long id_receita) {

}
