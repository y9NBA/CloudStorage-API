INSERT INTO "user" (username, password, email)
VALUES ('John Doe', '$2a$12$fMT6CAqYzNnHR.pX96o2AuGDwE6JiOyzVqV90/etj.ygkI2bpEQkS', 'johndoe@example.com');

INSERT INTO user_role (user_id, role)
VALUES ((SELECT id FROM "user" WHERE username = 'John Doe'), 'ROLE_ADMIN'),
       ((SELECT id FROM "user" WHERE username = 'John Doe'), 'ROLE_USER');
