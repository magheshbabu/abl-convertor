package com.ibsplc.msc.ablconverter.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.ibsplc.msc.ablconverter.core.ConverterExecutionContext;

public abstract class BaseConverter implements ItemProcessor<ConverterExecutionContext, ConverterExecutionContext>{
	
	@Override
	public ConverterExecutionContext process(ConverterExecutionContext context) throws Exception {
		
		context.setSourceCode(convert(context.getSourceCode()));
		
		return context;
	}

	protected abstract String convert(String code);

}
