CREATE TABLE subscriptions (
                               id BIGSERIAL PRIMARY KEY,

                               user_id BIGINT NOT NULL,
                               api_id BIGINT NOT NULL,

                               api_key_hash VARCHAR(255) NOT NULL,

                               created_at TIMESTAMP NOT NULL DEFAULT now(),

                               CONSTRAINT fk_subscription_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id),

                               CONSTRAINT fk_subscription_api
                                   FOREIGN KEY (api_id)
                                       REFERENCES apis(id),

                               CONSTRAINT uq_subscription_user_api
                                   UNIQUE (user_id, api_id)
);