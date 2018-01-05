package com.bahmni.batch.bahmnianalytics.attribute.flattening;

import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

@PrepareForTest(BatchUtils.class)
@RunWith(PowerMockRunner.class)
public class AttributeFlattenerTest {

    @Mock
    private Resource metadataJson;

    @Mock
    private JdbcTemplate mysqlJdbcTemplate;
    private AttributeFlattener attributeFlattener;
    private String json;
    private List<String> pivotColumns = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        pivotColumns.add("pivotColumnOne");
        pivotColumns.add("pivotColumnTwo");
        json = "[\n" +
                "  {\n" +
                "    \"attribute_type_table_name\": \"type_table\",\n" +
                "    \"attribute_table_name\": \"value_table\",\n" +
                "    \"attribute_type_pivot_column_name\": \"name\",\n" +
                "    \"value_table_joining_id\": \"value_table_joining_id\",\n" +
                "    \"type_table_joining_id\": \"type_table_joining_id\",\n" +
                "    \"tableData\" :" +
                "    {\n" +
                "         \"name\": \"programs\",\n" +
                "         \"columns\": [\n" +
                "              {\n" +
                "                   \"name\": \"primary\",\n" +
                "                   \"type\": \"\",\n" +
                "                   \"isPrimaryKey\": false,\n" +
                "                   \"reference\": null\n" +
                "              }\n" +
                "         ]\n" +
                "     }\n" +
                "  }\n" +
                "]\n";

        attributeFlattener = new AttributeFlattener();

        PowerMockito.mockStatic(BatchUtils.class);

        setValuesForMemberFields(attributeFlattener, "metadataJson", metadataJson);
        setValuesForMemberFields(attributeFlattener, "mysqlJdbcTemplate", mysqlJdbcTemplate);
    }

    @Test
    public void shouldSetupPivotTableForGivenInfo() throws Exception {
        Mockito.when(BatchUtils.convertResourceOutputToString(metadataJson)).thenReturn(json);
        Mockito.when(mysqlJdbcTemplate.queryForList(anyString(), eq(String.class))).thenReturn(pivotColumns);

        attributeFlattener.run();

        Assert.assertEquals(1, attributeFlattener.getTableData().size());
        Assert.assertEquals(3, attributeFlattener.getTableData().get(0).getColumns().size());

        Assert.assertEquals("primary", attributeFlattener.getTableData().get(0).getColumns().get(0).getName());
        Assert.assertEquals("pivotColumnOne", attributeFlattener.getTableData().get(0).getColumns().get(1).getName());
        Assert.assertEquals("pivotColumnTwo", attributeFlattener.getTableData().get(0).getColumns().get(2).getName());

    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }
}
