INSERT INTO public.roles (id, role_name) VALUES (1, 'ORGANIZER');
INSERT INTO public.roles (id, role_name) VALUES (2, 'PARTICIPANT');
INSERT INTO public.roles (id, role_name) VALUES (3, 'QUESTION_MAKER');
INSERT INTO public.roles (id, role_name) VALUES (4, 'ADMIN');

INSERT INTO public.users (avatar, name, password, username) VALUES (null, 'ADMIN EASYQUIZY MGM', '$2a$10$6p.elkkYKKnNtrfZLPItzOf8BZCX5.eb38v43WF8gglRDkTlKYdUG', 'mgm_eq_admin');
INSERT INTO public.users (avatar, name, password, username) VALUES (null, 'ORGANIZER EASYQUIZY MGM', '$2a$10$TylhqIYO8yzVJOXUeSYTs.prF/fGjfrHlK3r4TJ5J.g/BdrplSHUO', 'mgm_eq_ORGANIZER');
INSERT INTO public.users (avatar, name, password, username) VALUES (null, 'QUESTIONMAKER EASYQUIZY MGM', '$2a$10$HGzH7OdKa8OIW43tAKMFXetVaP0JdrhVzhd/QnKDpJreoV48neXYW', 'mgm_eq_QUESTIONMAKER');
INSERT INTO public.users (avatar, name, password, username) VALUES (null, 'PARTICIPANT EASYQUIZY MGM', '$2a$10$yD4utxuGBsXX1pPGeseddO1RR4U5E0N3siPHAmOZnmZzI3rtvLWu.', 'mgm_eq_PARTICIPANT');

INSERT INTO public.user_role (user_id, role_id) VALUES (1, 4);
INSERT INTO public.user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO public.user_role (user_id, role_id) VALUES (3, 3);
INSERT INTO public.user_role (user_id, role_id) VALUES (4, 2);