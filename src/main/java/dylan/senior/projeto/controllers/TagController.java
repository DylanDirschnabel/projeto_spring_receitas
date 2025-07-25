package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.cadastro.CadastroTagDTO;
import dylan.senior.projeto.dtos.listagem.ListagemTagDTO;
import dylan.senior.projeto.entities.Tag;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.TagRepository;
import dylan.senior.projeto.validacoes.ValidadorTag;
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
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ValidadorTag validadorTag;

    @PostMapping
    @Transactional
    public ResponseEntity<ListagemTagDTO> cadastrar(@RequestBody @Valid CadastroTagDTO dados, UriComponentsBuilder uriBuilder) {

        validadorTag.validar(dados.nome());

        var tag = new Tag(dados);
        tagRepository.save(tag);

        var uri = uriBuilder.path("/tags/{id}").buildAndExpand(tag.getId()).toUri();

        return ResponseEntity.created(uri).body(new ListagemTagDTO(tag));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<ListagemTagDTO>> listar(@PageableDefault(size=10)Pageable paginacao) {
        var page = tagRepository.findAll(paginacao).map(ListagemTagDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ListagemTagDTO> detalhar(@PathVariable Long id) {
        var tag = tagRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Tag não encontrada de nome" + id + "."));
        return ResponseEntity.ok(new ListagemTagDTO(tag));
    }

    @GetMapping("/buscaPorNome")
    @Transactional
    public ResponseEntity<ListagemTagDTO> detalharPorNome(@RequestBody @Valid CadastroTagDTO dados) {
        var tag = tagRepository.findByNome(dados.nome()).orElseThrow(() -> new EntidadeNaoEncontradaException("Tag não encontrada com nome " + dados.nome() + "."));
        return ResponseEntity.ok(new ListagemTagDTO(tag));
    }

}
