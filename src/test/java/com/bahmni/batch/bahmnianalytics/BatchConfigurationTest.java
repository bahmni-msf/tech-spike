package com.bahmni.batch.bahmnianalytics;

import com.bahmni.batch.bahmnianalytics.attribute.flattening.AttributeFlattener;
import com.bahmni.batch.bahmnianalytics.exports.AsIsTableGeneratorImpl;
import com.bahmni.batch.bahmnianalytics.exports.ObservationExportStep;
import com.bahmni.batch.bahmnianalytics.table.TableGeneratorStep;
import com.bahmni.batch.bahmnianalytics.exports.FormTableMetadataGenImpl;
import com.bahmni.batch.bahmnianalytics.exports.TreatmentRegistrationBaseExportStep;
import com.bahmni.batch.bahmnianalytics.form.FormListProcessor;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.table.TablesExportStep;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.JobFlowBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({BatchConfiguration.class, FileUtils.class})
@RunWith(PowerMockRunner.class)
public class BatchConfigurationTest {

    @Mock
    ExpectedException expectedException = ExpectedException.none();
    private BatchConfiguration batchConfiguration;
    @Mock
    private ClassPathResource bahmniConfigFolder;
    @Mock
    private Resource zipFolder;
    @Mock
    private Resource freemarkerTemplateLocation;
    @Mock
    private FormListProcessor formListProcessor;

    @Mock
    private JobBuilderFactory jobBuilderFactory;

    @Mock
    private TreatmentRegistrationBaseExportStep treatmentRegistrationBaseExportStep;

    @Mock
    private ObjectFactory<ObservationExportStep> observationExportStepFactory;

    @Mock
    private TableGeneratorStep tableGeneratorStep;

    @Mock
    private AsIsTableGeneratorImpl asIsTableGenerator;

    @Mock
    private ObjectFactory<TablesExportStep> tablesExportStepObjectFactory;

    @Mock
    public FormTableMetadataGenImpl formTableMetadataGenImpl;


    @Mock
    public AttributeFlattener attributeFlattener;


    @Before
    public void setUp() throws Exception {
        mockStatic(FileUtils.class);
        batchConfiguration = new BatchConfiguration();
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "bahmniConfigFolder", bahmniConfigFolder);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "zipFolder", zipFolder);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "freemarkerTemplateLocation", freemarkerTemplateLocation);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "formListProcessor", formListProcessor);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "jobBuilderFactory", jobBuilderFactory);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "treatmentRegistrationBaseExportStep", treatmentRegistrationBaseExportStep);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "observationExportStepFactory", observationExportStepFactory);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "tableGeneratorStep", tableGeneratorStep);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "formTableMetadataGenImpl", formTableMetadataGenImpl);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "asIsTableGenerator", asIsTableGenerator);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "tablesExportStepObjectFactory", tablesExportStepObjectFactory);
        CommonTestHelper.setValuesForMemberFields(batchConfiguration, "attributeFlattener", attributeFlattener);
    }

    @Test
    public void shouldAddFreeMarkerConfiguration() throws Exception {
        Configuration configuration = PowerMockito.mock(Configuration.class);
        whenNew(Configuration.class).withArguments(any()).thenReturn(configuration);
        File configurationFile = Mockito.mock(File.class);
        when(freemarkerTemplateLocation.getFile()).thenReturn(configurationFile);

        Configuration freeMarkerConfiguration = batchConfiguration.freeMarkerConfiguration();

        Assert.assertEquals(configuration, freeMarkerConfiguration);
        verify(configuration, times(1)).setClassForTemplateLoading(any(),any());
        verify(configuration, times(1)).setDefaultEncoding("UTF-8");
        verify(configuration, times(1)).setTemplateExceptionHandler(any(TemplateExceptionHandler.class));
    }

    @Test
    public void shouldCompleteDataExportWithObsForms() throws Exception {
        ArrayList<BahmniForm> bahmniForms = new ArrayList<>();
        BahmniForm medicalHistoryForm = new BahmniForm();
        BahmniForm fstg = new BahmniForm();
        bahmniForms.add(medicalHistoryForm);
        bahmniForms.add(fstg);

        when(formListProcessor.retrieveAllForms()).thenReturn(bahmniForms);
        JobBuilder jobBuilder = Mockito.mock(JobBuilder.class);
        when(jobBuilderFactory.get("endtbExports")).thenReturn(jobBuilder);
        when(jobBuilder.incrementer(any(RunIdIncrementer.class))).thenReturn(jobBuilder);
        when(jobBuilder.preventRestart()).thenReturn(jobBuilder);
        when(jobBuilder.listener(any(JobCompletionNotificationListener.class))).thenReturn(jobBuilder);
        Step treatmentStep = Mockito.mock(Step.class);
        when(treatmentRegistrationBaseExportStep.getStep()).thenReturn(treatmentStep);
        JobFlowBuilder jobFlowBuilder = Mockito.mock(JobFlowBuilder.class);
        when(jobBuilder.flow(treatmentStep)).thenReturn(jobFlowBuilder);
        FlowJobBuilder flowJobBuilder = Mockito.mock(FlowJobBuilder.class);
        when(jobFlowBuilder.end()).thenReturn(flowJobBuilder);
        Job expectedJob = Mockito.mock(Job.class);
        when(flowJobBuilder.build()).thenReturn(expectedJob);

        ObservationExportStep medicalHistoryObservationExportStep = Mockito.mock(ObservationExportStep.class);
        ObservationExportStep fstgObservationExportStep = Mockito.mock(ObservationExportStep.class);
        when(observationExportStepFactory.getObject()).thenReturn(medicalHistoryObservationExportStep).thenReturn(fstgObservationExportStep);
        Step medicalHistoryObservationStep = Mockito.mock(Step.class);
        Step fstgObservationStep = Mockito.mock(Step.class);
        when(medicalHistoryObservationExportStep.getStep()).thenReturn(medicalHistoryObservationStep);
        when(fstgObservationExportStep.getStep()).thenReturn(fstgObservationStep);

        Job actualJob = batchConfiguration.completeDataExport();

        Assert.assertEquals(expectedJob, actualJob);
        verify(formListProcessor, times(1)).retrieveAllForms();
        verify(jobBuilderFactory, times(1)).get("endtbExports");
        verify(treatmentRegistrationBaseExportStep, times(1)).getStep();
        verify(observationExportStepFactory, times(2)).getObject();
        verify(medicalHistoryObservationExportStep, times(1)).setForm(medicalHistoryForm);
        verify(fstgObservationExportStep, times(1)).setForm(fstg);
        verify(jobFlowBuilder, times(1)).next(medicalHistoryObservationStep);
        verify(jobFlowBuilder, times(1)).next(fstgObservationStep);
        verify(formTableMetadataGenImpl, times(1)).getTableData();
        verify(tableGeneratorStep, atLeastOnce()).createTables(formTableMetadataGenImpl.getTableData());
    }
}