#  Projeto de Pweb2 e Padrões de projetos


## Para iniciar a aplicação é necessários subir o container docker:
### # cd /demo/docker
### # docker-comh4ose up -d


## Após subir o container docker é preciso criar as tabelas Users e Authority:

### create table users( username varchar (50) not null primary key, password varchar (500) not null, enabled boolean not null);

### create table authorities ( username varchar(50) not null, authority varchar(50) not null, constraint fk_authorities_users foreign key(username) references users(username));

### create unique index ix_auth_username on authorities (username,authority)




