drop database if exists skillparty;
create database if not exists skillparty;
use skillparty;

CREATE TABLE ficheros 
(
    id bigint auto_increment PRIMARY KEY,
    direccion VARCHAR(255) not null unique,
	extension varchar(255) not null
);

insert into ficheros values(1, '/home/julen/SkillPartyFiles/admin/profile', '.jpg');

create table usuarios
(
	id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	nombre VARCHAR(255) NOT NULL,
	apellidos VARCHAR (255) NOT NULL,
	fecha_de_nacimiento datetime(6) NOT NULL,
	telefono varchar(255),
	email VARCHAR(255) not null,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    account_non_expired BOOLEAN NOT NULL,
    account_non_locked BOOLEAN NOT NULL,
    credentials_non_expired BOOLEAN NOT NULL,
	saldo decimal(38, 2) not null,
	por_cobrar decimal(38, 2) not null,
    puntuacion bigint not null,
    imagen_id bigint 
);

alter table usuarios add constraint FK_usuarios foreign key (imagen_id) references ficheros (id);

insert into usuarios values(1, 'Administrator', 'Owner', '2000-01-01', '000000000', 'julen.gallastegui@alumni.mondragon.edu',
'admin', '$2a$10$JNg3mDt2kJ8vujwPTsNjO.npMYYsonSFWsFODSIolDk5AI3BFj9FO', 1, 1, 1, 1, 1000, 0, 0, 1);

CREATE TABLE autoridades
(
    id bigint AUTO_INCREMENT NOT NULL PRIMARY KEY,
    autoridad VARCHAR(255) NOT NULL UNIQUE
);

insert into autoridades values (1, "ADMIN");
insert into autoridades values (2, "SUPPORT");
insert into autoridades values (3, "CREATE_ALL");
insert into autoridades values (5, "CREATE_SERVICE");
insert into autoridades values (6, "CREATE_COURSE");
insert into autoridades values (7, "USER_PRO");
insert into autoridades values (8, "USER");

CREATE TABLE usuarios_autoridades
(
    usuario_id bigint NOT NULL,
    autoridad_id bigint NOT NULL,
    PRIMARY KEY (usuario_id, autoridad_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (autoridad_id) REFERENCES autoridades(id)
);

insert into usuarios_autoridades values (1, 1);

create table categorias
(
	id bigint auto_increment primary key,
	descripcion VARCHAR(255) not null
);

create table contactos
(
	id bigint auto_increment PRIMARY KEY,
	cliente_id BIGINT not null,
	creador_id BIGINT not null,
	fecha DATE NOT NULL,
	descripcion VARCHAR(255) not null
);
alter table contactos add constraint FK_ponerseEnContacto foreign key (cliente_id) references usuarios (id);
alter table contactos add constraint FK2_ponerseEnContacto foreign key (creador_id) references usuarios (id);

create table mensajes
(
	id bigint auto_increment PRIMARY KEY,
	contacto_id bigint not null,
	usuario_id BIGINT not null,
    texto VARCHAR(500) not null,
	fecha_envio datetime(6) NOT NULL,
	fichero_id bigint
);

alter table mensajes add constraint FK_mensajes foreign key (contacto_id) references contactos (id);
alter table mensajes add constraint FK2_mensajes foreign key (usuario_id) references usuarios (id);
alter table mensajes add constraint FK3_mensajes foreign key (fichero_id) references ficheros (id);

create table idiomas
(
	id bigint auto_increment primary key,
	idioma varchar(255) not null unique
);

insert into idiomas values (1, "es");
insert into idiomas values (2, "eu");
insert into idiomas values (3, "en");

create table tipos
(
	id bigint auto_increment primary key,
	descripcion VARCHAR(255) not null
);

create table cursos
(
	id bigint auto_increment primary key,
	titulo VARCHAR(255) not null,
	descripcion VARCHAR(1000) not null,
	fecha_de_creacion datetime(6) NOT NULL,
	fecha_de_actualizacion datetime(6) NOT NULL,
	creador_id BIGINT not null,
    portada_id bigint,
	tipo_id bigint not null,
    precio decimal(38, 2) check(precio >= 0) not null,
	idioma_id bigint not null,
    publicado boolean not null,
	verificado boolean not null,
    puntuacion bigint not null
);

alter table cursos add constraint FK_cursos foreign key (creador_id) references usuarios (id);
alter table cursos add constraint FK2_cursos foreign key (tipo_id) references tipos (id);
alter table cursos add constraint FK3_cursos foreign key (portada_id) references ficheros(id);
alter table cursos add constraint FK4_cursos foreign key (idioma_id) references idiomas(id);

create table servicios
(
	id bigint auto_increment primary key,
	titulo VARCHAR(255) not null unique,
	descripcion VARCHAR(1000) not null,
	fecha_de_creacion datetime(6) NOT NULL,
	fecha_de_actualizacion datetime(6) NOT NULL,
	creador_id BIGINT not null,
    portada_id bigint,
	categoria_id bigint not null,
	idioma_id bigint not null,
    publicado boolean not null,
	verificado boolean not null,
    puntuacion bigint not null
);

alter table servicios add constraint FK_servicios foreign key (creador_id) references usuarios (id);
alter table servicios add constraint FK2_servicios foreign key (categoria_id) references categorias (id);
alter table servicios add constraint FK3_servicios foreign key (portada_id) references ficheros(id);
alter table servicios add constraint FK4_servicios foreign key (idioma_id) references idiomas(id);


create table opciones
(
	id bigint auto_increment primary key,
    servicio_id bigint not null,
	descripcion VARCHAR(255) not null,
    precio decimal(38, 2) check(precio >= 0) not null
);

alter table opciones add constraint FK_opciones foreign key (servicio_id) references servicios (id);

create table comprar_cursos
(
	id bigint auto_increment primary key,
	usuario_id BIGINT not null,
	curso_id bigint not null,
	fecha_de_compra datetime(6) NOT NULL,
    precio decimal(38, 2) check(precio >= 0) not null,
	terminado boolean not null
);

alter table comprar_cursos add constraint FK_comprar_cursos foreign key (usuario_id) references usuarios (id);
alter table comprar_cursos add constraint FK2_comprar_cursos foreign key (curso_id) references cursos (id);

create table comprar_servicios
(
	id bigint auto_increment primary key,
	usuario_id BIGINT not null, #Comprador
	servicio_id bigint not null,
	fecha_de_compra datetime(6) NOT NULL,
    opcion_id bigint not null,
	terminado boolean not null
);

alter table comprar_servicios add constraint FK_comprar_servicios foreign key (usuario_id) references usuarios (id);
alter table comprar_servicios add constraint FK2_comprar_servicios foreign key (servicio_id) references servicios (id);
alter table comprar_servicios add constraint FK3_comprar_servicios foreign key (opcion_id) references opciones (id);

create table valorar_cursos
(
	id bigint auto_increment primary key,
	usuario_id BIGINT not null,
	curso_id bigint not null,
	valoracion bigint not null,
    comentario VARCHAR(255),
	fecha_valoracion datetime(6) not null
);

alter table valorar_cursos add constraint FK_valorar_cursos foreign key (usuario_id) references usuarios (id);
alter table valorar_cursos add constraint FK2_valorar_cursos foreign key (curso_id) references cursos (id);

create table valorar_servicios
(
	id bigint auto_increment primary key,
	usuario_id BIGINT not null,
	servicio_id bigint not null,	
	valoracion bigint not null,
    comentario VARCHAR(255),
	fecha_valoracion datetime(6) not null
);

alter table valorar_servicios add constraint FK_valorar_servicios foreign key (usuario_id) references usuarios (id);
alter table valorar_servicios add constraint FK2_valorar_servicios foreign key (servicio_id) references servicios (id);

create table muestras #las imagenes de los servicios
(
	id bigint auto_increment primary key,
    servicio_id bigint not null,
    posicion bigint not null,
    multimedia_id bigint
);

alter table muestras add constraint FK_muestras foreign key (servicio_id) references servicios (id);
alter table muestras add constraint FK2_muestras foreign key (multimedia_id) references ficheros (id);

create table elementos #los elementos de los cursos
(
	id bigint auto_increment primary key,
    curso_id bigint not null,
    titulo VARCHAR(255) not null,
    posicion bigint not null,
    multimedia_id bigint,
	texto VARCHAR(500),
    fichero_id bigint
);

alter table elementos add constraint FK_elementos foreign key (curso_id) references cursos (id);
alter table elementos add constraint FK2_elementos foreign key (multimedia_id) references ficheros (id);
alter table elementos add constraint FK3_elementos foreign key (fichero_id) references ficheros (id);

select * from autoridades;
select * from usuarios;
select * from usuarios_autoridades;