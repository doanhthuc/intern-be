create table if not exists public.kahoot_quiz_export_status
(
    kahoot_user_id varchar(255),
    kahoot_quiz_id varchar(255),
    export_status varchar(255),
    quiz_id        bigserial references public.quizzes,
    constraint quizzes_kahoot_pk PRIMARY KEY (quiz_id, kahoot_user_id)
);
alter table public.kahoot_quiz_export_status
    owner to easyquizy;