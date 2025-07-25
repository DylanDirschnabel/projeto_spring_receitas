package dylan.senior.projeto.dtos.detalhamento;

import dylan.senior.projeto.entities.Usuario;

public record DetalhamentoUsuarioDTO(
        Long id,
        String login,
        String nome,
        String senha
                                     ) {

    public DetalhamentoUsuarioDTO(Usuario user) {
        this(user.getId(), user.getLogin(), user.getNome(), user.getSenha());
    }
}
