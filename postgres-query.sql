CREATE DATABASE minhasfinancas;

CREATE SCHEMA financas;

CREATE TABLE financas.usuario
(
  id bigserial NOT NULL PRIMARY KEY,
  nome character varying(150),
  email character varying(100),
  senha character varying(20),
  data_cadastro date default now()
);

CREATE TABLE financas.lancamento
(
  id bigserial NOT NULL PRIMARY KEY ,
  descricao character varying(100) NOT NULL,
  mes integer NOT NULL,
  ano integer NOT NULL,
  valor numeric(16,2),
  tipo character varying(20) check (tipo in ('RECEITA', 'DESPESA')) not NULL,
  status character varying(20) check (status in ('PENDENTE', 'CANCELADO', 'EFETIVADO')) not NULL,
  id_usuario bigint REFERENCES financas.usuario (id) not NULL,
  data_cadastro date default now()
);
