-- Insert roles
INSERT INTO authority (name)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM authority WHERE name = 'ROLE_ADMIN');

INSERT INTO authority (name)
SELECT 'ROLE_AGENT'
WHERE NOT EXISTS (SELECT 1 FROM authority WHERE name = 'ROLE_AGENT');

-- Insert users (escape 'user' table)
INSERT INTO "user" (id, login, password_hash, first_name, last_name, email, image_url, activated, lang_key, created_by, last_modified_by)
SELECT 1, 'admin', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC',
       'Administrator', 'Administrator', 'admin@localhost', NULL, true, 'en', 'system', 'system'
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE login = 'admin');

INSERT INTO "user" (id, login, password_hash, first_name, last_name, email, image_url, activated, lang_key, created_by, last_modified_by)
SELECT 2, 'user', '$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K',
       'User', 'User', 'user@localhost', NULL, true, 'en', 'system', 'system'
WHERE NOT EXISTS (SELECT 1 FROM "user" WHERE login = 'user');

-- User ↔ Role mapping
INSERT INTO user_authority (user_id, authority_name)
SELECT 1, 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM user_authority WHERE user_id = 1 AND authority_name = 'ROLE_ADMIN');

INSERT INTO user_authority (user_id, authority_name)
SELECT 1, 'ROLE_AGENT'
WHERE NOT EXISTS (SELECT 1 FROM user_authority WHERE user_id = 1 AND authority_name = 'ROLE_AGENT');

INSERT INTO user_authority (user_id, authority_name)
SELECT 2, 'ROLE_AGENT'
WHERE NOT EXISTS (SELECT 1 FROM user_authority WHERE user_id = 2 AND authority_name = 'ROLE_AGENT');
