/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package com.splicemachine.derby.stream.function;

import com.splicemachine.db.iapi.sql.execute.ExecRow;
import com.splicemachine.db.iapi.types.DataValueDescriptor;
import com.splicemachine.db.iapi.types.SQLInteger;
import com.splicemachine.db.impl.sql.execute.ValueRow;
import com.splicemachine.derby.iapi.sql.execute.SpliceOperation;
import com.splicemachine.derby.impl.sql.execute.operations.LocatedRow;
import com.splicemachine.derby.stream.iapi.OperationContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class DistinctAggregateKeyCreation<Op extends SpliceOperation> extends SpliceFunction<Op,LocatedRow, ExecRow> {

    private static final long serialVersionUID = 3988079974858059941L;
    private int[] groupByColumns;

    public DistinctAggregateKeyCreation() {
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2",justification = "Intentional")
    public DistinctAggregateKeyCreation(OperationContext<Op> operationContext, int[] groupByColumns) {
        super(operationContext);
        this.groupByColumns = groupByColumns;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(groupByColumns.length);
        for (int i = 0; i<groupByColumns.length;i++) {
            out.writeInt(groupByColumns[i]);
        }

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        groupByColumns = new int[in.readInt()];
        for (int i = 0; i<groupByColumns.length;i++) {
            groupByColumns[i] = in.readInt();
        }
    }

    @Override
     public ExecRow call(LocatedRow row) throws Exception {
        ValueRow valueRow = new ValueRow(groupByColumns.length+2);
        int position = 1;

        // copy the first N columns which are grouByColumns + distinct column position + distinct column
        // should we put the more distinct columns first instead? Is there performance difference for hashing?
        for (; position<=groupByColumns.length+1; position++) {
            valueRow.setColumn(position, row.getRow().getColumn(position));
        }
        //read the distinct column id
        DataValueDescriptor dvd = row.getRow().getColumn(position);
        valueRow.setColumn(position++, dvd);
        if (dvd == null) {
            // this is a row for non-distinct aggregates, put null value here
            valueRow.setColumn(position, new SQLInteger());
        } else {
            // this is a row corresponding to a distinct aggregate
            valueRow.setColumn(position, row.getRow().getColumn(groupByColumns.length+3));
        }
        return valueRow;
    }
    
}