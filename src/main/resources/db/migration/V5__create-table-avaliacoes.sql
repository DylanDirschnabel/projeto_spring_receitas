create table avaliacoes
(
    id serial,
    id_receita int,
    id_usuario int,
    nota int check(nota between 0 and 10),
    descricao text,
    primary key(id),
    foreign key(id_receita) references receitas(id) on delete cascade,
    foreign key(id_usuario) references usuarios(id) on delete cascade
)