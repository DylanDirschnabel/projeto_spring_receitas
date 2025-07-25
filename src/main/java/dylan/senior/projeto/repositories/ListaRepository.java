package dylan.senior.projeto.repositories;

import dylan.senior.projeto.dtos.listagem.ListagemListaDTO;
import dylan.senior.projeto.entities.Lista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ListaRepository extends JpaRepository<Lista, Long> {


    @Query("""
            
            SELECT l
            FROM Lista l
            WHERE l.usuario.id = :id
            
            """)
    List<Lista> listarPorUsuario(Long id);

}
