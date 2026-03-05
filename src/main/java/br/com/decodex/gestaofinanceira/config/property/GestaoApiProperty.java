package br.com.decodex.gestaofinanceira.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class GestaoApiProperty {

    private final Jwt jwt = new Jwt();
    private final Database database = new Database();

    public Jwt getJwt() {
        return jwt;
    }

    public Database getDatabase() {
        return database;
    }

    /* ================= JWT ================= */

    public static class Jwt {
        private String secret;
        private Long refreshExpirationDays = 7L;
        private Long accessExpirationHours = 1L;
        private int accessExpirationMs = 120000;
        private int refreshExpirationMs = 300000;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getRefreshExpirationDays() {
            return refreshExpirationDays;
        }

        public void setRefreshExpirationDays(Long refreshExpirationDays) {
            this.refreshExpirationDays = refreshExpirationDays;
        }

        public Long getAccessExpirationHours() {
            return accessExpirationHours;
        }

        public void setAccessExpirationHours(Long accessExpirationHours) {
            this.accessExpirationHours = accessExpirationHours;
        }

		public int getAccessExpirationMs() {
			return accessExpirationMs;
		}

		public void setAccessExpirationMs(int accessExpirationMs) {
			this.accessExpirationMs = accessExpirationMs;
		}

		public int getRefreshExpirationMs() {
			return refreshExpirationMs;
		}

		public void setRefreshExpirationMs(int refreshExpirationMs) {
			this.refreshExpirationMs = refreshExpirationMs;
		}
        
    }
    /* ================= DATABASE ================= */

    public static class Database {
        private String host;
        private Integer port;
        private String name;
        private String username;
        private String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
	}
}