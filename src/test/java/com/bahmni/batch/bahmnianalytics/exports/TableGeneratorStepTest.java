package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.CommonTestHelper;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.table.TableGeneratorStep;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class TableGeneratorStepTest {

    @Mock
    private JdbcTemplate postgresJdbcTemplate;

    @Mock
    private FreeMarkerEvaluator<TableData> freeMarkerEvaluatorForTables;

    @Mock
    private FormTableMetadataGenImpl formTableMetadataGenImpl;

    private TableGeneratorStep tableGeneratorStep;

    @Before
    public void setUp() throws Exception {
        tableGeneratorStep = new TableGeneratorStep();
        CommonTestHelper.setValuesForMemberFields(tableGeneratorStep, "postgresJdbcTemplate", postgresJdbcTemplate);
        CommonTestHelper.setValuesForMemberFields(tableGeneratorStep, "freeMarkerEvaluatorForTables", freeMarkerEvaluatorForTables);
    }

    @Test
    public void shouldNotEvaluateFTLWhenThereAreNoTables() throws Exception {
        when(formTableMetadataGenImpl.getTableData()).thenReturn(new ArrayList<>());
        tableGeneratorStep.createTables(formTableMetadataGenImpl.getTableData());

        verify(freeMarkerEvaluatorForTables, times(0)).evaluate(any(), any());
    }

    @Test
    public void shouldExecuteCreateSqlGivenTableData() throws Exception {
        TableData tableData = new TableData();
        tableData.setName("tableName");
        when(formTableMetadataGenImpl.getTableData()).thenReturn(Arrays.asList(tableData));
        String sql = "someSql";
        when(freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData)).thenReturn(sql);

        tableGeneratorStep.createTables(formTableMetadataGenImpl.getTableData());

        verify(freeMarkerEvaluatorForTables, times(1)).evaluate("ddlForForm.ftl", tableData);
        verify(postgresJdbcTemplate, times(1)).execute(sql);
    }

    @Test
    public void shouldExecuteCreateSqlMultipleTimesGivenMultipleTableData() throws Exception {
        TableData tableData1 = new TableData();
        tableData1.setName("tableName");
        TableData tableData2 = new TableData();
        tableData2.setName("tableName");
        when(formTableMetadataGenImpl.getTableData()).thenReturn(Arrays.asList(tableData1,tableData2));
        String sql = "someSql";
        when(freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData1)).thenReturn(sql);
        when(freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData2)).thenReturn(sql);

        tableGeneratorStep.createTables(formTableMetadataGenImpl.getTableData());

        verify(freeMarkerEvaluatorForTables, times(1)).evaluate("ddlForForm.ftl", tableData1);
        verify(freeMarkerEvaluatorForTables, times(1)).evaluate("ddlForForm.ftl", tableData2);
        verify(postgresJdbcTemplate, times(2)).execute(sql);
    }

}