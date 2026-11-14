package com.phcnforge.coworking.config;

import com.zaxxer.hikari.HikariDataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/coworking_db}")
    private String configuredUrl;

    @Value("${spring.datasource.username:postgres}")
    private String configuredUsername;

    @Value("${spring.datasource.password:postgres}")
    private String configuredPassword;

    @Value("${DATABASE_URL:}")
    private String renderDatabaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        String rawUrl = (renderDatabaseUrl != null && !renderDatabaseUrl.isBlank())
                ? renderDatabaseUrl.trim()
                : configuredUrl.trim();

        // Se já for H2 (como nos testes), retorna direto
        if (rawUrl.startsWith("jdbc:h2:")) {
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(rawUrl);
            ds.setDriverClassName("org.h2.Driver");
            ds.setUsername(configuredUsername);
            ds.setPassword(configuredPassword);
            return ds;
        }

        String jdbcUrl = rawUrl;
        String username = configuredUsername;
        String password = configuredPassword;

        // Converte URL no formato do Render/Heroku (postgres://usuario:senha@host:porta/banco)
        if (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://")) {
            URI uri = URI.create(rawUrl);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath();
            jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;

            if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")) {
                String[] credentials = uri.getUserInfo().split(":", 2);
                username = credentials[0];
                password = credentials[1];
            }
        }

        // Caso esteja apontando para localhost:5432 mas não haja Postgres rodando no container (ex: deploy isolado no Render)
        if (jdbcUrl.contains("localhost:5432") && !portaAcessivel("localhost", 5432)) {
            log.warn(
                    "PostgreSQL nao detectado em localhost:5432 e nenhuma variavel DATABASE_URL/DB_URL externa foi definida. "
                            + "Iniciando com banco em memoria compativel com PostgreSQL para evitar falha no deploy.");
            HikariDataSource fallback = new HikariDataSource();
            fallback.setJdbcUrl("jdbc:h2:mem:coworking_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
            fallback.setDriverClassName("org.h2.Driver");
            fallback.setUsername("sa");
            fallback.setPassword("");
            return fallback;
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private boolean portaAcessivel(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
