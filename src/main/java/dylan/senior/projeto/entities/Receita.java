package dylan.senior.projeto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "receitas")
@Entity(name = "Receita")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ElementCollection
    @CollectionTable(name = "receita_ingredientes", joinColumns = @JoinColumn(name = "id_receita"))
    @Column(name = "ingrediente")
    private List<String> ingredientes;

    @Setter
    private String corpo;
    @Setter
    private String nome;

    @ManyToOne
    @JoinColumn(name = "id_criador", referencedColumnName = "id")
    private Usuario criador;
    private LocalDateTime dtCriacao;

    @ManyToMany
    @JoinTable(
            name = "receitas_tags",
            joinColumns = @JoinColumn(name = "id_receita", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "id_tag", referencedColumnName = "id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "receita")
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    public Receita(List<String> ingredientes, String corpo, String nome, Usuario usuario, LocalDateTime data) {
        this.ingredientes = ingredientes;
        this.corpo = corpo;
        this.nome = nome;
        this.criador = usuario;
        this.dtCriacao = data;
    }
}
