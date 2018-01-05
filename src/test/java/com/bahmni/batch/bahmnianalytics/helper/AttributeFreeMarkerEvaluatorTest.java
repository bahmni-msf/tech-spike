package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.attribute.flattening.AttributesModel;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import freemarker.template.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

public class AttributeFreeMarkerEvaluatorTest {


    private FreeMarkerEvaluator attributeFreeMarkerEvaluator;
    private Configuration configuration;
    private String templateName = "attribute.ftl";
    private AttributesModel attributeModel;

    @Before
    public void setUp() throws Exception {
        attributeFreeMarkerEvaluator = new FreeMarkerEvaluator<TableData>();
        configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File(FreeMarkerEvaluator.class.getResource("/templates").getPath()));
        setValuesForMemberFields(attributeFreeMarkerEvaluator, "configuration", configuration);


        attributeModel = new AttributesModel();
        attributeModel.setAttribute_type_table_name("type_table");
        attributeModel.setAttribute_table_name("value_table");
        attributeModel.setValue_table_joining_id("value_table_joining_id");
        attributeModel.setType_table_joining_id("type_table_joining_id");
    }

    @Test (expected = Exception.class)
    public void shouldThrowExceptionIfAttributeModelIsEmpty() throws Exception {
        AttributesModel attributeModel = new AttributesModel();
        attributeFreeMarkerEvaluator.evaluate(templateName, attributeModel);
    }

    @Test
    public void shouldReturnQueryWithPrimaryKey() {
        TableData tableData = new TableData();
        tableData.setName("pivot_table");
        TableColumn primaryKeyColumn = new TableColumn("primary_id", "integer", true, null);
        tableData.setColumns(Arrays.asList(primaryKeyColumn));
        attributeModel.setTableData(tableData);

        String sql = attributeFreeMarkerEvaluator.evaluate(templateName, attributeModel);

        Assert.assertTrue(sql.contains("primary_id"));
    }
    @Test(expected = Exception.class)
    public void shouldThrowExceptionForTableDataWithoutIDColumn() {
        TableData tableData = new TableData();
        tableData.setName("pivot_table");
        TableColumn primaryKeyColumn = new TableColumn("primary_key", "integer", true, null);
        tableData.setColumns(Arrays.asList(primaryKeyColumn));
        attributeModel.setTableData(tableData);

        String sql = attributeFreeMarkerEvaluator.evaluate(templateName, attributeModel);

        Assert.assertTrue(sql.contains("primary_key"));
    }

    @Test
    public void shouldReturnQueryWithPivotColumns() {
        TableData tableData = new TableData();
        tableData.setName("pivot_table");
        TableColumn primaryKeyColumn = new TableColumn("primary_id", "integer", true, null);
        TableColumn pivotColumn = new TableColumn("pivotColumn", "text", true, null);
        tableData.setColumns(Arrays.asList(primaryKeyColumn, pivotColumn));
        attributeModel.setTableData(tableData);

        String sql = attributeFreeMarkerEvaluator.evaluate(templateName, attributeModel);

        Assert.assertTrue(sql.contains("pivotColumn"));
    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }
}
