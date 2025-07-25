package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByLogin(String login);
}
