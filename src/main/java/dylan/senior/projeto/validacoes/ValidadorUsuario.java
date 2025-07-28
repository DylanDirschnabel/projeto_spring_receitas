package dylan.senior.projeto.validacoes;

import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidadorUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void validar(CadastroUsuarioDTO dados) {
        if(dados == null) {
            throw new ValidacaoException("Dados inválidos para cadastro de usuário");
        }
        validarLogin(dados.login());
        validarSenha(dados.senha());
    }

    public void validarLogin(String login) {

        if(usuarioRepository.existsByLogin(login)) {
            throw new ValidacaoException("Erro: login já cadastrado");
        }

        if(login.length() < 8 || login.length() > 50) {
            throw new ValidacaoException("Erro: login deve ter entre 8 a 50 caracteres");
        }
    }

    public void validarSenha(String senha) {

        if(senha.length() < 8 || senha.length() > 50) {
            throw new ValidacaoException("Erro: senha deve ter entre 8 a 50 caracteres");
        }

        if(!senha.matches(".*\\d.*")) {
            throw new ValidacaoException("Erro: senha deve conter pelo menos um número");
        }

        if(!senha.matches(".*[A-Z].*")) {
            throw new ValidacaoException("Erro: senha deve conter pelo menos uma letra em maiúsculo");
        }

        if(!senha.matches(".*[!, @, #, $, %, &, ?].*")) {
            throw new ValidacaoException("Erro: senha deve conter pelo menos algum desses caracteres: [!, @, #, $, %, &, ?]");
        }

    }

}
