create table if not exists public.kahoot_account
(
    uuid   varchar(255)
    primary key,
    access_token text not null,
    expire_time bigint not null,
    email      varchar(255) not null,
    username      varchar(255) not null
);

alter table public.kahoot_account
    owner to easyquizy;