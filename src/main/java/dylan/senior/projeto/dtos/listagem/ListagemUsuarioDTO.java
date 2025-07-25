package dylan.senior.projeto.dtos.listagem;

import dylan.senior.projeto.entities.Usuario;

public record ListagemUsuarioDTO(
        Long id,
        String nome,
        String login
) {
    public ListagemUsuarioDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getLogin());
    }
}
