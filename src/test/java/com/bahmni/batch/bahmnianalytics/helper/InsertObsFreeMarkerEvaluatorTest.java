package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.exception.BatchResourceException;
import com.bahmni.batch.bahmnianalytics.exports.ObsRecordExtractorForTable;
import com.bahmni.batch.bahmnianalytics.form.domain.ForeignKey;
import com.bahmni.batch.bahmnianalytics.form.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import freemarker.template.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class InsertObsFreeMarkerEvaluatorTest {
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

        freeMarkerEvaluator.evaluate(templateName, new ObsRecordExtractorForTable("tableName"));
    }

    @Test
    public void shouldReturnEmptyStringForExtractorWithTableNameAndNoRecordList() throws Exception {
        String templateName = "insertObs.ftl";
        ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable("tableName");

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, extractor);
        assertEquals("", generatedSql);
    }


    @Test
    public void shouldReturnOneInsertStatementForExtractorWithTableNameAndOneRecord() throws Exception {
        String templateName = "insertObs.ftl";
        ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable("tableName");
        Map<String, String> record = new HashMap<>();
        record.put("column_name", "column_value");
        extractor.setRecordList(Arrays.asList(record));
        String expectedSql = "INSERT INTO tableName ( column_name ) VALUES ( column_value );";

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, extractor);

        Assert.assertNotNull(generatedSql);
        assertEquals(expectedSql, generatedSql);
    }

    @Test
    public void shouldReturnMultipleInsertStatementsForExtractorWithTableNameAndMultipleRecords() throws Exception {
        String templateName = "insertObs.ftl";
        ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable("tableName");
        Map<String, String> record1 = new HashMap<>();
        record1.put("r1_column_name1", "r1_column_value1");
        record1.put("r1_column_name2", "r1_column_value2");
        record1.put("r1_column_name3", "r1_column_value3");
        Map<String, String> record2 = new HashMap<>();
        record2.put("r2_column_name1", "r2_column_value1");
        record2.put("r2_column_name2", "r2_column_value2");
        record2.put("r2_column_name3", "r2_column_value3");
        extractor.setRecordList(Arrays.asList(record1, record2));
        String expectedSql = "INSERT INTO tableName ( r1_column_name1 , r1_column_name3 , r1_column_name2 ) " +
                "VALUES ( r1_column_value1 , r1_column_value3 , r1_column_value2 ); " +
                "INSERT INTO " + "tableName ( r2_column_name3 , r2_column_name1 , r2_column_name2 )" +
                " VALUES ( r2_column_value3 , r2_column_value1 , r2_column_value2 );";

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, extractor);

        Assert.assertNotNull(generatedSql);
        assertEquals(expectedSql, generatedSql);
    }

    @Test
    public void shouldReturnInsertStatementsWithNullValuesForRecordsWithNullValues() throws Exception {
        String templateName = "insertObs.ftl";
        ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable("tableName");
        Map<String, String> record1 = new HashMap<>();
        record1.put("r1_column_name1", null);
        record1.put("r1_column_name2", "r1_column_value2");
        record1.put("r1_column_name3", null);
        Map<String, String> record2 = new HashMap<>();
        record2.put("r2_column_name1", null);
        record2.put("r2_column_name2", null);
        record2.put("r2_column_name3", "r2_column_value3");
        extractor.setRecordList(Arrays.asList(record1, record2));
        String expectedSql = "INSERT INTO tableName ( r1_column_name1 , r1_column_name3 , r1_column_name2 ) " +
                "VALUES ( null , null , r1_column_value2 ); " +
                "INSERT INTO tableName ( r2_column_name3 , r2_column_name1 , r2_column_name2 ) " +
                "VALUES ( r2_column_value3 , null , null );";

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, extractor);

        Assert.assertNotNull(generatedSql);
        assertEquals(expectedSql, generatedSql);
    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }
}
