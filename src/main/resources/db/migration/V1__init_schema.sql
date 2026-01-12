CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE apis (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      base_url TEXT NOT NULL,
                      owner_id BIGINT NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT now(),
                      CONSTRAINT fk_api_owner
                          FOREIGN KEY (owner_id) REFERENCES users(id)
);
