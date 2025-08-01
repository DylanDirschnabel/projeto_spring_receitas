ALTER TABLE receita_ingredientes
ALTER COLUMN id_receita TYPE BIGINT;

ALTER TABLE receitas_listas
ALTER COLUMN id_lista TYPE BIGINT;
ALTER TABLE receitas_listas
ALTER COLUMN id_receita TYPE BIGINT;

ALTER TABLE receitas
ALTER COLUMN id_criador TYPE BIGINT;

ALTER TABLE avaliacoes
ALTER COLUMN id_receita TYPE BIGINT;

ALTER TABLE receitas_tags
ALTER COLUMN id_tag TYPE BIGINT;
ALTER TABLE receitas_tags
ALTER COLUMN id_receita TYPE BIGINT;