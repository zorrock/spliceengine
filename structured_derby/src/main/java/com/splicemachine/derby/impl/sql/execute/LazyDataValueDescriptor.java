package com.splicemachine.derby.impl.sql.execute;

import com.splicemachine.derby.impl.sql.execute.serial.SerializerThunk;
import com.splicemachine.utils.SpliceLogUtils;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.types.BooleanDataValue;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.sql.*;
import java.util.Calendar;

public class LazyDataValueDescriptor implements DataValueDescriptor {

    private static Logger LOG = Logger.getLogger(LazyDataValueDescriptor.class);

    private DataValueDescriptor dvd;

    protected byte[] dvdBytes;
    protected SerializerThunk serializerThunk;
    protected boolean isSerialized;
    protected boolean isDeserialized;

    public LazyDataValueDescriptor(){

    }

    public LazyDataValueDescriptor(DataValueDescriptor dvd, SerializerThunk serializerThunk){
        this.setDvd(dvd);
        this.serializerThunk = serializerThunk;
    }

    public void initForDeserialization(byte[] bytes){
        this.dvdBytes = bytes;
        isDeserialized = false;
        getDvd().setToNull();
    }

    protected void forceDeserialization(){
        if(!isDeserialized && getDvd() != null && dvdBytes != null){
            try{
                serializerThunk.deserialize(dvdBytes, getDvd());
                isDeserialized = true;
            }catch(Exception e){
                SpliceLogUtils.logAndThrowRuntime(LOG, "Error lazily deserializing bytes",e);
            }
        }
    }

    protected void forceSerialization(){
        if(!isSerialized && dvdBytes == null){
            try{
                dvdBytes = serializerThunk.serialize(getDvd());
                isSerialized = true;
            }catch(Exception e){
                SpliceLogUtils.logAndThrowRuntime(LOG, "Error serializing DataValueDescriptor to bytes", e);
            }
        }
    }

    protected void resetForSerialization(){
        dvdBytes = null;
        isSerialized = false;
    }

    protected void resetForDeserialization(){
        isDeserialized = false;
        getDvd().setToNull();
    }

    @Override
    public int getLength() throws StandardException {
        forceDeserialization();
        return getDvd().getLength();
    }

    @Override
    public String getString() throws StandardException {
        forceDeserialization();
        return getDvd().getString();
    }

    @Override
    public String getTraceString() throws StandardException {
        forceDeserialization();
        return getDvd().getTraceString();
    }

    @Override
    public boolean getBoolean() throws StandardException {
        forceDeserialization();
        return getDvd().getBoolean();
    }

    @Override
    public byte getByte() throws StandardException {
        forceDeserialization();
        return getDvd().getByte();
    }

    @Override
    public short getShort() throws StandardException {
        forceDeserialization();
        return getDvd().getShort();
    }

    @Override
    public int getInt() throws StandardException {
        forceDeserialization();
        return getDvd().getInt();
    }

    @Override
    public long getLong() throws StandardException {
        forceDeserialization();
        return getDvd().getLong();
    }

    @Override
    public float getFloat() throws StandardException {
        forceDeserialization();
        return getDvd().getFloat();
    }

    @Override
    public double getDouble() throws StandardException {
        forceDeserialization();
        return getDvd().getDouble();
    }

    @Override
    public int typeToBigDecimal() throws StandardException {
        forceDeserialization();
        return getDvd().typeToBigDecimal();
    }

    @Override
    public byte[] getBytes() throws StandardException {
        forceSerialization();
        return dvdBytes;
    }

    @Override
    public Date getDate(Calendar cal) throws StandardException {
        forceDeserialization();
        return getDvd().getDate(cal);
    }

    @Override
    public Time getTime(Calendar cal) throws StandardException {
        forceDeserialization();
        return getDvd().getTime(cal);
    }

    @Override
    public Timestamp getTimestamp(Calendar cal) throws StandardException {
        forceDeserialization();
        return getDvd().getTimestamp(cal);
    }

    @Override
    public Object getObject() throws StandardException {
        forceDeserialization();
        return getDvd().getObject();
    }

    @Override
    public InputStream getStream() throws StandardException {
        forceDeserialization();
        return getDvd().getStream();
    }

    @Override
    public boolean hasStream() {
        forceDeserialization();
        return getDvd().hasStream();
    }

    @Override
    public DataValueDescriptor cloneHolder() {
        return new LazyDataValueDescriptor(getDvd().cloneHolder(), serializerThunk);
    }

    @Override
    public DataValueDescriptor cloneValue(boolean forceMaterialization) {
        return new LazyDataValueDescriptor(getDvd().cloneValue(forceMaterialization), serializerThunk);
    }

    @Override
    public DataValueDescriptor recycle() {
        return null;
    }

    @Override
    public DataValueDescriptor getNewNull() {
        return new LazyDataValueDescriptor(getDvd().getNewNull(), serializerThunk);
    }

    @Override
    public void setValueFromResultSet(ResultSet resultSet, int colNumber, boolean isNullable) throws StandardException, SQLException {
        resetForSerialization();
        getDvd().setValueFromResultSet(resultSet, colNumber, isNullable);
    }

    @Override
    public void setInto(PreparedStatement ps, int position) throws SQLException, StandardException {
        resetForSerialization();
        getDvd().setInto(ps, position);
    }

    @Override
    public void setInto(ResultSet rs, int position) throws SQLException, StandardException {
        getDvd().setInto(rs, position);
    }

    @Override
    public void setValue(int theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(double theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(float theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(short theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(long theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(byte theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(boolean theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Object theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(byte[] theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setBigDecimal(Number bigDecimal) throws StandardException {
        resetForSerialization();
        getDvd().setValue(bigDecimal);
    }

    @Override
    public void setValue(String theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Blob theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Clob theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Time theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Time theValue, Calendar cal) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue, cal);
    }

    @Override
    public void setValue(Timestamp theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Timestamp theValue, Calendar cal) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue, cal);
    }

    @Override
    public void setValue(Date theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setValue(Date theValue, Calendar cal) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue, cal);
    }

    @Override
    public void setValue(DataValueDescriptor theValue) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theValue);
    }

    @Override
    public void setToNull() {
        resetForSerialization();
        getDvd().setToNull();
    }

    @Override
    public void normalize(DataTypeDescriptor dtd, DataValueDescriptor source) throws StandardException {
        resetForSerialization();
        getDvd().normalize(dtd, source);
    }

    @Override
    public BooleanDataValue isNullOp() {
        forceDeserialization();
        return getDvd().isNullOp();
    }

    @Override
    public BooleanDataValue isNotNull() {
        forceDeserialization();
        return getDvd().isNotNull();
    }

    @Override
    public String getTypeName() {
        return getDvd().getTypeName();
    }

    @Override
    public void setObjectForCast(Object value, boolean instanceOfResultType, String resultTypeClassName) throws StandardException {
        resetForSerialization();
        getDvd().setObjectForCast(value, instanceOfResultType, resultTypeClassName);
    }

    @Override
    public void readExternalFromArray(ArrayInputStream ais) throws IOException, ClassNotFoundException {
        resetForSerialization();
        getDvd().readExternalFromArray(ais);
    }

    @Override
    public int typePrecedence() {
        return getDvd().typePrecedence();
    }

    protected DataValueDescriptor unwrap(DataValueDescriptor dvd){

        DataValueDescriptor unwrapped = null;

        if(dvd instanceof LazyDataValueDescriptor){
            LazyDataValueDescriptor ldvd = (LazyDataValueDescriptor) dvd;
            ldvd.forceDeserialization();
            unwrapped = ldvd.getDvd();
        }else{
            unwrapped = dvd;
        }

        return unwrapped;
    }

    @Override
    public BooleanDataValue equals(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().equals(unwrap(left), unwrap(right));
    }

    @Override
    public BooleanDataValue notEquals(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().notEquals(unwrap(left), unwrap(right));
    }

    @Override
    public BooleanDataValue lessThan(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().lessThan(unwrap(left), unwrap(right));
    }

    @Override
    public BooleanDataValue greaterThan(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().greaterThan(unwrap(left), unwrap(right));
    }

    @Override
    public BooleanDataValue lessOrEquals(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().lessOrEquals(unwrap(left), unwrap(right));
    }

    @Override
    public BooleanDataValue greaterOrEquals(DataValueDescriptor left, DataValueDescriptor right) throws StandardException {
        forceDeserialization();
        return getDvd().greaterOrEquals(unwrap(left), unwrap(right));
    }

    @Override
    public DataValueDescriptor coalesce(DataValueDescriptor[] list, DataValueDescriptor returnValue) throws StandardException {
        forceDeserialization();
        return getDvd().coalesce(list, returnValue);
    }

    @Override
    public BooleanDataValue in(DataValueDescriptor left, DataValueDescriptor[] inList, boolean orderedList) throws StandardException {
        forceDeserialization();
        return getDvd().in(left, inList, orderedList);
    }

    @Override
    public int compare(DataValueDescriptor other) throws StandardException {
        forceDeserialization();

        return getDvd().compare(unwrap(other));
    }

    @Override
    public int compare(DataValueDescriptor other, boolean nullsOrderedLow) throws StandardException {
        forceDeserialization();

        return getDvd().compare(unwrap(other), nullsOrderedLow);
    }

    @Override
    public boolean compare(int op, DataValueDescriptor other, boolean orderedNulls, boolean unknownRV) throws StandardException {
        forceDeserialization();

        return getDvd().compare(op, unwrap(other), orderedNulls, unknownRV);
    }

    @Override
    public boolean compare(int op, DataValueDescriptor other, boolean orderedNulls, boolean nullsOrderedLow, boolean unknownRV) throws StandardException {
        forceDeserialization();

        return getDvd().compare(op, unwrap(other), orderedNulls, nullsOrderedLow, unknownRV);
    }

    @Override
    public void setValue(InputStream theStream, int valueLength) throws StandardException {
        resetForSerialization();
        getDvd().setValue(theStream, valueLength);
    }

    @Override
    public void checkHostVariable(int declaredLength) throws StandardException {
        getDvd().checkHostVariable(declaredLength);
    }

    @Override
    public int estimateMemoryUsage() {
        forceDeserialization();
        return getDvd().estimateMemoryUsage();
    }

    @Override
    public boolean isNull() {
        forceDeserialization();
        return getDvd() == null || getDvd().isNull();
    }

    @Override
    public void restoreToNull() {
        getDvd().restoreToNull();
        resetForSerialization();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeBoolean(getDvd() != null);

        if(getDvd() != null){
            out.writeObject(getDvd());
        }

        out.writeBoolean(dvdBytes != null);

        if(dvdBytes != null){
            out.writeObject(new FormatableBitSet(dvdBytes));
        }

        out.writeUTF(serializerThunk.getClass().getCanonicalName());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        if(in.readBoolean()){
            setDvd((DataValueDescriptor) in.readObject());
        }

        if(in.readBoolean()){
            FormatableBitSet fbs = (FormatableBitSet) in.readObject();
            dvdBytes = fbs.getByteArray();
        }

        try{
            serializerThunk = (SerializerThunk) Class.forName(in.readUTF()).newInstance();
        }catch(Exception e){
            throw new RuntimeException("Error deserializing serialization class", e);
        }
    }

    @Override
    public int getTypeFormatId() {
        forceDeserialization();
        return getDvd().getTypeFormatId();
    }

    protected DataValueDescriptor getDvd() {
        return dvd;
    }

    protected void setDvd(DataValueDescriptor dvd) {
        this.dvd = dvd;
    }

    @Override
    public int hashCode() {
        forceDeserialization();
        return getDvd().hashCode();
    }

    @Override
    public boolean equals(Object o) {

        forceDeserialization();

        Object other = o;

        if(o instanceof LazyDataValueDescriptor){
           other = unwrap((LazyDataValueDescriptor) o);
        }

        return getDvd().equals(other);
    }
}
