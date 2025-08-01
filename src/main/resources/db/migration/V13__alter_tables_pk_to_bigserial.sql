

ALTER TABLE tags
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE usuarios
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE receitas
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE receita_ingredientes
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE avaliacoes
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE listas
    ALTER COLUMN id TYPE BIGINT;

ALTER SEQUENCE tags_id_seq OWNED BY tags.id;
ALTER SEQUENCE usuarios_id_seq OWNED BY usuarios.id;
ALTER SEQUENCE receitas_id_seq OWNED BY receitas.id;
ALTER SEQUENCE receita_ingredientes_id_seq OWNED BY receita_ingredientes.id;
ALTER SEQUENCE avaliacoes_id_seq OWNED BY avaliacoes.id;
ALTER SEQUENCE listas_id_seq OWNED BY listas.id;
