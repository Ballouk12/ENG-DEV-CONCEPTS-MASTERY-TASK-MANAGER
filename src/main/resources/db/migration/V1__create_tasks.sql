CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(100) NOT NULL
);

CREATE TABLE tasks (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(150) NOT NULL,
                       description VARCHAR(1000),
                       status VARCHAR(30) NOT NULL,
                       version BIGINT NOT NULL,
                       user_id BIGINT,
                       CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_tasks_status ON tasks(status);