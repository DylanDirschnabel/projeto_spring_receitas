package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.autenticacao.LoginDTO;
import dylan.senior.projeto.dtos.autenticacao.LoginRespostaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.security.TokenService;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private ValidadorUsuario validadorUsuario;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO body) {

        Usuario user = this.usuarioRepository.findByLogin(body.login()).orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        if(passwordEncoder.matches(body.senha(), user.getSenha())) {
            String token = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new LoginRespostaDTO(user.getNome(), token, user.getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody CadastroUsuarioDTO body) {
        validadorUsuario.validar(body);

        Optional<Usuario> user = this.usuarioRepository.findByLogin(body.login());

        if(user.isEmpty()) {
            Usuario newUser = new Usuario();
            newUser.setSenha(passwordEncoder.encode(body.senha()));
            newUser.setLogin(body.login());
            newUser.setNome(body.nome());

            this.usuarioRepository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new LoginRespostaDTO(newUser.getNome(), token, newUser.getId()));

        }
        return ResponseEntity.badRequest().build();
    }

}
