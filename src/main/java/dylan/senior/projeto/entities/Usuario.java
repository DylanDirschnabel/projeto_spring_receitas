package dylan.senior.projeto.entities;

import dylan.senior.projeto.dtos.cadastro.CadastroUsuarioDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String login;
    @Setter
    private String nome;
    @Setter
    private String senha;

    @OneToMany(mappedBy = "criador")
    private Set<Receita> receitas = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    @OneToMany(mappedBy = "usuario")
    private Set<Lista> listas = new HashSet<>();

    public Usuario(CadastroUsuarioDTO dados) {
        this.login = dados.login();
        this.nome = dados.nome();
        this.senha = dados.senha();
    }

}
