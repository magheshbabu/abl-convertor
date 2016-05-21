package com.ibsplc.msc.ablconverter.batch.listener;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class Exceptionlistener implements StepExecutionListener {
	
	private static final Logger LOGGER = Logger.getLogger(Exceptionlistener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
		for (Throwable e : stepExecution.getFailureExceptions()) {
			LOGGER.error(e.getMessage(), e);
		}
		return ExitStatus.COMPLETED;
	}

}
