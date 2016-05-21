package com.ibsplc.msc.ablconverter.batch.util;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.LineMapper;

import com.ibsplc.msc.ablconverter.core.Line;

public class AblLineMapper implements LineMapper<Line>{

	StepExecution stepExecution;
	
	@Override
	public Line mapLine(String line, int lineNumber) throws Exception {
		return new Line(lineNumber, line);
	}

}
