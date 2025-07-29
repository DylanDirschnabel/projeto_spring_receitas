package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoListaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroListaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemListaDTO;
import dylan.senior.projeto.entities.Lista;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.ListaRepository;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Transactional
    public Lista criarLista(CadastroListaDTO dados) {
        Usuario usuario = usuarioRepository.findById(dados.id_usuario()).orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado de id " + dados.id_usuario() + "."));
        return new Lista(dados.descricao(), dados.nome(), usuario);
    }

    @Transactional
    public Lista adicionarReceita(Long id_receita, Long id_lista) {
        Receita receita = receitaRepository.findById(id_receita).orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id_receita + "."));
        Lista lista = listaRepository.findById(id_lista).orElseThrow(() -> new EntidadeNaoEncontradaException("Lista não encontrada de id " + id_lista + "."));

        lista.getReceitas().add(receita);
        return lista;
    }

    @Transactional
    public Lista removerReceita(Long id_receita, Long id_lista) {
        Receita receita = receitaRepository.findById(id_receita).orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id_receita + "."));
        Lista lista = listaRepository.findById(id_lista).orElseThrow(() -> new EntidadeNaoEncontradaException("Lista não encontrada de id " + id_lista + "."));

        lista.getReceitas().remove(receita);
        return lista;
    }

    @Transactional
    public Lista alterar(AlteracaoListaDTO dados, Long id) {
        Lista lista = listaRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Lista não encontrada de id " + id + "."));
        if(dados.descricao() != null && !dados.descricao().isBlank()) {
            lista.setDescricao(dados.descricao());
        }
        if(dados.nome() != null && !dados.nome().isBlank()) {
            lista.setNome(dados.nome());
        }
        return lista;
    }

    @Transactional
    public List<ListagemListaDTO> listarPorUsuario(Long id) {
        return listaRepository.listarPorUsuario(id).stream().map(ListagemListaDTO::new).toList();
    }


}
