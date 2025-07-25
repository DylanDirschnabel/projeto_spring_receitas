package dylan.senior.projeto.repositories;

import dylan.senior.projeto.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("SELECT COUNT(obj) > 0 " +
            "FROM Avaliacao obj " +
            "WHERE obj.receita.id = :idReceita " +
            "AND obj.usuario.id = :idUsuario")
    boolean existsByIdReceitaIdUsuario(Long idReceita, Long idUsuario);

}
