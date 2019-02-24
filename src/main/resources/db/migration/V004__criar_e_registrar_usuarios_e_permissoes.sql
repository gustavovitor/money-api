CREATE TABLE usuario(
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(60) NOT NULL,
  email VARCHAR(60) NOT NULL,
  senha VARCHAR(150) NOT NULL
);

CREATE TABLE permissao(
  id BIGSERIAL PRIMARY KEY,
  descricao VARCHAR(60) NOT NULL
);

CREATE TABLE usuario_permissao(
  codigo_usuario BIGINT NOT NULL,
  codigo_permissao BIGINT NOT NULL,
  PRIMARY KEY (codigo_usuario, codigo_permissao),
  FOREIGN KEY (codigo_usuario) REFERENCES usuario(id),
  FOREIGN KEY (codigo_permissao) REFERENCES permissao(id)
);

INSERT INTO usuario (nome, email, senha) VALUES ('Administrador', 'admin@threelayers.com', '$2a$10$hOZf963ihmCOteQuzuFLSeTkI8gIoAsF.JmLdmEh8n20UJ2Dw/i5O');

INSERT INTO permissao (descricao) VALUES ('ROLE_CADASTRAR_CATEGORIA');
INSERT INTO permissao (descricao) VALUES ('ROLE_PESQUISAR_CATEGORIA');

INSERT INTO permissao (descricao) VALUES ('ROLE_CADASTRAR_PESSOA');
INSERT INTO permissao (descricao) VALUES ('ROLE_REMOVER_PESSOA');
INSERT INTO permissao (descricao) VALUES ('ROLE_PESQUISAR_PESSOA');

INSERT INTO permissao (descricao) VALUES ('ROLE_CADASTRAR_LANCAMENTO');
INSERT INTO permissao (descricao) VALUES ('ROLE_REMOVER_LANCAMENTO');
INSERT INTO permissao (descricao) VALUES ('ROLE_PESQUISAR_LANCAMENTO');

INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,1);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,2);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,3);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,4);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,5);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,6);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,7);
INSERT INTO usuario_permissao (codigo_usuario, codigo_permissao) VALUES (1,8);

