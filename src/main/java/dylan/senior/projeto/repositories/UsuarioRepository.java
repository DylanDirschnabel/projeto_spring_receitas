package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByLogin(String login);

    Optional<Usuario> findByLogin(String login);
}
