create table receitas_tags
(
    id_receita int,
    id_tag int,
    primary key(id_receita, id_tag),
    foreign key(id_receita) references receitas(id) on delete cascade,
    foreign key(id_tag) references tags(id)
);