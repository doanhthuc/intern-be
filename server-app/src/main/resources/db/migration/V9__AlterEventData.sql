alter table if exists public.events
add column if not exists description varchar(255);

alter table if exists public.events
drop column if exists location;