create table callback_request
(
    id           uuid      not null,
    comment      varchar(255),
    message_customer   boolean   not null default false,
    customer_id  uuid      not null,
    created_time timestamp not null,
    FOREIGN KEY (customer_id) REFERENCES customer (id),
    primary key (id)
);