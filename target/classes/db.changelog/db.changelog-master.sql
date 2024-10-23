-- liquibase formatted sql

-- changeset dev:1 contextFilter:base
create table if not exists person (
    id bigint generated always as identity (start with 1) primary key,
    name varchar(255)
);
--rollback drop table person;

-- changeset dev:2 contextFilter:qa
insert into person( name ) values ( 'test name 1' );
insert into person( name ) values ( 'test name 2' );
--rollback delete from person;
