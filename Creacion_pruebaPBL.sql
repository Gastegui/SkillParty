drop database if exists pruebapbl;
create database pruebapbl;

drop user if exists 'pruebapbluser';
create user pruebapbluser identified by 'pruebapbluser';
grant all on pruebapbl.* to pruebapbluser;

use pruebapbl;

drop table if exists usuario;
CREATE TABLE usuario (
    id bigint AUTO_INCREMENT NOT NULL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    account_non_expired BOOLEAN NOT NULL,
    account_non_locked BOOLEAN NOT NULL,
    credentials_non_expired BOOLEAN NOT NULL
);

drop table if exists autoridad;
CREATE TABLE autoridad (
    id bigint AUTO_INCREMENT NOT NULL PRIMARY KEY,
    autoridad VARCHAR(255) NOT NULL UNIQUE
);

drop table if exists usuario_autoridad;
CREATE TABLE usuario_autoridad (
    usuario_id bigint NOT NULL,
    autoridad_id bigint NOT NULL,
    PRIMARY KEY (usuario_id, autoridad_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (autoridad_id) REFERENCES autoridad(id)
);