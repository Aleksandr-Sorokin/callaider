package ru.sorokin.server.service;

import org.springframework.stereotype.Service;
import ru.sorokin.server.repository.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
@Service
public class DataTransactionService {

    public Thread otherThread(Integer isolationLevel, String updateRq, String selectRq) {
        return new Thread(() -> {
            try (
                    final Connection conn = Repository.getConnection();
                    final Statement connStatement = conn.createStatement()
            ) {
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(isolationLevel);
                final ResultSet resultSetTwo = connStatement.executeQuery(selectRq);
                while (resultSetTwo.next()) {
                    final String version = resultSetTwo.getString("version");
                    final String data = resultSetTwo.getString("data");
                    System.out.println("[otherTransaction SELECT] data: " + data + ", version: " + version);
                }
                if (updateRq != null) {
                    System.out.println("Start UPDATE OTHER Transaction");
                    connStatement.executeUpdate(updateRq);
                    final ResultSet resultSetTwo1 = connStatement.executeQuery(selectRq);
                    while (resultSetTwo1.next()) {
                        final String version = resultSetTwo1.getString("version");
                        final String data = resultSetTwo1.getString("data");
                        System.out.println("[secondTransaction UPDATE] data: " + data + ", version: " + version);
                    }
                }
                conn.commit();
                System.out.println("[otherTransaction commit]");
            } catch (SQLException e) {
                System.out.println("[otherTransaction]" + e.getClass().getName() + " " + e.getMessage());
            }
        });
    }

    public void firstTransaction(Integer isolationLevel,
                                 String updateRq,
                                 String selectRq,
                                 Thread otherTransactional,
                                 int sleep) {
        try (
                final Connection connection = Repository.getConnection();
                final Statement statement = connection.createStatement()
        ) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(isolationLevel);

            final ResultSet resultSetOne = statement.executeQuery(selectRq);
            while (resultSetOne.next()) {
                final String version = resultSetOne.getString("version");
                final String data = resultSetOne.getString("data");
                System.out.println("[firstTransaction SELECT] data: " + data + ", version: " + version);
            }
            statement.executeUpdate(updateRq);
            final ResultSet resultSetOne1 = statement.executeQuery(selectRq);
            while (resultSetOne1.next()) {
                final String version = resultSetOne1.getString("version");
                final String data = resultSetOne1.getString("data");
                System.out.println("[firstTransaction UPDATE] data: " + data + ", version: " + version);
            }

            Thread otherTransaction = otherTransactional;
            otherTransaction.start();
            Thread.sleep(sleep);

            final ResultSet resultSetTwo = statement.executeQuery(selectRq);
            while (resultSetTwo.next()) {
                final String version = resultSetTwo.getString("version");
                final String data = resultSetTwo.getString("data");
                System.out.println("[firstTransaction SELECT] data: " + data + ", version: " + version);
            }
            var start = System.currentTimeMillis();

            var stop = System.currentTimeMillis();
            System.out.println("Pause before firstTransaction commit = " + (stop - start));
            connection.commit();
            System.out.println("[firstTransaction commit]");

        } catch (SQLException | InterruptedException e) {
            System.out.println("[firstTransaction]" + e.getClass().getName() + " " + e.getMessage());
        }
    }
}
