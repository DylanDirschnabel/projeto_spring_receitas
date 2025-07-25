package dylan.senior.projeto.dtos.cadastro;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CadastroGeradoReceitaDTO(Long id_usuario, String nome, List<String> tags, String comentario) {
}
