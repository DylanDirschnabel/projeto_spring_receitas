package dylan.senior.projeto.repositories;

import dylan.senior.projeto.dtos.busca.ListagemSemTagsDTO;
import dylan.senior.projeto.entities.Receita;
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
}
