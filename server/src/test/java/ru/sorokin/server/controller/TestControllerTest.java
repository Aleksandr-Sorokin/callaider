package ru.sorokin.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sorokin.server.repository.Repository;
import ru.sorokin.server.service.DataTransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class TestControllerTest {
    int ISOLATION_LEVEL = Connection.TRANSACTION_SERIALIZABLE;
//    int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
//    int ISOLATION_LEVEL = Connection.TRANSACTION_REPEATABLE_READ;

    private DataTransactionService dataTransactionService;

    @BeforeEach
    void addData() {
        dataTransactionService = new DataTransactionService();
        try (
                final Connection conn = Repository.getConnection();
                final Statement connStatement = conn.createStatement()
        ) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            connStatement.executeUpdate("UPDATE test_data SET data = 'test data 1', version = 'v1'");
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getClass().getName() + " " + e.getMessage());
        }
    }

    @Test
    void testUpdateFirstBeforeSecondTransaction() {
        byte startUpdateFirstTransaction = 1;
        String selectID2Rq = "SELECT * FROM test_data WHERE id = 2";
        String updateVersion2ID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";
        String updateVersion3ID2Rq = "UPDATE test_data SET version = 'v3' WHERE id = 2";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateVersion2ID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion3ID2Rq, selectID2Rq),
                2000,
                startUpdateFirstTransaction);

    }

    @Test
    void testUpdateFirstBeforeSecondTransactionOtherField() {
        byte startUpdateFirstTransaction = 1;
        String selectID2Rq = "SELECT * FROM test_data WHERE id = 2";
        String updateDataID2Rq = "UPDATE test_data SET data = 'test data 2' WHERE id = 2";
        String updateVersionID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateDataID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersionID2Rq, selectID2Rq),
                2000,
                startUpdateFirstTransaction);
    }

    @Test
    void testOnlyUpdateSecondTransaction() {
        byte disableUpdateFirstTransaction = 0;
        String selectID2Rq = "SELECT * FROM test_data WHERE id = 2";
        String updateVersion2ID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                null,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion2ID2Rq, selectID2Rq),
                2000,
                disableUpdateFirstTransaction);
    }

    @Test
    void testSecondTransactionUpdateBeforeUpdateFirstTransaction() {
        byte startUpdateFirstTransactionAfter = 2;
        String selectID2Rq = "SELECT * FROM test_data WHERE id = 2";
        String updateVersion2ID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";
        String updateVersion3ID2Rq = "UPDATE test_data SET version = 'v3' WHERE id = 2";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateVersion3ID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion2ID2Rq, selectID2Rq),
                2000,
                startUpdateFirstTransactionAfter);
    }

    @Test
    void testInsertSecondTransaction() {
        byte disableUpdateFirstTransaction = 0;
        String selectCountRq = "SELECT count(*) FROM test_data";
        String insertRq = "INSERT INTO test_data(data, version) values ('test data 2', 'v2')";

        dataTransactionService.firstTransactionTypeInsert(
                ISOLATION_LEVEL,
                null,
                selectCountRq,
                dataTransactionService.otherThreadTypeInsert(ISOLATION_LEVEL, insertRq, selectCountRq),
                2000,
                disableUpdateFirstTransaction);
    }
}