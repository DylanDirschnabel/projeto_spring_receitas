package dylan.senior.projeto.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dylan.senior.projeto.dtos.busca.BuscaReceitaDTO;
import dylan.senior.projeto.dtos.alteracao.AlteracaoReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemBuscaReceitaDTO;
import dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroGeradoReceitaDTO;
import dylan.senior.projeto.dtos.cadastro.CadastroReceitaDTO;
import dylan.senior.projeto.dtos.detalhamento.DetalhamentoReceitaDTO;
import dylan.senior.projeto.dtos.listagem.ListagemAvaliacaoDTO;
import dylan.senior.projeto.entities.Receita;
import dylan.senior.projeto.entities.Tag;
import dylan.senior.projeto.entities.Usuario;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.infra.exceptions.exception.ValidacaoException;
import dylan.senior.projeto.repositories.ReceitaRepository;
import dylan.senior.projeto.repositories.TagRepository;
import dylan.senior.projeto.repositories.UsuarioRepository;
import dylan.senior.projeto.validacoes.ValidadorUsuario;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReceitaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ReceitaRepository receitaRepository;

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private ValidadorUsuario validadorUsuario;

    @Transactional
    public Receita criarReceita(CadastroReceitaDTO dados) {
        Usuario usuario = usuarioRepository.findById(dados.id_criador())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado de id " + dados.id_criador() + "."));
        LocalDateTime data = LocalDateTime.now();

        var receita = new Receita(dados.ingredientes(), dados.corpo(), dados.nome(), usuario, data);

        dados.tags().forEach(tag -> adicionarTag(receita, tag));

        return receita;
    }

    @Transactional
    public void adicionarTag(Receita receita, String tag) {
        if(receita == null || tag == null || tag.isBlank()) {
            throw new ValidacaoException("Dados inválidos para adição de tag na receita");
        }

        if(tagRepository.existsByNome(tag)) {

            var t = tagRepository.findByNome(tag);
            receita.getTags().add((t.get()));

        } else {

            var t = new Tag(tag);
            receita.getTags().add(t);
            tagRepository.save(t);

        }

    }

    @Transactional
    public void removerTag(Receita receita, String tag) {
        if(receita == null || tag == null) {
            throw new ValidacaoException("Dados inválidos para remoção de tag da receita");
        }

        var t = tagRepository.findByNome(tag)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Tag não encontrada com nome '" + tag + "'."));
        receita.getTags().remove(t);
    }

    @Transactional
    public void adicionarIngrediente(Receita receita, String ingrediente) {
        if(receita == null || ingrediente == null) {
            throw new ValidacaoException("Dados inválidos para adição de ingrediente da receita");
        }
        receita.getIngredientes().add(ingrediente);
    }

    @Transactional
    public void removerIngrediente(Receita receita, String ingrediente) {
        if(receita == null || ingrediente == null) {
            throw new ValidacaoException("Dados inválidos para remoção de ingrediente da receita");
        }

        if(!receita.getIngredientes().contains(ingrediente)) {
            throw new ValidacaoException("Receita não contém o ingrediente '" + ingrediente + "'.");
        }
        receita.getIngredientes().remove(ingrediente);
    }

    @Transactional
    public DetalhamentoReceitaDTO detalhar(Receita receita) {
        if(receita == null) {
            return null;
        }

        List<String> tags = receita.getTags().stream().map(Tag::getNome).toList();
        List<ListagemAvaliacaoDTO> lista =
                receita.getAvaliacoes().stream().map(x -> new ListagemAvaliacaoDTO(
                        x.getId(),
                        x.getNota(),
                        x.getUsuario().getNome(),
                        x.getUsuario().getId(),
                        x.getComentario())).toList();

        return new DetalhamentoReceitaDTO(
                receita.getId(),
                receita.getNome(),
                tags,
                receita.getIngredientes(),
                receita.getCorpo(),
                receita.getDtCriacao().toString(),
                receita.getCriador().getId(),
                receita.getCriador().getNome(),
                lista
        );
    }

    @Transactional
    public Receita alterar(Long id, AlteracaoReceitaDTO dados) {

        Receita receita = receitaRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Receita não encontrada de id " + id + "."));

        validadorUsuario.validarAutenticacao(receita.getCriador().getId());

        if(dados == null) {
            return receita;
        }

        if(dados.nome() != null && !dados.nome().isBlank()) {
            receita.setNome(dados.nome());
        }
        if(dados.corpo() != null && !dados.corpo().isBlank()) {
            receita.setCorpo(dados.corpo());
        }


        return receita;
    }


    @Transactional
    public List<ListagemBuscaReceitaDTO> buscaInclusiva(BuscaReceitaDTO dados) {
        if(dados == null) {
            return new ArrayList<>();
        }

        return receitaRepository.buscaInclusivaTags(
                dados.inclusas().stream().map(String::toLowerCase).toList(),
                dados.exclusas().stream().map(String::toLowerCase).toList())
                .stream().map(x -> new ListagemBuscaReceitaDTO(x, receitaRepository.findTagsById(x.id()))).toList();
    }

    @Transactional
    public List<ListagemBuscaReceitaDTO> buscaExclusiva(BuscaReceitaDTO dados) {
        if(dados == null) {
            return new ArrayList<>();
        }

        return receitaRepository.buscaExclusivaTags(dados.inclusas().stream().map(String::toLowerCase).toList(),
                                                    dados.exclusas().stream().map(String::toLowerCase).toList(),
                                                    dados.inclusas().size())
                .stream().map(x ->  new ListagemBuscaReceitaDTO(x, receitaRepository.findTagsById(x.id()))).toList();
    }

    @Transactional
    public List<ListagemBuscaReceitaDTO> buscaPorNome(String nome) {
        if(nome == null) {
            nome = "";
        }

        return receitaRepository.buscaPorNome(nome).stream().map(x -> new ListagemBuscaReceitaDTO(
                x, receitaRepository.findTagsById(x.id())
        )).toList();

    }

    @Transactional
    public List<ListagemBuscaReceitaDTO> buscaPorRecomendacao(Long id) {
        if(!usuarioRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Usuário não encontrado de id " + id + ".");
        }

        List<Object[]> resultado = receitaRepository.recomendarReceitas(id);
        if(resultado.isEmpty()) {
            return this.buscaPorNome(null);
        }

        List<ListagemSemTagsDTO> listagem = resultado.stream().map(r -> new ListagemSemTagsDTO(
                (Long) r[0],
                (String) r[1],
                (Double) r[2],
                (LocalDateTime) r[3],
                (String) r[4]
                )
        ).toList();

        return listagem.stream().map(r -> new ListagemBuscaReceitaDTO(
                r,
                receitaRepository.findTagsById(r.id()))
        ).toList();
    }


    String jsonSchema = """
            
            
            {
              "type": "object",
              "properties": {
                "id_criador": {
                  "type": "integer",
                  "description": "ID do criador da receita",
                  "nullable": false
                },
                "nome": {
                  "type": "string",
                  "description": "Nome da receita",
                  "minLength": 1
                },
                "corpo": {
                  "type": "string",
                  "description": "Modo de preparo da receita",
                  "minLength": 1
                },
                "ingredientes": {
                  "type": "array",
                  "description": "Lista de ingredientes",
                  "items": {
                    "type": "string"
                  },
                  "minItems": 1
                },
                "tags": {
                  "type": "array",
                  "description": "Lista de tags associadas à receita",
                  "items": {
                    "type": "string"
                  }
                }
              },
              "required": ["id_criador", "nome", "corpo", "ingredientes", "tags"],
              "additionalProperties": false
            }
            
            """;

    @Transactional
    public CadastroReceitaDTO gerar(CadastroGeradoReceitaDTO dados) throws JsonProcessingException {
        String tags = "";
        if(dados.tags() != null) {
            tags = dados.tags().toString();
        }

        Prompt prompt = new Prompt("Me construa uma nova receita, em formato de JSON," +
                " com um id do usuário de tipo 'Long'," +
                " um nome para a receita de tipo 'String'," +
                " um modo de preparo de tipo 'String'," +
                " uma lista de ingredientes de tipo 'List<String>'" +
                " e uma lista de tags de tipo 'List<String>'." +
                " Para que você faça isso, use o id do usuário '" + dados.id_usuario() + "'." +
                " Além disso, crie a receita usando como base o nome '"+ dados.nome() +"'," +
                " com as tags '" + tags + "' e com esse comentário adicional de instrução '"+ dados.comentario() +"'." +
                " O nome e tags desejados, além do comentário, podem não ser informados. Caso não sejam, faça a receita como preferir." +
                " Sinta-se livre para adicionar mais tags como preferir, e adicione tags que facilitem a busca da receita," +
                " como os principais ingredientes ou algum eletrodoméstico necessário," +
                " e adicione tags de ingredientes que são comuns alergias, como lactose, nozes e alho. Também adicione como tag os ingredientes que sejam NECESSÁRIOS para a receita. " +
                " SEMPRE adicione a tag 'IA' e NUNCA coloque a tag 'Necessário'. Faça a receita com base no idioma solicitado no comentário. Como padrão, use o português. ",
                OpenAiChatOptions.builder().
                        responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, this.jsonSchema)).build()

        );
        ChatResponse response = chatModel.call(prompt);

        String json = response.getResult().getOutput().getText();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, CadastroReceitaDTO.class);
    }




}
