package dylan.senior.projeto.dtos.cadastro;

import dylan.senior.projeto.entities.Tag;
import jakarta.validation.constraints.NotBlank;

public record CadastroTagDTO(
        @NotBlank
        String nome) {

}
