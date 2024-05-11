drop database if exists pruebapbl;
create database pruebapbl;

create user pruebapbluser identified by 'pruebapbluser';
grant all on pruebapbl.* to pruebapbluser;

use pruebapbl;

drop table if exists usuario;
create table usuario(
	id int auto_increment primary key,
    nombre varchar(100) unique,
    contraseña varchar(100),
    rol varchar(100)
    );

insert into usuario(nombre, contraseña, rol) values ('nombre', 'contraseña', 'USER');