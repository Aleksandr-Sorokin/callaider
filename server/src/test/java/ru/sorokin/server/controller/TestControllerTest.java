package ru.sorokin.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sorokin.server.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class TestControllerTest {
    int ISOLATION_LEVEL = Connection.TRANSACTION_SERIALIZABLE;
//    int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
//    int ISOLATION_LEVEL = Connection.TRANSACTION_REPEATABLE_READ;

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
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
    }

    @Test
    void testUpdateFirstBeforeSecondTransaction() {
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            System.out.println("testUpdateFirstBeforeSecondTransaction");
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(ISOLATION_LEVEL);

            final ResultSet resultSetOne = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                System.out.println("[firstTransaction SELECT] version: " + version);
            }
            statement.executeUpdate("UPDATE test_data SET version = 'v2' WHERE id = 2");
            final ResultSet resultSetOne1 = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                System.out.println("[firstTransaction UPDATE] version: " + version);
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
                        System.out.println("[secondTransaction SELECT] version: " + version);
                    }

                    connStatement.executeUpdate("UPDATE test_data SET version = 'v3' WHERE id = 2");
                    final ResultSet resultSetTwo1 = connStatement.executeQuery("SELECT * FROM test_data WHERE id = 2");
                    while (resultSetTwo1.next()) {
                        final String version = resultSetTwo1.getString("version");
                        System.out.println("[secondTransaction UPDATE] version: " + version);
                    }
                    conn.commit();
                    System.out.println("[secondTransaction commit]");

                } catch (SQLException e) {
                    System.out.println("[secondTransaction]" + e.getClass().getName() + " " + e.getMessage());
                }
            });
            otherTransaction.start();
            Thread.sleep(2000);

            final ResultSet resultSetTwo = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetTwo.next()) {
                final String version = resultSetTwo.getString("version");
                System.out.println("[firstTransaction SELECT] version: " + version);
            }
            var start = System.currentTimeMillis();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            var stop = System.currentTimeMillis();
            System.out.println("Pause before firstTransaction commit = " + (stop - start));
            connection.commit();
            System.out.println("[firstTransaction commit]");

        } catch (SQLException | InterruptedException e) {
            System.out.println("[firstTransaction]" + e.getClass().getName() + " " + e.getMessage());
        }
    }

    @Test
    void testOnlyUpdateSecondTransaction() {
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            System.out.println("OnlyUpdateSecondTransaction");
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(ISOLATION_LEVEL);

            final ResultSet resultSetOne = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                System.out.println("[firstTransaction SELECT] version: " + version);
            }

            Thread secondTransaction = new Thread(() -> {
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
                        System.out.println("[secondTransaction UPDATE] version: " + version);
                    }
                    conn.commit();
                    System.out.println("[secondTransaction commit]");

                } catch (SQLException e) {
                    System.out.println("[secondTransaction]" + e.getClass().getName() + " " + e.getMessage());
                }
            });
            secondTransaction.start();
            Thread.sleep(2000);

            final ResultSet resultSetOne1 = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                System.out.println("[firstTransaction SELECT] version: " + version);
            }
            connection.commit();
            System.out.println("[firstTransaction commit]");


        } catch (SQLException | InterruptedException e) {
            System.out.println("[firstTransaction]" + e.getClass().getName() + " " + e.getMessage());
        }
    }

    @Test
    void testSecondTransactionUpdateBeforeUpdateFirstTransaction() {
        System.out.println("testSecondTransactionUpdateBeforeUpdateFirstTransaction");
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(ISOLATION_LEVEL);

            final ResultSet resultSetOne = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                System.out.println("[firstTransaction SELECT] version: " + version);
            }

            Thread secondTransaction = new Thread(() -> {
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
                        System.out.println("[secondTransaction UPDATE] version: " + version);
                    }
                    var start = System.currentTimeMillis();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    var stop = System.currentTimeMillis();
                    System.out.println("Pause before secondTransaction commit = " + (stop - start));
                    conn.commit();
                    System.out.println("[secondTransaction commit]");

                } catch (SQLException e) {
                    System.out.println("[secondTransaction]" + e.getClass().getName() + " " + e.getMessage());
                }
            });
            secondTransaction.start();
            Thread.sleep(2000);
            System.out.println("Wait commit second Transaction");
            statement.executeUpdate("UPDATE test_data SET version = 'v3' WHERE id = 2");
            final ResultSet resultSetOne1 = statement.executeQuery("SELECT * FROM test_data WHERE id = 2");
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                System.out.println("[firstTransaction UPDATE] version: " + version);
            }
            connection.commit();
            System.out.println("[firstTransaction commit]");

        } catch (SQLException | InterruptedException e) {
            System.out.println("[firstTransaction]" + e.getClass().getName() + " " + e.getMessage());
        }
    }
}