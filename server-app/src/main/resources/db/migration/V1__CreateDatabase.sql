create table if not exists public.attachments
(
    id              bigserial
    primary key,
    attachment_type varchar(255),
    content         text
    );

alter table public.attachments
    owner to easyquizy;

create table if not exists public.categories
(
    id   bigserial
    primary key,
    name varchar(20) not null
    );

alter table public.categories
    owner to easyquizy;

create table if not exists public.events
(
    id         bigserial
    primary key,
    end_date   timestamp    not null,
    location   varchar(255) not null,
    start_date timestamp    not null,
    title      varchar(255) not null
    );

alter table public.events
    owner to easyquizy;

create table if not exists public.questions
(
    id            bigserial
    primary key,
    difficulty    varchar(255),
    time_limit    integer      not null,
    title         varchar(200) not null,
    attachment_id bigint
    constraint fk_attachment_question
    references public.attachments,
    category_id   bigint
    constraint fk_category_question
    references public.categories
    );

alter table public.questions
    owner to easyquizy;

create table if not exists public.answers
(
    id          bigserial
    primary key,
    is_correct  boolean          not null,
    text        varchar(150) not null,
    question_id bigint
    constraint fk_answer_question
    references public.questions
    );

alter table public.answers
    owner to easyquizy;

create table if not exists public.quizzes
(
    id         bigserial
    primary key,
    title      varchar(255) not null,
    event_id bigint
    constraint fk_quizzes_events
    references public.events
    );

alter table public.quizzes
    owner to easyquizy;

create table if not exists public.questions_quizzes
(
    question_id bigint not null
    constraint fkrbryjdfms9s093opa1vxvxt3e
    references public.questions,
    quiz_id     bigint not null
    constraint fk329v2fgl4anwr4mscqedch7ck
    references public.quizzes
);

alter table public.questions_quizzes
    owner to easyquizy;

create table if not exists public.roles
(
    id        bigserial
    primary key,
    role_name varchar(255)
    );

alter table public.roles
    owner to easyquizy;

create table if not exists public.users
(
    id       bigserial
    primary key,
    avatar   text,
    name     varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null
    );

alter table public.users
    owner to easyquizy;

create table if not exists public.user_role
(
    user_id bigint not null
    constraint fkj345gk1bovqvfame88rcx7yyx
    references public.users,
    role_id bigint not null
    constraint fkt7e7djp752sqn6w22i6ocqy6q
    references public.roles
);

alter table public.user_role
    owner to easyquizy;