package com.ibsplc.msc.ablconverter.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.ibsplc.msc.ablconverter.core.ConverterExecutionContext;
import com.ibsplc.msc.ablconverter.core.Line;

public class InitProcessor implements ItemProcessor<Line, ConverterExecutionContext>{

	@Override
	public ConverterExecutionContext process(Line item) throws Exception {
		
		ConverterExecutionContext context = new ConverterExecutionContext();
		context.setOriginalLine(item);
		context.setSourceCode(item.getSourceCode());
		return context;
	}

}
