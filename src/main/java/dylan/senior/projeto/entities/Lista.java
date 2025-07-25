package dylan.senior.projeto.entities;

import dylan.senior.projeto.dtos.cadastro.CadastroListaDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "listas")
@Entity(name = "Lista")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String nome;
    @Setter
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "receitas_listas",
            joinColumns = @JoinColumn(name = "id_lista", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_receita", referencedColumnName = "id")
    )
    private Set<Receita> receitas = new HashSet<>();

    public Lista(String descricao, String nome, Usuario usuario) {
        this.descricao = descricao;
        this.nome = nome;
        this.usuario = usuario;
    }

}
