package ru.sorokin.server.service;

import org.springframework.stereotype.Service;
import ru.sorokin.server.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class DataTransactionService {

    public Thread otherThreadTypeUpdate(Integer isolationLevel, String updateRq, String selectRq) {
        return new Thread(() -> {
            try (
                    final Connection connection = Repository.getConnection();
                    final Statement statement = connection.createStatement()
            ) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(isolationLevel);

                select("OTHER TRANSACTION SELECT", statement, selectRq);
                if (updateRq != null) {
                    System.out.println("Start UPDATE OTHER TRANSACTION");
                    update("OTHER TRANSACTION UPDATE", statement, updateRq, selectRq);
                }
                connection.commit();
                System.out.println("[OTHER TRANSACTION commit]");
            } catch (SQLException e) {
                System.out.println("[OTHER TRANSACTION]" + e.getClass().getName() + " " + e.getMessage());
            }
        });
    }

    public Thread otherThreadTypeInsert(Integer isolationLevel, String updateRq, String selectRq) {
        return new Thread(() -> {
            try (
                    final Connection connection = Repository.getConnection();
                    final Statement statement = connection.createStatement()
            ) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(isolationLevel);

                selectCount("OTHER TRANSACTION SELECT", statement, selectRq);
                if (updateRq != null) {
                    System.out.println("Start INSERT OTHER TRANSACTION");
                    insert("OTHER TRANSACTION INSERT", statement, updateRq, selectRq);
                }
                connection.commit();
                System.out.println("[OTHER TRANSACTION commit]");
            } catch (SQLException e) {
                System.out.println("[OTHER TRANSACTION]" + e.getClass().getName() + " " + e.getMessage());
            }
        });
    }

    public void firstTransactionTypeUpdate(Integer isolationLevel,
                                           String updateRq,
                                           String selectRq,
                                           Thread otherTransactional,
                                           int sleep,
                                           byte order) {
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            System.out.println(printIsolationLevel(isolationLevel));

            select("FIRST TRANSACTION SELECT", statement, selectRq);
            if (updateRq != null && order == 1) {
                update("FIRST TRANSACTION UPDATE", statement, updateRq, selectRq);
            }
            otherTransactional.start();
            Thread.sleep(sleep);

            if (updateRq != null && order == 2) {
                System.out.println("Wait commit OTHER TRANSACTION");
                update("FIRST TRANSACTION UPDATE", statement, updateRq, selectRq);
            } else {
                select("FIRST TRANSACTION SELECT", statement, selectRq);
            }
            connection.commit();
            System.out.println("[FIRST TRANSACTION commit]");

        } catch (SQLException | InterruptedException e) {
            System.out.println("[FIRST TRANSACTION]" + e.getClass().getName() + " " + e.getMessage());
        }
    }

    public void firstTransactionTypeInsert(Integer isolationLevel,
                                           String updateRq,
                                           String selectRq,
                                           Thread otherTransactional,
                                           int sleep,
                                           byte order) {
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);
            System.out.println(printIsolationLevel(isolationLevel));

            selectCount("FIRST TRANSACTION SELECT", statement, selectRq);
            if (updateRq != null && order == 1) {
                insert("FIRST TRANSACTION INSERT", statement, updateRq, selectRq);
            }
            otherTransactional.start();
            Thread.sleep(sleep);

            if (updateRq != null && order == 2) {
                System.out.println("Wait commit OTHER TRANSACTION");
                insert("FIRST TRANSACTION INSERT", statement, updateRq, selectRq);
            } else {
                selectCount("FIRST TRANSACTION SELECT", statement, selectRq);
            }
            connection.commit();
            System.out.println("[FIRST TRANSACTION commit]");

        } catch (SQLException | InterruptedException e) {
            System.out.println("[FIRST TRANSACTION]" + e.getClass().getName() + " " + e.getMessage());
        }
    }

    private void update(String nameTransactional, Statement statement, String sqlUpdate, String sqlSelect) throws SQLException {
        statement.executeUpdate(sqlUpdate);
        select(nameTransactional, statement, sqlSelect);
    }

    private void insert(String nameTransactional, Statement statement, String sqlUpdate, String sqlSelect) throws SQLException {
        statement.executeUpdate(sqlUpdate);
        selectCount(nameTransactional, statement, sqlSelect);
    }

    private void select(String nameTransactional, Statement statement, String sqlSelect) throws SQLException {
        final ResultSet resultSet = statement.executeQuery(sqlSelect);
        while (resultSet.next()) {
            final String version = resultSet.getString("version");
            final String data = resultSet.getString("data");
            System.out.printf("[%s] data: (%s), version: (%s)%n", nameTransactional, data, version);
        }
    }

    private void selectCount(String nameTransactional, Statement statement, String sqlSelect) throws SQLException {
        final ResultSet resultSetOne = statement.executeQuery(sqlSelect);
        while (resultSetOne.next()) {
            final int count = resultSetOne.getInt(1);
            System.out.printf("[%s] Count: (%s)%n", nameTransactional, count);
        }
    }

    public String printIsolationLevel(Integer isolation) {
        switch (isolation) {
            case Connection.TRANSACTION_READ_COMMITTED:
                return "ISOLATION LEVEL TRANSACTION_READ_COMMITTED";
            case Connection.TRANSACTION_SERIALIZABLE:
                return "ISOLATION LEVEL TRANSACTION_SERIALIZABLE";
            case Connection.TRANSACTION_REPEATABLE_READ:
                return "ISOLATION LEVEL TRANSACTION_REPEATABLE_READ";
            default:
                return "ISOLATION LEVEL UNKNOWN";
        }
    }
}
