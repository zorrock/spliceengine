package com.splicemachine.derby.impl.sql.execute.actions;

import org.apache.derby.iapi.services.io.ArrayUtil;
import org.apache.derby.iapi.services.io.StoredFormatIds;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.impl.sql.execute.FKInfo;
import org.apache.derby.impl.sql.execute.TriggerInfo;

import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;
import java.util.Properties;

/**
 *	This class  describes compiled constants that are passed into
 *	DeleteResultSets.
 *
 */

public class DeleteConstantOperation extends WriteCursorConstantOperation
{
	/********************************************************
	**
	**	This class implements Formatable. But it is NOT used
 	**	across either major or minor releases.  It is only
	** 	written persistently in stored prepared statements, 
	**	not in the replication stage.  SO, IT IS OK TO CHANGE
	**	ITS read/writeExternal.
	**
	********************************************************/
	
	int numColumns;
	ConstantAction[] dependentCActions; //constant action for the dependent table
	ResultDescription resultDescription; //required for dependent tables.

	// CONSTRUCTORS

	/**
	 * Public niladic constructor. Needed for Formatable interface to work.
	 *
	 */
    public	DeleteConstantOperation() { super(); }

	/**
	 *	Make the ConstantAction for an DELETE statement.
	 *
	 *  @param conglomId	Conglomerate ID.
	 *	@param heapSCOCI	StaticCompiledOpenConglomInfo for heap.
	 *  @param irgs			Index descriptors
	 *  @param indexCIDS	Conglomerate IDs of indices
	 *	@param indexSCOCIs	StaticCompiledOpenConglomInfos for indexes.
	 *  @param emptyHeapRow	Template for heap row.
	 *  @param deferred		True means process as a deferred insert.
	 *	@param targetUUID	UUID of target table
	 *	@param lockMode		The lock mode to use
	 *							(row or table, see TransactionController)
	 *	@param fkInfo		Array of structures containing foreign key info, if any (may be null)
	 *	@param triggerInfo	Array of structures containing trigger info, if any (may be null)
	 *  @param baseRowReadList Map of columns read in.  1 based.
     *  @param streamStorableHeapColIds Null for non rep. (0 based)
	 *  @param numColumns	Number of columns to read.
	 *  @param singleRowSource		Whether or not source is a single row source
	 */
	public	DeleteConstantOperation(
								long				conglomId,
								StaticCompiledOpenConglomInfo heapSCOCI,
                                int[] pkColumns,
								IndexRowGenerator[]	irgs,
								long[]				indexCIDS,
								StaticCompiledOpenConglomInfo[] indexSCOCIs,
								ExecRow				emptyHeapRow,
								boolean				deferred,
								UUID				targetUUID,
								int					lockMode,
								FKInfo[]			fkInfo,
								TriggerInfo			triggerInfo,
								FormatableBitSet				baseRowReadList,
								int[]				baseRowReadMap,
								int[]               streamStorableHeapColIds,
								int					numColumns,
								boolean				singleRowSource,
								ResultDescription   resultDescription,
								ConstantAction[] dependentCActions)
	{
		super( conglomId, 
			   heapSCOCI,
                pkColumns,
			   irgs, indexCIDS, indexSCOCIs,
			   null, // index names not needed for delete.
			   deferred, 
			   (Properties) null,
			   targetUUID,
			   lockMode,
			   fkInfo,
			   triggerInfo,
			   emptyHeapRow,
			   baseRowReadList,
			   baseRowReadMap,
			   streamStorableHeapColIds,
			   singleRowSource
			   );

		this.numColumns = numColumns;
		this.resultDescription = resultDescription;
		this.dependentCActions =  dependentCActions;
	}

	// INTERFACE METHODS

	// Formatable methods

	/**
	  @see java.io.Externalizable#readExternal
	  @exception IOException thrown on error
	  @exception ClassNotFoundException	thrown on error
	  */
	public void readExternal( ObjectInput in )
		 throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		numColumns = in.readInt();

		//information required for cascade delete
		dependentCActions = new ConstantAction[ArrayUtil.readArrayLength(in)];
		ArrayUtil.readArrayItems(in, dependentCActions);
		resultDescription = (ResultDescription) in.readObject();

	}

	/**

	  @exception IOException thrown on error
	  */
	public void writeExternal( ObjectOutput out )
		 throws IOException
	{
		super.writeExternal(out);
		out.writeInt(numColumns);

		//write cascade delete information
		ArrayUtil.writeArray(out, dependentCActions);
		out.writeObject(resultDescription);

	}

	/**
	 * Get the formatID which corresponds to this class.
	 *
	 *	@return	the formatID of this class
	 */
	public	int	getTypeFormatId()	{ return StoredFormatIds.DELETE_CONSTANT_ACTION_V01_ID; }

	// KeyToBaseRowConstantAction METHODS
}
