package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.attribute.flattening.AttributesModel;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Field;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(PowerMockRunner.class)
public class AttributeTableExportStepTest {
    @Mock
    private AttributesModel attributesModel;

    @Mock
    private StepBuilderFactory stepBuilderFactory;

    @Mock
    ResourceLoader resourceLoader;

    @Mock
    private FreeMarkerEvaluator<AttributesModel> attributesModelFreeMarkerEvaluator;


    private AttributeTableExportStep attributeTableExportStep = new AttributeTableExportStep();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(BatchUtils.class);
        setValuesForMemberFields(attributeTableExportStep, "attributesModel", attributesModel);
        setValuesForMemberFields(attributeTableExportStep, "stepBuilderFactory", stepBuilderFactory);
        setValuesForMemberFields(attributeTableExportStep, "attributesModelFreeMarkerEvaluator", attributesModelFreeMarkerEvaluator);
        setValuesForMemberFields(attributeTableExportStep, "resourceLoader", resourceLoader);
        BatchUtils.stepNumber = 0;
    }

    private void setValuesForMemberFields(Object observationExportStep, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = observationExportStep.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(observationExportStep, valueForMemberField);
    }

    @Test
    public void shouldReturnTableDataFromAttributeModel() {

        attributeTableExportStep.getTableData();

        verify(attributesModel, times(1)).getTableData();
    }

    @Test
    public void shouldEvaluateFTLOnAttributeModelWithAttributeFTL() {

        attributeTableExportStep.getJDBCReaderSqlString();

        verify(attributesModelFreeMarkerEvaluator, times(1)).evaluate("attribute.ftl", attributesModel);
    }
}