package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.CommonTestHelper;
import com.bahmni.batch.bahmnianalytics.exception.BatchResourceException;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import freemarker.template.Configuration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@PrepareForTest(FreeMarkerEvaluator.class)
@RunWith(PowerMockRunner.class)
public class ReaderFreeMarkerEvaluatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private FreeMarkerEvaluator freeMarkerEvaluator;

    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        freeMarkerEvaluator = new FreeMarkerEvaluator<TableData>();
        configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File(FreeMarkerEvaluator.class.getResource("/templates").getPath()));
        CommonTestHelper.setValuesForMemberFields(freeMarkerEvaluator, "configuration", configuration);
    }

    @Test
    public void shouldThrowBatchExceptionIfTheFtlTemplateIsNotPresent() throws Exception {
        String templateName = "nonExisted.ftl";
        TableData tableData = new TableData();
        tableData.setName("tableName");
        TableColumn tableColumn = new TableColumn("columnName", "type", true, null);
        tableData.setColumns(Arrays.asList(tableColumn));

        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name [" + templateName + "]");

        freeMarkerEvaluator.evaluate(templateName, tableData);
    }

    @Test
    public void emptyTableDataObjectWithNoNameAndNoneOfTheColumns() throws Exception {
        String templateName = "reader.ftl";
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name [" + templateName + "]");

        freeMarkerEvaluator.evaluate(templateName, new TableData());
    }

    @Test
    public void shouldSelectFromTableWithSingleColumn() throws Exception {
        String templateName = "reader.ftl";
        TableData tableData = new TableData();
        tableData.setName("tableName");
        TableColumn tableColumn = new TableColumn("columnName", "type", true, null);
        tableData.setColumns(Arrays.asList(tableColumn));

        String sql = freeMarkerEvaluator.evaluate(templateName, tableData);

        assertEquals("SELECT tableName.columnName FROM tableName;", sql);
    }

    @Test
    public void shouldSelectFromTableWithMultipleColumns() throws Exception {
        String templateName = "reader.ftl";
        TableData tableData = new TableData();
        tableData.setName("tableName");
        TableColumn tableColumnOne = new TableColumn("columnOne", "type", true, null);
        TableColumn tableColumnTwo = new TableColumn("columnTwo", "type", false, null);
        tableData.setColumns(Arrays.asList(tableColumnOne, tableColumnTwo));

        String sql = freeMarkerEvaluator.evaluate(templateName, tableData);

        assertEquals("SELECT tableName.columnOne , tableName.columnTwo FROM tableName;", sql);
    }
    @Test
    public void shouldTransformTableWithColumnConceptIDToConceptName() throws Exception {
        String templateName = "reader.ftl";
        TableData tableData = new TableData();
        tableData.setName("tableName");
        TableColumn tableColumn = new TableColumn("concept_name", "text", false, null);
        tableData.setColumns(Arrays.asList(tableColumn));
        String sql = freeMarkerEvaluator.evaluate(templateName, tableData);

        assertEquals("SELECT (SELECT cn.name FROM concept_name cn WHERE cn.concept_id = tableName.concept_id AND cn.concept_name_type = 'FULLY_SPECIFIED') as concept_name FROM tableName;", sql);
    }




}
