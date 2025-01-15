create table customer
(
    id           uuid               not null,
    name         varchar(50)        not null,
    phone_number varchar(50) unique not null,
    email        varchar(100),
    primary key (id)
);