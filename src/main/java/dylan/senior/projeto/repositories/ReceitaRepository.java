package dylan.senior.projeto.repositories;

import dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO;
import dylan.senior.projeto.entities.Receita;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ReceitaRepository extends JpaRepository<Receita, Long> {


    @Query("""

            SELECT new dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO(
            r.id,
            r.nome,
            ROUND(COALESCE(AVG(a.nota), 0), 2),
            r.dtCriacao,
            r.criador.nome
            )
            FROM Receita r
            LEFT JOIN r.avaliacoes a
            WHERE LOWER(r.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            GROUP BY r.id, r.criador.nome
            ORDER BY COALESCE(AVG(a.nota), 0) DESC

            """)
    List<ListagemSemTagsDTO> buscaPorNome(@Param("nome") String nome);


    @Query("""
            
            SELECT new dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO(
            r.id,
            r.nome,
            ROUND(COALESCE(AVG(a.nota), 0), 2),
            r.dtCriacao,
            r.criador.nome
            )
            FROM Receita r
            JOIN r.tags t
            LEFT JOIN r.avaliacoes a
            WHERE LOWER(t.nome) IN :inclusas
            AND r.id NOT IN
            (SELECT r2.id FROM Receita r2 JOIN r2.tags t2 WHERE LOWER(t2.nome) IN :exclusas)
            GROUP BY r.id, r.criador.nome
            HAVING COUNT(DISTINCT LOWER(t.nome)) = :qtInclusas
            ORDER BY COALESCE(AVG(a.nota), 0) DESC
            
            """)
    List<ListagemSemTagsDTO> buscaExclusivaTags(List<String> inclusas, List<String> exclusas, int qtInclusas);

    @Query(value = """
            
            SELECT new dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO(
            r.id,
            r.nome,
            ROUND(COALESCE(AVG(a.nota), 0), 2),
            r.dtCriacao,
            r.criador.nome
            )
            FROM Receita r
            JOIN r.tags t
            LEFT JOIN r.avaliacoes a
            GROUP BY r.id, r.criador.nome
            ORDER BY
                SUM(CASE
                     WHEN LOWER(t.nome) IN :inclusas THEN 1
                     WHEN LOWER(t.nome) IN :exclusas THEN -1
                     ELSE 0
                END) DESC,
                COALESCE(AVG(a.nota), 0) DESC
            
            """)
    List<ListagemSemTagsDTO> buscaInclusivaTags(List<String> inclusas, List<String> exclusas);

    @Query("""
            SELECT t.nome
            FROM Receita r
            JOIN r.tags t
            WHERE r.id = :id
            """)
    List<String> findTagsById(Long id);

//    @Query("""
//            SELECT new dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO(
//            r.id,
//            r.nome,
//            ROUND(COALESCE(AVG(a.nota), 0), 2),
//            r.dtCriacao,
//            r.criador.nome
//            )
//            FROM Receita r
//            JOIN r.tags t
//            WHERE t.nome IN
//            (
//            SELECT t2.nome
//            FROM Lista l
//            JOIN l.receitas r2
//            JOIN r2.tags t2
//            WHERE l.usuario.id = :id_usuario
//            GROUP BY r2.id, t2.nome
//            ORDER BY COUNT(t2.nome) DESC
//            LIMIT 5
//            )
//            AND r.id NOT IN
//            (
//            SELECT r3.id
//            FROM Lista l2
//            JOIN l2.receitas r3
//            WHERE l.usuario.id = :id_usuario
//            )
//            AND r.id NOT IN
//            (
//            SELECT r4.id
//            FROM Avaliacao a
//            JOIN a.receita r4
//            WHERE a.usuario = :id_usuario
//            )
//            LIMIT 3
//            """)
//    List<ListagemSemTagsDTO> Recomendar(Long id_usuario);


    @Query(value = """

        SELECT r.id, r.nome, ROUND(COALESCE(AVG(a.nota), 0), 2) AS nota, r.dt_criacao, u.nome AS criador
        FROM receitas r
        JOIN receitas_tags rt ON r.id = rt.id_receita
        JOIN tags t ON rt.id_tag = t.id
        LEFT JOIN avaliacoes a ON r.id = a.id_receita
        JOIN usuarios u ON r.id_criador = u.id
        WHERE t.nome IN (
            SELECT t2.nome
            FROM listas l
            JOIN receitas_listas rl ON l.id = rl.id_lista
            JOIN receitas r2 ON rl.id_receita = r2.id
            JOIN receitas_tags rt2 ON r2.id = rt2.id_receita
            JOIN tags t2 ON rt2.id_tag = t2.id
            WHERE l.id_usuario = :idUsuario
            GROUP BY t2.nome
            ORDER BY COUNT(t2.nome) DESC
            LIMIT 5
        )
        AND r.id NOT IN (
            SELECT rl2.id_receita
            FROM listas l2
            JOIN receitas_listas rl2 ON l2.id = rl2.id_lista
            WHERE l2.id_usuario = :idUsuario
        )
        AND r.id NOT IN (
            SELECT a2.id_receita
            FROM avaliacoes a2
            WHERE a2.id_usuario = :idUsuario
        )
        GROUP BY r.id, r.nome, r.dt_criacao, u.nome
        ORDER BY nota DESC
        LIMIT 3

""", nativeQuery = true)
    List<Object[]> recomendarReceitas(@Param("idUsuario") Long idUsuario);

}
