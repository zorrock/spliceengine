package com.splicemachine.hbase.txn;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.zookeeper.RecoverableZooKeeper;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import com.splicemachine.constants.TxnConstants;
import com.splicemachine.hbase.txn.coprocessor.region.TxnUtils;
import com.splicemachine.utils.SpliceLogUtils;

public class ZkTransactionManager extends TransactionManager {
    static final Logger LOG = Logger.getLogger(ZkTransactionManager.class);
    protected String transactionPath;
    protected JtaXAResource xAResource;
  
    public ZkTransactionManager(final Configuration conf) throws IOException {
    	super(conf);
    	this.transactionPath = conf.get(TxnConstants.TRANSACTION_PATH_NAME,TxnConstants.DEFAULT_TRANSACTION_PATH);
    }

    public ZkTransactionManager(final String transactionPath, final Configuration conf) throws IOException {
    	super(conf);
    	this.transactionPath = transactionPath;
    }
    
    public ZkTransactionManager(final Configuration conf, ZooKeeperWatcher zkw, RecoverableZooKeeper rzk) throws IOException {
    	this.transactionPath = conf.get(TxnConstants.TRANSACTION_PATH_NAME,TxnConstants.DEFAULT_TRANSACTION_PATH);
    	this.zkw = zkw;
    	this.rzk = rzk;
    }

    public ZkTransactionManager(final String transactionPath, ZooKeeperWatcher zkw, RecoverableZooKeeper rzk) throws IOException {
    	this.transactionPath = transactionPath;
    	this.zkw = zkw;
    	this.rzk = rzk;
    }
    
    public TransactionState beginTransaction() throws KeeperException, InterruptedException, IOException, ExecutionException {
    	SpliceLogUtils.trace(LOG, "Begin transaction");
    	return new TransactionState(TxnUtils.beginTransaction(transactionPath, zkw));
    }
   
    public int prepareCommit(final TransactionState transactionState) throws KeeperException, InterruptedException, IOException {
    	if (LOG.isDebugEnabled()) 
    		LOG.debug("Do prepareCommit on " + transactionState.getTransactionID());
    	TxnUtils.prepareCommit(transactionState.getTransactionID(), rzk);
    	return 0;
     }

    public void doCommit(final TransactionState transactionState) throws KeeperException, InterruptedException, IOException  {
    	if (LOG.isDebugEnabled()) 
    		LOG.debug("Do commit on " + transactionState.getTransactionID());
    	TxnUtils.doCommit(transactionState.getTransactionID(), rzk);
    }

    public void tryCommit(final TransactionState transactionState) throws IOException, KeeperException, InterruptedException {
    	if (LOG.isDebugEnabled()) 
    		LOG.debug("Try commit on " +transactionState.getTransactionID());
       	prepareCommit(transactionState);
       	doCommit(transactionState);
    }
    
    public void abort(final TransactionState transactionState) throws IOException, KeeperException, InterruptedException {
    	if (LOG.isDebugEnabled()) 
    		LOG.debug("Abort on " +transactionState.getTransactionID());
    	TxnUtils.abort(transactionState.getTransactionID(), rzk);
     }

    public synchronized JtaXAResource getXAResource() {
        if (xAResource == null) {
            xAResource = new JtaXAResource(this);
        }
        return xAResource;
    }
}
