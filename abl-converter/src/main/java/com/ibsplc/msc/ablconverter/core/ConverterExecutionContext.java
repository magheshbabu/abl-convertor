package com.ibsplc.msc.ablconverter.core;

public class ConverterExecutionContext {
	Line originalLine;
	String sourceCode;
	boolean isLineModified = false;
	
	public Line getOriginalLine() {
		return originalLine;
	}
	public void setOriginalLine(Line originalLine) {
		this.originalLine = originalLine;
	}
	public String getSourceCode() {
		return sourceCode;
	}
	public void setSourceCode(String sourceCode) {
		if(null != this.sourceCode && !this.sourceCode.equals(sourceCode)){
			this.isLineModified = true;
		}
		this.sourceCode = sourceCode;
	}
	public boolean isLineModified() {
		return isLineModified;
	}
	
}
