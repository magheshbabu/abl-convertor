package com.ibsplc.msc.ablconverter.main;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	private static ApplicationContext context;

	public static void main(String[] args) throws IOException {
		
		context = new ClassPathXmlApplicationContext("abl-converter-config.xml");

		AblConverter ablConverter = (AblConverter)context.getBean("ablConverter");
		ablConverter.startConverting();

		System.out.println("Done");
		

	}

}
