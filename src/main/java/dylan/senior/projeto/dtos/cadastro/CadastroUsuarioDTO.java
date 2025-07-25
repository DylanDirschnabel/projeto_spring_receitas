package dylan.senior.projeto.dtos.cadastro;

import jakarta.validation.constraints.NotBlank;

public record CadastroUsuarioDTO(
        @NotBlank
        String login,
        @NotBlank
        String senha,
        @NotBlank
        String nome) {
}
