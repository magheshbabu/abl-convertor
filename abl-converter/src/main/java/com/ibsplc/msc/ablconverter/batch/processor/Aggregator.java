package com.ibsplc.msc.ablconverter.batch.processor;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.ibsplc.msc.ablconverter.core.ConverterExecutionContext;
import com.ibsplc.msc.ablconverter.core.Fragment;
import com.ibsplc.msc.ablconverter.core.FragmentType;

public class Aggregator implements ItemProcessor<ConverterExecutionContext, String>{
	
	private static final Logger LOGGER = Logger.getLogger(Aggregator.class);
	
	@Value("${new.line.comment}")
	String newLineComments;
	
	@Value("${old.line.comment}")
	String oldLineComments;
	
	@Value("#{jobParameters['abl.source.file']}")
	String fileName;

	@Override
	public String process(ConverterExecutionContext item) throws Exception {
		
		if(item.isLineModified()){
			StringBuilder builder = new StringBuilder(item.getSourceCode());
			if(null != item.getOriginalLine().getFragments() && !item.getOriginalLine().getFragments().isEmpty()){
				for (int i = 0; i < item.getOriginalLine().getFragments().size() - 1; i++) {
					Fragment f = item.getOriginalLine().getFragments().get(i);
					
					if(0 == i && f.getFragmentType() == FragmentType.WhiteSpace){
						builder.insert(0, f.getCode());
						continue;
					}
					 
					builder.append(" " + f.getCode() + " ");// appending comment at the last of line
				}
			}
			
			builder.insert(0, "/* " + item.getOriginalLine().getLine().trim() + " */" + oldLineComments + "\n");
			//builder.insert(0, "/* " + "item.getOriginalLine().getLine()" + " */" );
			builder.append(newLineComments);
			
			String processedLine = builder.toString();
			
			LOGGER.debug("Before processing line " + item.getOriginalLine().getLineNumber() + " in file " + this.fileName + " :: " + item.getOriginalLine().getLine());
			LOGGER.debug("After processing line " + item.getOriginalLine().getLineNumber() + " in file " + this.fileName + " :: " + processedLine);
			return processedLine;
		}
		
		return item.getOriginalLine().getLine().trim();
	}

}
