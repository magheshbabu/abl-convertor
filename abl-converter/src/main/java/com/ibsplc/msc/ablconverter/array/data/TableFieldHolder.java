package com.ibsplc.msc.ablconverter.array.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.ibsplc.msc.ablconverter.core.TableAndField;

@Component
public class TableFieldHolder {
	private  List<TableAndField> tableAndField = new LinkedList<TableAndField>();

	@Value("classpath:TableAndFieldDetails.csv")
	Resource file;
	
	public void load() throws FileNotFoundException, IOException{
		FileInputStream in = new FileInputStream(file.getFile());
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));

	    String strLine;
	    while ((strLine = br.readLine()) != null) {
	    	TableAndField tf = new TableAndField();
	    	tf = tf.create(strLine.split(","));
	    	tableAndField.add(tf);
	    }
	    br.close();
	    in.close();
	}

	public List<TableAndField> getTableAndField() {
		return tableAndField;
	}

	public boolean ExistsTable (String TableName) {

		ListIterator itr = tableAndField.listIterator();
		while (itr.hasNext()) {
			TableAndField tf = (TableAndField) itr.next();
			if(tf.getTableName()== TableName) {
				return true;
			}
		}
		return false;
	}

	public boolean ExistsField (String FieldName) {

		ListIterator itr = tableAndField.listIterator();
		while (itr.hasNext()) {
			TableAndField tf = (TableAndField) itr.next();
			if(tf.getFieldName()== FieldName) {
				return true;
			}
		}
		return false;
	}

	public boolean ExistsTableAndField (String TableAndFieldName) {

		ListIterator itr = tableAndField.listIterator();
		while (itr.hasNext()) {
			TableAndField tf = (TableAndField) itr.next();
			//System.out.println("TableAndFieldName = " + TableAndFieldName.trim().toLowerCase());
			//System.out.println("tf.getTableName()  tf.getFieldName().trim().toLowerCase()) = " + tf.getTableName() + "." + tf.getFieldName().trim().toLowerCase());
			if((tf.getTableName() + "." + tf.getFieldName().trim().toLowerCase()).equals(TableAndFieldName.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
