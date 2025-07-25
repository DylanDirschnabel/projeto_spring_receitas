package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.alteracao.AlteracaoListaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroListaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoListaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemListaDTO;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ListaRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.services.ListaService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("listas")
public class ListaController {

    @Autowired
    private ListaService listaService;

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<ListagemListaDTO> cadastrar(@RequestBody @Valid @NotNull CadastroListaDTO dados, UriComponentsBuilder uriBuilder) {
        var lista = listaService.criarLista(dados);
        listaRepository.save(lista);

        var uri = uriBuilder.path("/listas/{id}").buildAndExpand(lista.getId()).toUri();

        return ResponseEntity.created(uri).body(new ListagemListaDTO(lista));
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Page<ListagemListaDTO>> listar(@PageableDefault(size=10) Pageable paginacao) {
        var page = listaRepository.findAll(paginacao).map(ListagemListaDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/lista/{id}")
    @Transactional
    public ResponseEntity<DetalhamentoListaDTO> detalhar(@PathVariable @NotNull Long id) {
        var lista = listaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Lista não encontrada de id " + id + "."));
        return ResponseEntity.ok(new DetalhamentoListaDTO(lista));
    }

    @PostMapping("/adicionar/{id_lista}")
    @Transactional
    public ResponseEntity<DetalhamentoListaDTO> adicionarReceita(@PathVariable @NotNull Long id_lista, @RequestBody @NotNull Long id_receita) {
        var lista = listaService.adicionarReceita(id_receita, id_lista);
        return ResponseEntity.ok(new DetalhamentoListaDTO(lista));
    }

    @DeleteMapping("/remover/{id_lista}")
    @Transactional
    public ResponseEntity<DetalhamentoListaDTO> removerReceita(@PathVariable @NotNull Long id_lista, @RequestBody @NotNull Long id_receita) {
        var lista = listaService.removerReceita(id_receita, id_lista);
        return ResponseEntity.ok(new DetalhamentoListaDTO(lista));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ListagemListaDTO> alterar(@PathVariable @NotNull Long id, @RequestBody @NotNull AlteracaoListaDTO dados) {
        var lista = listaService.alterar(dados, id);
        return ResponseEntity.ok(new ListagemListaDTO(lista));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        var lista = listaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Lista não encontrada de id " + id + "."));
        listaRepository.delete(lista);
        return ResponseEntity.ok("Lista deletada com sucesso!");
    }

    @GetMapping("/usuario/{id}")
    @Transactional
    public ResponseEntity<List<ListagemListaDTO>> listarPorUsuario(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(listaService.listarPorUsuario(id));
    }
}
