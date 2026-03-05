CREATE TABLE refresh_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL,
    data_expiracao DATETIME NOT NULL,
    usuario_id BIGINT NOT NULL,
    dispositivo VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_token_token (token),
    CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_refresh_token_token ON refresh_token (token);
