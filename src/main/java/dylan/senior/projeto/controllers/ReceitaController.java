package dylan.senior.projeto.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dylan.senior.projeto.dtos.busca.BuscaPorNomeDTO;
import dylan.senior.projeto.dtos.busca.BuscaReceitaDTO;
import dylan.senior.projeto.dtos.alteracao.AlteracaoReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroGeradoReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroReceitaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemBuscaReceitaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemReceitaDTO;

import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.services.ReceitaService;
import jakarta.persistence.EntityNotFoundException;
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
@RequestMapping("receitas")
public class ReceitaController {

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private ReceitaService receitaService;

    @PostMapping
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> cadastrar(@RequestBody @Valid CadastroReceitaDTO dados, UriComponentsBuilder uriBuilder) {
        var receita = receitaService.criarReceita(dados);
        receitaRepository.save(receita);

        var uri = uriBuilder.path("/receitas/{id}").buildAndExpand(receita.getId()).toUri();

        return ResponseEntity.created(uri).body(new ListagemReceitaDTO(receita));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<ListagemReceitaDTO>> listar(@PageableDefault(size = 10) Pageable paginacao) {
        var page = receitaRepository.findAll(paginacao).map(ListagemReceitaDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<DetalhamentoReceitaDTO> detalhar(@PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Receita não encontrada"));
        return ResponseEntity.ok(receitaService.detalhar(receita));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> alterar(@PathVariable Long id, @RequestBody AlteracaoReceitaDTO dados) {
        var receita = receitaService.alterar(id, dados);
        return ResponseEntity.ok(new ListagemReceitaDTO(receita));
    }

    @PostMapping("/tag/{id}")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> adicionarTag(@RequestBody String tag, @PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id + "."));
        receitaService.adicionarTag(receita, tag);

        return ResponseEntity.ok(new ListagemReceitaDTO(receita));
    }

    @DeleteMapping("/tag/{id}")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> removerTag(@RequestBody String tag, @PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id + "."));
        receitaService.removerTag(receita, tag);

        return ResponseEntity.ok(new ListagemReceitaDTO(receita));
    }

    @PostMapping("/ingrediente/{id}")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> adicionarIngrediente(@RequestBody String ingrediente, @PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow();
        receitaService.adicionarIngrediente(receita, ingrediente);

        return ResponseEntity.ok(new ListagemReceitaDTO(receita));
    }

    @DeleteMapping("/ingrediente/{id}")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> removerIngrediente(@RequestBody String ingrediente, @PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow();
        receitaService.removerIngrediente(receita, ingrediente);

        return ResponseEntity.ok(new ListagemReceitaDTO(receita));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        var receita = receitaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id + "."));
        receitaRepository.delete(receita);
        return ResponseEntity.ok("Receita deletada com sucesso!");
    }

    // -------- Consultas Avançadas

    @GetMapping("/busca")
    @Transactional
    public ResponseEntity<List<ListagemBuscaReceitaDTO>> buscaPorNome(@RequestBody @Valid BuscaPorNomeDTO dados) {
        return ResponseEntity.ok(receitaService.buscaPorNome(dados.nome()));
    }

    @GetMapping("/busca/exclusiva")
    @Transactional
    public ResponseEntity<List<ListagemBuscaReceitaDTO>> buscaExclusiva(@RequestBody @Valid BuscaReceitaDTO dados) {
        return ResponseEntity.ok(receitaService.buscaExclusiva(dados));
    }

    @GetMapping("/busca/inclusiva")
    @Transactional
    public ResponseEntity<List<ListagemBuscaReceitaDTO>> buscaInclusiva(@RequestBody @Valid BuscaReceitaDTO dados) {
        return ResponseEntity.ok(receitaService.buscaInclusiva(dados));
    }

    // -------- Gerar Receita com IA

    @PostMapping("/ia")
    @Transactional
    public ResponseEntity<ListagemReceitaDTO> gerar(@RequestBody@Valid CadastroGeradoReceitaDTO dados, UriComponentsBuilder uriBuilder) throws JsonProcessingException {
        CadastroReceitaDTO dadosCadastroReceita = receitaService.gerar(dados);
        var receita = receitaService.criarReceita(dadosCadastroReceita);
        receitaRepository.save(receita);

        var uri = uriBuilder.path("/receitas/{id}").buildAndExpand(receita.getId()).toUri();

        return ResponseEntity.created(uri).body(new ListagemReceitaDTO(receita));
    }


}
