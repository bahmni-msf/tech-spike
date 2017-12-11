package com.bahmni.batch.bahmnianalytics;

import com.bahmni.batch.bahmnianalytics.exports.ObservationExportStep;
import com.bahmni.batch.bahmnianalytics.exports.TableGeneratorStep;
import com.bahmni.batch.bahmnianalytics.exports.TableMetadataGeneratorStep;
import com.bahmni.batch.bahmnianalytics.exports.TreatmentRegistrationBaseExportStep;
import com.bahmni.batch.bahmnianalytics.form.FormListProcessor;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

	public static final String FULL_DATA_EXPORT_JOB_NAME = "endtbExports";

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private TreatmentRegistrationBaseExportStep treatmentRegistrationBaseExportStep;


	@Autowired
	private FormListProcessor formListProcessor;

	@Autowired
	private ObjectFactory<ObservationExportStep> observationExportStepFactory;

	@Value("${templates}")
	private Resource freemarkerTemplateLocation;

	@Autowired
	public JobCompletionNotificationListener jobCompletionNotificationListener;

	@Autowired
	public TableGeneratorStep tableGeneratorStep;

	@Autowired
	public TableMetadataGeneratorStep tableMetadataGeneratorStep;


	@Value("${zipFolder}")
	private Resource zipFolder;

	@Value("${bahmniConfigFolder}")
	private Resource bahmniConfigFolder;

	@Bean
	public JobExecutionListener listener() {
		return jobCompletionNotificationListener;
	}

	@Bean
	public Job completeDataExport() throws IOException {

		List<BahmniForm> forms = formListProcessor.retrieveAllForms();

		FlowBuilder<FlowJobBuilder> completeDataExport = jobBuilderFactory.get(FULL_DATA_EXPORT_JOB_NAME)
				.incrementer(new RunIdIncrementer()).preventRestart()
				.listener(listener())
		                .flow(treatmentRegistrationBaseExportStep.getStep());

		for (BahmniForm form : forms) {
				ObservationExportStep observationExportStep = observationExportStepFactory.getObject();
				observationExportStep.setForm(form);
				completeDataExport.next(observationExportStep.getStep());
				tableMetadataGeneratorStep.generateTableDataForForm(form);
		}
		tableGeneratorStep.createTables();

		return completeDataExport.end().build();
	}

	@Bean
	public freemarker.template.Configuration freeMarkerConfiguration() throws IOException {
		freemarker.template.Configuration freemarkerTemplateConfig = new freemarker.template.Configuration(
				freemarker.template.Configuration.VERSION_2_3_22);
		freemarkerTemplateConfig.setDefaultEncoding("UTF-8");
		freemarkerTemplateConfig.setClassForTemplateLoading(this.getClass(),"/templates");
		freemarkerTemplateConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		return freemarkerTemplateConfig;
	}

	@PreDestroy
	public void generateReport(){
//		try {
//			File report = new File(bahmniConfigFolder.getFile(),"report.html");
//			String reportOutput =  reportGenerator.generateReport();
//			FileUtils.writeStringToFile(report,reportOutput);
//		}
//		catch (IOException e) {
//			throw new BatchResourceException("Unable to write the report file ["+zipFolder.getFilename()+"]",e);
//		}
	}
}
