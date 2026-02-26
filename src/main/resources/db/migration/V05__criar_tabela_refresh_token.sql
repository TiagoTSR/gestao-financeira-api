CREATE TABLE refresh_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    usuario_id BIGINT NOT NULL,
    
    PRIMARY KEY (id),
    UNIQUE (token),
    CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE INDEX idx_refresh_token_valor ON refresh_token (token);