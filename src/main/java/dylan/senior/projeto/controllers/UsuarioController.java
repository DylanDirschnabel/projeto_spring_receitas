package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoUsuarioDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoUsuarioDTO;
import dylan.senior.projeto.dtos.listagem.ListagemUsuarioDTO;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.services.UsuarioService;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ValidadorUsuario validadorUsuario;

    
    @GetMapping
    @Transactional
    public ResponseEntity<Page<ListagemUsuarioDTO>> listar(@PageableDefault(size = 10, sort={"nome"}) Pageable paginacao) {
        var page = usuarioRepository.findAll(paginacao).map(ListagemUsuarioDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<DetalhamentoUsuarioDTO> detalhar(@PathVariable Long id) {

        validadorUsuario.validarAutenticacao(id);

        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado de id " + id + "."));
        return ResponseEntity.ok(new DetalhamentoUsuarioDTO(usuario));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DetalhamentoUsuarioDTO> alterar(@RequestBody AlteracaoUsuarioDTO dados, @PathVariable Long id) {
        var usuario = usuarioService.alterar(dados, id);
        return ResponseEntity.ok(new DetalhamentoUsuarioDTO(usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deletar(@PathVariable Long id) {

        validadorUsuario.validarAutenticacao(id);

        var usuario = usuarioRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Usuario não encontrado de id " + id + "."));
        usuarioRepository.delete(usuario);
        return ResponseEntity.ok("Usuário deletado com sucesso!");
    }
}
