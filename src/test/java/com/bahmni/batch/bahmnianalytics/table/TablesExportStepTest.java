package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.CommonTestHelper;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
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
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PrepareForTest(BatchUtils.class)
@RunWith(PowerMockRunner.class)
public class TablesExportStepTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private StepBuilderFactory stepBuilderFactory;

    @Mock
    private FreeMarkerEvaluator<TableData> tableRecordHolderFreeMarkerEvaluator;

    @Mock
    private ObjectFactory<TableRecordWriter> recordWriterObjectFactory;

    @Mock
    ResourceLoader resourceLoader;

    private TablesExportStep tablesExportStep = new TablesExportStep();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(BatchUtils.class);
        CommonTestHelper.setValuesForMemberFields(tablesExportStep, "dataSource", dataSource);
        CommonTestHelper.setValuesForMemberFields(tablesExportStep, "stepBuilderFactory", stepBuilderFactory);
        CommonTestHelper.setValuesForMemberFields(tablesExportStep, "tableRecordHolderFreeMarkerEvaluator", tableRecordHolderFreeMarkerEvaluator);
        CommonTestHelper.setValuesForMemberFields(tablesExportStep, "recordWriterObjectFactory", recordWriterObjectFactory);
        CommonTestHelper.setValuesForMemberFields(tablesExportStep, "resourceLoader", resourceLoader);
        BatchUtils.stepNumber = 0;
    }

    @Test
    public void shouldSetTheTable() throws Exception {
        TableData table = mock(TableData.class);
        String lengthyTableName = "moreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormName";
        when(table.getName()).thenReturn("table").thenReturn(lengthyTableName);

        tablesExportStep.setTableData(table);

        String stepName = tablesExportStep.getStepName();
        assertEquals("Step-1 table", stepName);
        stepName = tablesExportStep.getStepName();
        assertEquals("Step-2 moreThanHundredCharacterInTheFormNamemoreThanHundredCharacterInTheFormNamemoreThanHundredChar", stepName);

    }

    @Test
    public void shouldGetTheBatchStepForTableExport() throws Exception {
        TableData table = mock(TableData.class);
        StepBuilder stepBuilder = mock(StepBuilder.class);
        SimpleStepBuilder simpleStepBuilder = mock(SimpleStepBuilder.class);
        TaskletStep expectedBaseExportStep = mock(TaskletStep.class);
        tablesExportStep.setTableData(table);
        String tableName = "table";
        when(table.getName()).thenReturn(tableName);
        when(stepBuilderFactory.get("Step-1 " + tableName)).thenReturn(stepBuilder);
        when(stepBuilder.chunk(100)).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.reader(any())).thenReturn(simpleStepBuilder);
        when(recordWriterObjectFactory.getObject()).thenReturn(new TableRecordWriter());
        when(simpleStepBuilder.processor(any())).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.writer(any())).thenReturn(simpleStepBuilder);
        when(simpleStepBuilder.build()).thenReturn(expectedBaseExportStep);

        Step tablesExportStepStep = tablesExportStep.getStep();

        Assert.assertNotNull(tablesExportStepStep);
        Assert.assertEquals(expectedBaseExportStep, tablesExportStepStep);
    }
}