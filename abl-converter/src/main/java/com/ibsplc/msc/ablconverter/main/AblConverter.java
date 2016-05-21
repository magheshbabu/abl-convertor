package com.ibsplc.msc.ablconverter.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AblConverter {

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	Job job;

	@Value("${source.folder.name}")
	String folderName;

	static Logger logger = Logger.getLogger(AblConverter.class);

	public void startConverting() throws IOException {

		String dateStamp = new SimpleDateFormat("yyyyMMddhhmm")
				.format(new Date());

		Collection<File> files = FileUtils.listFiles(new File(folderName),
				new RegexFileFilter("^(.*?)"), TrueFileFilter.INSTANCE);

		for (File file : files) {
			try {

				SimpleLayout layout = new SimpleLayout();
				FileAppender appender = new FileAppender(layout, file.getParent() + File.separator + file.getName() + ".log", false);
				Logger.getRootLogger().addAppender(appender);
				
				Map<String, JobParameter> params = new HashMap<>();
				params.put("abl.source.file", new JobParameter(file.getPath()));
				params.put("abl.result.file", new JobParameter(file.getParent()
						+ File.separator + dateStamp + file.getName()));

				JobExecution execution = jobLauncher.run(job,
						new JobParameters(params));
				System.out.println("Exit Status : " + execution.getStatus());
				Logger.getRootLogger().removeAppender(appender);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
