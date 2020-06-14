drop table if exists pharmacies CASCADE;

create table pharmacies (
    id bigint generated by default as identity,
    address varchar(255),
    city varchar(255),
    name varchar(255) not null,
    token varchar(255),
    primary key (id)
);

alter table pharmacies add constraint pharmacies_unique_name unique (name);
alter table pharmacies add constraint pharmacies_unique_token unique (token);



drop table if exists medicines CASCADE;

create table medicines (
    id bigint generated by default as identity,
    count bigint,
    name varchar(255),
    price decimal(19,2),
    updated_at timestamp,
    pharmacy_id bigint not null,
    primary key (id)
);

alter table medicines add constraint FKbhhcuoq8hwx6nq2afmtqbvqnt foreign key (pharmacy_id) references pharmacies;

