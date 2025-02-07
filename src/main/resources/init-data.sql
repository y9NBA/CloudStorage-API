INSERT INTO "user" (username, password, email)
VALUES ('John Doe', '$2a$12$fMT6CAqYzNnHR.pX96o2AuGDwE6JiOyzVqV90/etj.ygkI2bpEQkS', 'johndoe@example.com');

INSERT INTO user_role (user_id, role)
VALUES ((SELECT id FROM "user" WHERE username = 'John Doe'), 'ROLE_ADMIN'),
       ((SELECT id FROM "user" WHERE username = 'John Doe'), 'ROLE_USER');

INSERT INTO file (user_id, file_name, file_size, mime_type, url)
VALUES ((SELECT id FROM "user" WHERE username = 'John Doe'),'Test.txt', '100', 'application/txt', 'home/y9nba/Text.txt');

INSERT INTO file_access (file_id, user_id, access_level)
VALUES ((SELECT id FROM file WHERE user_id = (SELECT id FROM "user" WHERE username = 'John Doe')), (SELECT id FROM "user" WHERE username = 'John Doe'), 'ACCESS_EDITOR');

INSERT INTO audit_log (user_id, action, file_id)
VALUES ((SELECT id FROM "user" WHERE username = 'John Doe'), 'ACTION_UPDATE', (SELECT id FROM file WHERE user_id = (SELECT id FROM "user" WHERE username = 'John Doe')));