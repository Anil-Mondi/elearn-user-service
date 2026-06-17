DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100),

    email VARCHAR(100) UNIQUE,

    contact_number VARCHAR(20),

    password VARCHAR(255),

    role VARCHAR(20),

    status VARCHAR(20),

    reset_token VARCHAR(255)
);