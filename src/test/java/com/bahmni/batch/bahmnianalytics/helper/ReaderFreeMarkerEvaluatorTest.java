package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.exception.BatchResourceException;
import com.bahmni.batch.bahmnianalytics.table.domain.ForeignKey;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import freemarker.template.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
        setValuesForMemberFields(freeMarkerEvaluator, "configuration", configuration);
    }

    @Test
    public void shouldThrowBatchExceptionIfTheFtlTemplateIsNotPresent() throws Exception {
        String templateName = "nonExisted.ftl";
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name [" + templateName + "]");

        freeMarkerEvaluator.evaluate(templateName, new TableData());
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

        assertEquals("SELECT columnName FROM tableName;", sql);
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

        assertEquals("SELECT columnOne , columnTwo FROM tableName;", sql);
    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }


}
