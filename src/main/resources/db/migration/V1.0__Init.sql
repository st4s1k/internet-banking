create table users (
    id serial primary key,
    name varchar(20) not null unique
);

create table accounts (
    id serial primary key,
    funds numeric not null,
    user_id integer references users(id) on delete cascade
);