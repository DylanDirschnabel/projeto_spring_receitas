package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByNome(String tag);

    Optional<Tag> findByNome(String tag);
}
