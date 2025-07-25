create table listas
(
    id serial,
    id_usuario int,
    nome varchar(150),
    descricao varchar(255),
    primary key (id),
    foreign key(id_usuario) references usuarios(id) on delete cascade
);

create table receitas_listas
(
   id_lista int,
   id_receita int,
   primary key(id_lista, id_receita),
   foreign key(id_lista) references listas(id) on delete cascade,
   foreign key(id_receita) references receitas(id) on delete cascade
);