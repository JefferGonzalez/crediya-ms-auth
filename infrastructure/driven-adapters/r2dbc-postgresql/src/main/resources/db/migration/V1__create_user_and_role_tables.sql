CREATE TABLE users
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    names        VARCHAR(100)        NOT NULL,
    last_name    VARCHAR(100)        NOT NULL,
    email        VARCHAR(254) UNIQUE NOT NULL,
    base_salary  NUMERIC(15, 2)      NOT NULL,
    birth_date   DATE,
    phone_number VARCHAR(20),
    address      VARCHAR(255),
    rol_id       UUID                NOT NULL
);

CREATE TABLE roles
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

INSERT INTO roles (id, name, description)
VALUES ('6638a86e-487f-4443-8b8a-f37069704c85', 'ADMIN', 'Administrator with full permissions'),
       ('ca90e460-948f-48f3-ad09-0016c2429349', 'ADVISOR', 'Advisor with limited permissions'),
       ('f432e0fc-5aff-42b5-a370-5bfb011011e1', 'CUSTOMER', 'Customer with basic access');

ALTER TABLE users
    ADD CONSTRAINT fk_users_roles FOREIGN KEY (rol_id) REFERENCES roles (id);
