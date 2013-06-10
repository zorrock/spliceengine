package com.splicemachine.si.txn;

import com.splicemachine.si.LStoreSetup;
import com.splicemachine.si.StoreSetup;
import com.splicemachine.si.TransactorSetup;
import com.splicemachine.si.api.Transactor;
import com.splicemachine.si.impl.Transaction;
import com.splicemachine.si.impl.TransactionId;
import com.splicemachine.si.impl.TransactionStatus;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TransactionManagerTest {
    protected static Transactor transactor;

    static StoreSetup storeSetup;
    static TransactorSetup transactorSetup;

    static void baseSetUp() {
        transactor = transactorSetup.transactor;
    }

    @BeforeClass
    public static void setUp() {
        storeSetup = new LStoreSetup();
        transactorSetup = new TransactorSetup(storeSetup, true);
        baseSetUp();
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }

    @Test
    public void beginTransactionTest() throws Exception {
        TransactionId transactionId = transactor.beginTransaction();
        Assert.assertNotNull(transactionId);
        Transaction transaction = transactorSetup.transactionStore.getTransaction(transactionId);
        Assert.assertTrue(transaction.getBeginTimestampDirect() >= 0);
        Assert.assertTrue(transaction.isEffectivelyActive());
    }

    @Test
    public void doCommitTest() throws Exception {
        TransactionId transactionId = transactor.beginTransaction();
        transactor.commit(transactionId);
        Transaction transaction = transactorSetup.transactionStore.getTransaction(transactionId);
        Assert.assertTrue(transaction.getBeginTimestampDirect() >= 0);
        Assert.assertTrue(transaction.isCommitted());
        Assert.assertTrue(transaction.getBeginTimestampDirect() < transaction.getCommitTimestamp());
    }

    @Test
    public void rollbackTest() throws Exception {
        TransactionId transactionId = transactor.beginTransaction();
        transactor.rollback(transactionId);
        Transaction transaction = transactorSetup.transactionStore.getTransaction(transactionId);
        Assert.assertTrue(transaction.getBeginTimestampDirect() >= 0);
        Assert.assertTrue(transaction.getStatus().equals(TransactionStatus.ROLLED_BACK));
        Assert.assertNull(transaction.getCommitTimestamp());
    }

}
