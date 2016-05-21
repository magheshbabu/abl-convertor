package com.ibsplc.msc.ablconverter.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;

import com.ibsplc.msc.ablconverter.core.Line;

public class MultiLineReader implements ItemReader<Line>, ItemStream{
	
	private FlatFileItemReader<String> delegeteReader;
	private int lineNumber = 0;

	@Override
	public Line read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		StringBuilder sb = new StringBuilder();
		boolean canAppend = false;
		for (String line = null; (line = this.delegeteReader.read()) != null;) {
			lineNumber++;
			
			if(line.startsWith("1")){
				canAppend = true;
			}
			if(line.startsWith("4") && null != sb){
				canAppend = false;
			}
			
			line = line + System.getProperty("line.separator");
			sb.append(line);
			if(!canAppend){
				return new Line(lineNumber, sb.toString());
			}
		}
		
		return null;
		
	}
    

	@Override
	public void close() throws ItemStreamException {
		this.delegeteReader.close();
	}

	@Override
	public void open(ExecutionContext arg0) throws ItemStreamException {
		this.delegeteReader.open(arg0);
	}

	@Override
	public void update(ExecutionContext arg0) throws ItemStreamException {
		this.delegeteReader.update(arg0);
	}

	public void setDelegeteReader(FlatFileItemReader<String> delegeteReader) {
		this.delegeteReader = delegeteReader;
	}

}
