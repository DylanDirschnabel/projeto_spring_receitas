ALTER TABLE usuarios
ADD CONSTRAINT login_unico UNIQUE (login);

ALTER TABLE tags
ADD CONSTRAINT tag_unica UNIQUE (nome);

ALTER TABLE avaliacoes
ADD CONSTRAINT avaliacao_unica UNIQUE (id_receita, id_usuario);