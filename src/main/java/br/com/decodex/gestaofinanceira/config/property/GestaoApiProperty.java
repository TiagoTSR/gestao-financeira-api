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
        private Long accessExpirationMinutes = 30L;
        private Long accessExpirationHours = 6L;
  
        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public Long getAccessExpirationMinutes() {
            return accessExpirationMinutes;
        }

        public void setAccessExpirationMinutes(Long accessExpirationMinutes) {
            this.accessExpirationMinutes = accessExpirationMinutes;
        }

        public Long getAccessExpirationHours() {
            return accessExpirationHours;
        }

        public void setAccessExpirationHours(Long accessExpirationHours) {
            this.accessExpirationHours = accessExpirationHours;
        }
        
        public long getAccessExpirationMillis() {
            return accessExpirationMinutes * 60 * 1000L;
        }

        public long getRefreshExpirationMillis() {
            return accessExpirationHours * 24 * 60 * 60 * 1000L;
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