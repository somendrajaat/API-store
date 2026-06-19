CREATE TABLE api_usage (
                           id BIGSERIAL PRIMARY KEY,

                           api_id BIGINT NOT NULL,
                           subscription_id BIGINT NOT NULL,

                           method VARCHAR(10) NOT NULL,
                           status_code INT NOT NULL,

                           latency_ms BIGINT NOT NULL,

                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_usage_api
                               FOREIGN KEY(api_id)
                                   REFERENCES apis(id),

                           CONSTRAINT fk_usage_subscription
                               FOREIGN KEY(subscription_id)
                                   REFERENCES subscriptions(id)
);