package dylan.senior.projeto.controllers;

import dylan.senior.projeto.dtos.cadastro.CadastroTagDTO;
import dylan.senior.projeto.dtos.listagem.ListagemTagDTO;
import dylan.senior.projeto.entities.Tag;
import dylan.senior.projeto.infra.exceptions.exception.EntidadeNaoEncontradaException;
import dylan.senior.projeto.repositories.TagRepository;
import dylan.senior.projeto.validacoes.ValidadorTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @InjectMocks
    private TagController tagController;

    @Mock
    private ValidadorTag validadorTag;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CadastroTagDTO cadastroTagDTO;

    private Tag tag;

    @BeforeEach
    public void setUp() {
        tag = new Tag("nome");
    }

    // ---- Testes 'cadastrar' ---- //

    @Test
    @DisplayName("Teste 'cadastrar'")
    public void teste1() {

        CadastroTagDTO dados = new CadastroTagDTO("Nova Tag");

        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");


        ResponseEntity<ListagemTagDTO> response = tagController.cadastrar(dados, uriBuilder);

        assertAll("testes",
                () -> assertEquals("201 CREATED", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("Nova Tag", response.getBody().nome());
                }
                );


        verify(validadorTag).validar("Nova Tag");
        verify(tagRepository).save(any(Tag.class));

    }

    // ---- Testes 'listar' ---- //

    @Test
    @DisplayName("Teste 'listar'")
    public void teste2() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Tag> pageTags = new PageImpl<>(List.of(tag));
        when(tagRepository.findAll(pageable)).thenReturn(pageTags);


        ResponseEntity<Page<ListagemTagDTO>> response = tagController.listar(pageable);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> assertNotNull(response.getBody()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals(1, response.getBody().getTotalElements());
                },
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().getContent().get(0).nome());
                }
                );

        verify(tagRepository).findAll(pageable);

    }

    // ---- Testes 'detalhar' ---- //

    @Test
    @DisplayName("Teste 'detalhar': tag válida")
    public void teste3() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        ResponseEntity<ListagemTagDTO> response = tagController.detalhar(1L);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                }
                );

        verify(tagRepository).findById(1L);
    }
    @Test
    @DisplayName("Teste 'detalhar': tag não encontrada")
    public void teste4() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("Tag não encontrada de id 1.", assertThrows(EntidadeNaoEncontradaException.class, () -> tagController.detalhar(1L)).getMessage());
    }

    // ---- Testes 'detalharPorNome' ---- //

    @Test
    @DisplayName("Teste 'detalharPorNome': nome válido")
    public void teste5() {
        when(tagRepository.findByNome("nome")).thenReturn(Optional.of(tag));

        CadastroTagDTO dados = new CadastroTagDTO("nome");

        ResponseEntity<ListagemTagDTO> response = tagController.detalharPorNome(dados);

        assertAll("testes",
                () -> assertEquals("200 OK", response.getStatusCode().toString()),
                () -> {
                    assertNotNull(response.getBody());
                    assertEquals("nome", response.getBody().nome());
                }
        );

        verify(tagRepository).findByNome("nome");
    }
    @Test
    @DisplayName("Teste 'detalhar': tag não encontrada")
    public void teste6() {
        when(tagRepository.findByNome("nome")).thenReturn(Optional.empty());

        CadastroTagDTO dados = new CadastroTagDTO("nome");

        assertEquals("Tag não encontrada de nome 'nome'.", assertThrows(EntidadeNaoEncontradaException.class, () -> tagController.detalharPorNome(dados)).getMessage());
    }

}