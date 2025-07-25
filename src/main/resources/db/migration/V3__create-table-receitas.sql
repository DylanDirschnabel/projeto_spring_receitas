create table receitas
(
    id serial,
    id_criador int,
    nome varchar(150),
    corpo text,
    dt_criacao date,

    primary key(id),
    foreign key(id_criador) references usuarios(id) on delete cascade
);

create table receita_ingredientes
(
    id serial,
    ingrediente varchar(50),
    id_receita int,

    primary key(id),
    foreign key(id_receita) references receitas(id) on delete cascade
);