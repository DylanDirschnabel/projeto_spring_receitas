package dylan.senior.projeto.entities;

import dylan.senior.projeto.dtos.cadastro.CadastroTagDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Table(name = "tags")
@Entity(name = "Tag")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    public Tag(CadastroTagDTO dados) {
        this.nome = dados.nome();
    }

    public Tag(String nome) {
        this.nome = nome;
    }

}
