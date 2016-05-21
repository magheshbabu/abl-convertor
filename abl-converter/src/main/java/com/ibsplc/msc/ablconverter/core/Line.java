package com.ibsplc.msc.ablconverter.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Line {

	int lineNumber;
	String line;

	List<Fragment> fragments;

	public Line(int lineNumber, String line) {

		this.line = line;
		this.lineNumber = lineNumber;

		fragments = new ArrayList<>();

		// add white space
		Matcher m = Pattern.compile("^\\s*").matcher(line);
		while (m.find()) {
			String whiteSpace = m.group();
			Fragment fragment = new Fragment(whiteSpace, FragmentType.WhiteSpace);
			fragments.add(fragment);
			line = line.replace(whiteSpace, "");
		}

		// add all comments
		m = Pattern.compile("(\\/\\*)(.*?)(\\*\\/)").matcher(line);
		while (m.find()) {
			String comment = m.group();
			Fragment fragment = new Fragment(comment, FragmentType.Comment);
			fragments.add(fragment);
			line = line.replace(comment, "");
		}
		
		Fragment fragment = new Fragment(line.trim(), FragmentType.SourceCode);
		fragments.add(fragment);

	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLine() {
		return line;
	}

	public List<Fragment> getFragments() {
		return fragments;
	}
	
	public String getSourceCode(){
		if(null != this.getFragments() 
				&& !this.getFragments().isEmpty()
				&& this.getFragments().get(this.getFragments().size() - 1).getFragmentType() == FragmentType.SourceCode){
			return this.getFragments().get(this.getFragments().size() - 1).getCode();
		}
		
		return null;
	}
}
