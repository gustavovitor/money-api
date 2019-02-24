CREATE TABLE pessoa(
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(60) NOT NULL,
  logradouro VARCHAR(120),
  numero VARCHAR(32),
  complemento VARCHAR(32),
  bairro VARCHAR(60),
  cep VARCHAR(30),
  cidade VARCHAR(80),
  estado VARCHAR(80),
  ativo BOOLEAN
);

INSERT INTO pessoa (nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo)
VALUES ('Pessoa Exemplo', 'Logradouro Exemplo', '000', '000', 'Bairro Bolinha', '00000000', 'Bobos', 'Zeros', true);
