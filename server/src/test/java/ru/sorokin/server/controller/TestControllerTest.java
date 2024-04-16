package ru.sorokin.server.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sorokin.server.repository.Repository;
import ru.sorokin.server.service.DataTransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class TestControllerTest {
//    int ISOLATION_LEVEL = Connection.TRANSACTION_SERIALIZABLE;
    int ISOLATION_LEVEL = Connection.TRANSACTION_READ_COMMITTED;
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
//        String commandLock = " FOR SHARE";
 //       String commandLock = " FOR NO KEY UPDATE";
 //       String commandLock = " FOR UPDATE";
        String commandLock = "";
        int id = 2;
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", id, commandLock);
        String updateVersion2ID2Rq = String.format("UPDATE test_data SET version = 'v2' WHERE id = %d", id);
        String updateVersion3ID2Rq = String.format("UPDATE test_data SET version = 'v3' WHERE id = %d", id);

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
//        String commandLock = " FOR SHARE";
//        String commandLock = " FOR NO KEY UPDATE";
//        String commandLock = " FOR UPDATE";
        String commandLock = "";
        int id = 2;
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", id, commandLock);
        String updateDataID2Rq = String.format("UPDATE test_data SET data = 'test data 2' WHERE id = %d", id);
        String updateVersionID2Rq = String.format("UPDATE test_data SET version = 'v2' WHERE id = %d", id);

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
//        String commandLock = " FOR SHARE";
//        String commandLock = " FOR NO KEY UPDATE";
//        String commandLock = " FOR UPDATE";
        String commandLock = "";
        int id = 2;
        String selectID2WithLockRq = String.format("SELECT * FROM test_data WHERE id = %d%s", id, commandLock);
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d", id);
        String updateVersion2ID2Rq = String.format("UPDATE test_data SET version = 'v2' WHERE id = %d", id);

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                null,
                selectID2WithLockRq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion2ID2Rq, selectID2Rq),
                2000,
                disableUpdateFirstTransaction);
    }

    @Test
    void testSecondTransactionUpdateBeforeUpdateFirstTransaction() {
        byte startUpdateFirstTransactionAfter = 2;
//        String commandLock = " FOR SHARE";
//        String commandLock = " FOR NO KEY UPDATE";
//        String commandLock = " FOR UPDATE";
        String commandLock = "";
        int id = 2;
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", id, commandLock);
        String updateVersion2ID2Rq = String.format("UPDATE test_data SET version = 'v2' WHERE id = %d", id);

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateVersion2ID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion2ID2Rq, selectID2Rq),
                2000,
                startUpdateFirstTransactionAfter);
    }


    @Test
    void testTransactionUpdateWithOtherDataIDButSecondBefore() {
        byte startUpdateFirstTransactionBefore = 1;
//        String commandLock = " FOR SHARE";
//        String commandLock = " FOR NO KEY UPDATE";
//        String commandLock = " FOR UPDATE";
        String commandLock = "";
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", 2, commandLock);
        String selectID3Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", 4, commandLock);
        String updateVersion2ID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";
        String updateVersion3ID2Rq = "UPDATE test_data SET version = 'v3' WHERE id = 4";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateVersion2ID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion3ID2Rq, selectID3Rq),
                0,
                startUpdateFirstTransactionBefore);
    }

    @Test
    void testTransactionUpdateWithOtherDataIDButFirstBefore() {
        //отличичается при TRANSACTION_REPEATABLE_READ

        byte startUpdateFirstTransactionAfter = 2;
//        String commandLock = " FOR SHARE";
//        String commandLock = " FOR NO KEY UPDATE";
//        String commandLock = " FOR UPDATE";
        String commandLock = "";
        String selectID2Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", 2, commandLock);
        String selectID3Rq = String.format("SELECT * FROM test_data WHERE id = %d%s", 4, commandLock);
        String updateVersion2ID2Rq = "UPDATE test_data SET version = 'v2' WHERE id = 2";
        String updateVersion3ID2Rq = "UPDATE test_data SET version = 'v3' WHERE id = 4";

        dataTransactionService.firstTransactionTypeUpdate(
                ISOLATION_LEVEL,
                updateVersion2ID2Rq,
                selectID2Rq,
                dataTransactionService.otherThreadTypeUpdate(ISOLATION_LEVEL, updateVersion3ID2Rq, selectID3Rq),
                2000,
                startUpdateFirstTransactionAfter);
    }

    @Test
    void testInsertSecondTransaction() {
        String commandLock = "";
        byte disableUpdateFirstTransaction = 0;
        String selectCountRq = String.format("SELECT count(*) FROM test_data %s", commandLock);
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