package ru.sorokin.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sorokin.server.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class TestControllerTest {

    @BeforeEach
    void addData() {
        try (
                final Connection conn = Repository.getConnection();
                final Statement connStatement = conn.createStatement()
        ) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            connStatement.executeUpdate("UPDATE test_data SET version = 'v1' WHERE id = 2");
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testReadCommitUpdateSecondTransaction() {
        int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(ISOLATION_LEVEL);

            final ResultSet resultSetOne = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                System.out.println("[firstT1 SELECT] version: " + version);
            }

            Thread otherTransaction = new Thread(() -> {
                try (
                        final Connection conn = Repository.getConnection();
                        final Statement connStatement = conn.createStatement()
                ) {
                    conn.setAutoCommit(false);
                    conn.setTransactionIsolation(ISOLATION_LEVEL);

                    connStatement.executeUpdate("UPDATE test_data SET version = 'v2' WHERE id = 2");
                    final ResultSet resultSetTwo = connStatement.executeQuery("SELECT * FROM test_data WHERE id = 2");
                    while (resultSetTwo.next()) {
                        final String version = resultSetTwo.getString("version");
                        System.out.println("[secondT1 UPDATE] version: " + version);
                    }
                    conn.commit();
                    System.out.println("[secondT commit]");

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            });
            otherTransaction.start();
            Thread.sleep(2000);

            final ResultSet resultSetOne1 = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                System.out.println("[firstT2 SELECT] version: " + version);
            }
            connection.commit();
            System.out.println("[firstT commit]");


        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testReadCommitUpdateFirstTransaction() {
        int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(ISOLATION_LEVEL);

            final ResultSet resultSetOne = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                System.out.println("[firstT1 SELECT] version: " + version);
            }
            statement.executeUpdate("UPDATE test_data SET version = 'v2' WHERE id = 2");
            final ResultSet resultSetOne1 = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                System.out.println("[firstT2 UPDATE] version: " + version);
            }

            Thread otherTransaction = new Thread(() -> {
                try (
                        final Connection conn = Repository.getConnection();
                        final Statement connStatement = conn.createStatement()
                ) {
                    conn.setAutoCommit(false);
                    conn.setTransactionIsolation(ISOLATION_LEVEL);
                    final ResultSet resultSetTwo = connStatement.executeQuery("SELECT * FROM test_data WHERE id = 2");
                    while (resultSetTwo.next()) {
                        final String version = resultSetTwo.getString("version");
                        System.out.println("[secondT1 SELECT] version: " + version);
                    }

                    connStatement.executeUpdate("UPDATE test_data SET version = 'v3' WHERE id = 2");
                    final ResultSet resultSetTwo1 = connStatement.executeQuery("SELECT * FROM test_data WHERE id = 2");
                    while (resultSetTwo1.next()) {
                        final String version = resultSetTwo1.getString("version");
                        System.out.println("[secondT2 UPDATE] version: " + version);
                    }
                    conn.commit();
                    System.out.println("[secondT commit]");

                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            });
            otherTransaction.start();
            Thread.sleep(2000);

            final ResultSet resultSetTwo = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetTwo.next()) {
                final String version = resultSetTwo.getString("version");
                System.out.println("[firstT3 SELECT] version: " + version);
            }
            var start = System.currentTimeMillis();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            var stop = System.currentTimeMillis();
            System.out.println("Pause = " + (stop - start));
            connection.commit();
            System.out.println("[firstT commit]");

        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}