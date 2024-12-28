INSERT INTO roles (role_name, description)
VALUES ('ROLE_ADMIN', 'Administrator role with full access'),
       ('ROLE_USER', 'Regular user role with limited access');

INSERT INTO users (username, password, email)
VALUES ('John Doe', '$2a$12$fMT6CAqYzNnHR.pX96o2AuGDwE6JiOyzVqV90/etj.ygkI2bpEQkS', 'johndoe@example.com');

INSERT INTO user_role (user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'John Doe'), (SELECT id FROM roles WHERE role_name = 'ROLE_ADMIN')),
       ((SELECT id FROM users WHERE username = 'John Doe'), (SELECT id FROM roles WHERE role_name = 'ROLE_USER'));
