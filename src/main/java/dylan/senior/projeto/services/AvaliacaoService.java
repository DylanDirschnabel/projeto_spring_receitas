package dylan.senior.projeto.services;

import dylan.senior.projeto.dtos.alteracao.AlteracaoAvaliacaoDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroAvaliacaoDTO;
import dylan.senior.projeto.entities.Avaliacao;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.AvaliacaoRepository;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorAvaliacao;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AvaliacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private ValidadorAvaliacao validadorAvaliacao;

    @Autowired
    private ValidadorUsuario validadorUsuario;

    @Transactional
    public Avaliacao criarAvaliacao(CadastroAvaliacaoDTO dados) {

        validadorAvaliacao.validar(dados);

        Usuario usuario = usuarioRepository.findById(dados.id_usuario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado de id " + dados.id_usuario() + "."));
        Receita receita = receitaRepository.findById(dados.id_receita())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + dados.id_receita() + "."));

        return new Avaliacao(dados.nota(), dados.comentario(), usuario, receita);
    }

    @Transactional
    public Avaliacao alterar(Long id, AlteracaoAvaliacaoDTO dados) {

        var avaliacao = avaliacaoRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException("Avaliação não encontrada com id " + id + "."));

        validadorUsuario.validarAutenticacao(avaliacao.getUsuario().getId());

        if(dados.nota() != null) {
            validadorAvaliacao.validarNota(dados.nota());
            avaliacao.setNota(dados.nota());
        }
        if(dados.comentario() != null && !dados.comentario().isBlank()) {
            avaliacao.setComentario(dados.comentario());
        }

        return avaliacao;
    }

}
