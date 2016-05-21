package com.ibsplc.msc.ablconverter.core;

public class Fragment {

	String code;
	FragmentType fragmentType;
	
	public Fragment(String code, FragmentType fragmentType){
		this.code = code;
		this.fragmentType = fragmentType;
	}

	public String getCode() {
		return code;
	}

	public FragmentType getFragmentType() {
		return fragmentType;
	}
	
	
}
