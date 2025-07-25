package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TagRepositoryTest {

    @Mock
    private TagRepository tagRepository;

    private Tag tag;

    @BeforeEach
    public void setUp() {
        tag = new Tag("teste");
    }

    @Test
    @DisplayName("existsByNome test - should be true")
    public void teste1() {
        when(tagRepository.existsByNome("teste")).thenReturn(true);

        assertTrue(tagRepository.existsByNome("teste"));
    }

    @Test
    @DisplayName("existsByNome test - should not be true")
    public void teste2() {
        when(tagRepository.existsByNome("teste")).thenReturn(false);

        assertFalse(tagRepository.existsByNome("teste"));
    }

    @Test
    @DisplayName("findByNome test - should retrieve tag")
    public void teste3() {

        when(tagRepository.findByNome("teste")).thenReturn(Optional.of(tag));

        assertEquals("teste", tagRepository.findByNome("teste").get().getNome());
    }

    @Test
    @DisplayName("findByNome test - should not retrieve tag")
    public void teste4() {

        when(tagRepository.findByNome("testeeee")).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), tagRepository.findByNome("testeeee"));
    }


}