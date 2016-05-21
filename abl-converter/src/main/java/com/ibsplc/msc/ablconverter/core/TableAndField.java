package com.ibsplc.msc.ablconverter.core;

/**
 * Created by maghesh on 10/05/2016.
 */
public class TableAndField {

    private String tableName;
    private String fieldName;
    private String fieldArraySize;
    private String dataType = ""; //TEMP ASSISNMENT...MUST CHANGE 

    public TableAndField create(final String... args) {
        if (args.length == 0) {
            return null; // or throw an exception
        }
        final TableAndField taf = new TableAndField();
        taf.setTableName(args[0]);
        taf.setFieldName(args[1]);
        taf.setFieldArraySize(args[2]);
        taf.setDataType(args[3]);
        // ...
        return taf;
    }

    public String getFieldArraySize() {
        return fieldArraySize;
    }

    public void setFieldArraySize(String fieldArraySize) {
        this.fieldArraySize = fieldArraySize;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getTableNameDotFieldName() {
    	return this.tableName + "." + this.fieldName;
    }



}
