package com.splicemachine.si2.txn;

import com.splicemachine.constants.HBaseConstants;
import com.splicemachine.constants.TxnConstants;
import com.splicemachine.si.utils.SIUtils;
import com.splicemachine.utils.SpliceLogUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

public class TransactionTableCreator {
    static final Logger LOG = Logger.getLogger(TransactionTableCreator.class);

    public static void createTransactionTable() {
        try {
            @SuppressWarnings("resource")
            HBaseAdmin admin = new HBaseAdmin(new Configuration());
            if (!admin.tableExists(SIUtils.TRANSACTION_TABLE_BYTES)) {
                HTableDescriptor desc = new HTableDescriptor(SIUtils.TRANSACTION_TABLE_BYTES);
                desc.addFamily(new HColumnDescriptor(HBaseConstants.DEFAULT_FAMILY.getBytes(),
                        HBaseConstants.DEFAULT_VERSIONS,
                        admin.getConfiguration().get(HBaseConstants.TABLE_COMPRESSION, HBaseConstants.DEFAULT_COMPRESSION),
                        HBaseConstants.DEFAULT_IN_MEMORY,
                        HBaseConstants.DEFAULT_BLOCKCACHE,
                        HBaseConstants.DEFAULT_TTL,
                        HBaseConstants.DEFAULT_BLOOMFILTER));
                desc.addFamily(new HColumnDescriptor(TxnConstants.DEFAULT_FAMILY));
                admin.createTable(desc);
            }
        } catch (Exception e) {
            SpliceLogUtils.logAndThrowRuntime(LOG, e);
        }
    }
}
