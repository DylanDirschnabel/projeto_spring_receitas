package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoAvaliacaoDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoAvaliacaoDTO;
import dylan.senior.projeto.dtos.listagem.ListagemAvaliacaoDTO;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import dylan.senior.projeto.services.AvaliacaoService;
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

import java.util.List;

@RestController
@RequestMapping("avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ValidadorUsuario validadorUsuario;

    @PostMapping
    @Transactional
    public ResponseEntity<DetalhamentoAvaliacaoDTO> cadastrar(@RequestBody @Valid CadastroAvaliacaoDTO dados, UriComponentsBuilder uriBuilder) {

        var avaliacao = avaliacaoService.criarAvaliacao(dados);
        avaliacaoRepository.save(avaliacao);

        var uri = uriBuilder.path("/avaliacoes/{id}").buildAndExpand(avaliacao.getId()).toUri();

        return ResponseEntity.created(uri).body(new DetalhamentoAvaliacaoDTO(avaliacao));
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<DetalhamentoAvaliacaoDTO> detalhar(@PathVariable Long id) {
        var avaliacao = avaliacaoRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Avaliação não encontrada de id " + id + "."));
        return ResponseEntity.ok(new DetalhamentoAvaliacaoDTO(avaliacao));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<ListagemAvaliacaoDTO>> listar(@PageableDefault(size = 10)Pageable paginacao) {
        var page = avaliacaoRepository.findAll(paginacao).map(ListagemAvaliacaoDTO::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ListagemAvaliacaoDTO> alterar(@PathVariable Long id, @RequestBody AlteracaoAvaliacaoDTO dados) {
        var avaliacao = avaliacaoService.alterar(id, dados);

        return ResponseEntity.ok(new ListagemAvaliacaoDTO(avaliacao));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        var avaliacao = avaliacaoRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Avaliação não encontrada de id " + id + "."));

        validadorUsuario.validarAutenticacao(avaliacao.getUsuario().getId());

        avaliacaoRepository.delete(avaliacao);
        return ResponseEntity.ok("Avaliação deletada com sucesso!");
    }

}
