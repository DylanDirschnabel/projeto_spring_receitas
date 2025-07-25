package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ValidadorUsuario validadorUsuario;

    @Transactional
    public Usuario alterar(AlteracaoUsuarioDTO dados, Long id) {
        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado de id " + id + "."));

        if(dados.nome() != null && !dados.nome().isBlank()) {
            usuario.setNome(dados.nome());
        }
        if(dados.login() != null && !dados.login().isBlank()) {
            validadorUsuario.validarLogin(dados.login());
            usuario.setLogin(dados.login());
        }
        if(dados.senha() != null && !dados.senha().isBlank()) {
            validadorUsuario.validarSenha(dados.senha());
            usuario.setSenha(dados.senha());
        }

        return usuario;
    }

}
