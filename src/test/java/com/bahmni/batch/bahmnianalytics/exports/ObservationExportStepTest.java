package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.ObservationProcessor;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@PrepareForTest(BatchUtils.class)
@RunWith(PowerMockRunner.class)
public class ObservationExportStepTest {

    @Mock
    private DataSource dataSource;
    @Mock
    private StepBuilderFactory stepBuilderFactory;
    @Mock
    private Resource outputFolder;
    @Mock
    private FreeMarkerEvaluator<BahmniForm> freeMarkerEvaluator;
    @Mock
    private ObjectFactory<ObservationProcessor> observationProcessorFactory;
    @Mock
    private ObjectFactory<DatabaseObsWriter> obsWriterObjectFactory;

    private ObservationExportStep observationExportStep = new ObservationExportStep();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(BatchUtils.class);
        setValuesForMemberFields(observationExportStep, "dataSource", dataSource);
        setValuesForMemberFields(observationExportStep, "stepBuilderFactory", stepBuilderFactory);
        setValuesForMemberFields(observationExportStep, "outputFolder", outputFolder);
        setValuesForMemberFields(observationExportStep, "freeMarkerEvaluator", freeMarkerEvaluator);
        setValuesForMemberFields(observationExportStep, "observationProcessorFactory", observationProcessorFactory);
        setValuesForMemberFields(observationExportStep,"databaseObsWriterObjectFactory",obsWriterObjectFactory );
        setValuesForMemberFields(observationExportStep,"stepNumber", 0);
    }

    @Test
    public void shouldSetTheForm() throws Exception {
        BahmniForm form = mock(BahmniForm.class);
        Concept formName = mock(Concept.class);

        when(form.getFormName()).thenReturn(formName);
        String formWithLenthyName = "moreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormName";
        when(formName.getName()).thenReturn("Form").thenReturn(formWithLenthyName);

        observationExportStep.setForm(form);

        String stepName = observationExportStep.getStepName();
        assertEquals("Step-1 Form", stepName);
        stepName = observationExportStep.getStepName();
        assertEquals("Step-2 moreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormNamemoreThanHundredChar", stepName);

    }

    @Test
    public void shouldGetTheBatchStepForBaseExport() throws Exception {
        StepBuilder stepBuilder = mock(StepBuilder.class);
        BahmniForm form = mock(BahmniForm.class);
        Concept formNameConcept = mock(Concept.class);
        String formName = "Form";
        observationExportStep.setForm(form);
        SimpleStepBuilder simpleStepBuilder = mock(SimpleStepBuilder.class);
        TaskletStep expectedBaseExportStep = mock(TaskletStep.class);

        when(form.getFormName()).thenReturn(formNameConcept);
        when(formNameConcept.getName()).thenReturn(formName);
        when(stepBuilderFactory.get("Step-1 "+formName)).thenReturn(stepBuilder);
        when(stepBuilder.chunk(100)).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.reader(any())).thenReturn(simpleStepBuilder);
        when(observationProcessorFactory.getObject()).thenReturn(new ObservationProcessor());
        when(obsWriterObjectFactory.getObject()).thenReturn(new DatabaseObsWriter());
        when(simpleStepBuilder.processor(any())).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.writer(any())).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.build()).thenReturn(expectedBaseExportStep);


        Step observationExportStepStep = observationExportStep.getStep();

        Assert.assertNotNull(observationExportStepStep);
        Assert.assertEquals(expectedBaseExportStep, observationExportStepStep);
    }

    private void setValuesForMemberFields(Object observationExportStep, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = observationExportStep.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(observationExportStep, valueForMemberField);
    }
}
