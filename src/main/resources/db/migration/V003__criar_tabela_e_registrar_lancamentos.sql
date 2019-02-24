CREATE TABLE lancamentos(
  id BIGSERIAL PRIMARY KEY,
  descricao VARCHAR(60) NOT NULL,
  data_vencimento DATE NOT NULL,
  data_pagamento DATE,
  valor DECIMAL(10,2) NOT NULL,
  observacao VARCHAR(100),
  tipo VARCHAR(20) NOT NULL,
  codigo_categoria BIGINT NOT NULL,
  codigo_pessoa BIGINT NOT NULL,
  FOREIGN KEY (codigo_categoria) REFERENCES categoria(id),
  FOREIGN KEY (codigo_pessoa) REFERENCES pessoa(id)
);

INSERT INTO lancamentos (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, codigo_categoria, codigo_pessoa)
VALUES ('Lancamento Receita', CURRENT_DATE, CURRENT_DATE, 999.99, 'Teste receita.', 'RECEITA', 5, 1);

INSERT INTO lancamentos (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, codigo_categoria, codigo_pessoa)
VALUES ('Lancamento Despesa', CURRENT_DATE, CURRENT_DATE, 999.99, 'Teste despesa.', 'DESPESA', 5, 1);
