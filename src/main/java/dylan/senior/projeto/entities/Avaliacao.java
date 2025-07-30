package dylan.senior.projeto.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "avaliacoes")
@Entity(name = "Avaliacao")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private int nota;
    private LocalDateTime dtCriacao;
    @Setter
    private String comentario;

    @Setter
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_receita", referencedColumnName = "id")
    private Receita receita;

    public Avaliacao(int nota, String comentario, Usuario usuario, Receita receita) {
        this.nota = nota;
        this.comentario = comentario;
        this.usuario = usuario;
        this.receita = receita;
        this.dtCriacao = LocalDateTime.now();
    }

}
