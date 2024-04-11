package ru.sorokin.server.repository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
@NoArgsConstructor
@Component
public class Repository {

    //@Value(value = "${spring.datasource.url}")
    static String url = "jdbc:postgresql://localhost:6541/postgres";
    //@Value(value = "${spring.datasource.username}")
    static String user = "postgres";
    //@Value(value = "${spring.datasource.password}")
    static String password = "postgres";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}