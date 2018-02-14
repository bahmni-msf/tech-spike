package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.CommonTestHelper;
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

import java.lang.reflect.Field;


@PrepareForTest(BatchUtils.class)
@RunWith(PowerMockRunner.class)
public class AsIsTableGeneratorImplTest {

    private AsIsTableGeneratorImpl asIsTableGenerator;

    @Mock
    private Resource metadataJson;

    private String json;

    @Before
    public void setUp() throws Exception {
        asIsTableGenerator = new AsIsTableGeneratorImpl();
        PowerMockito.mockStatic(BatchUtils.class);
        json = "[\n" +
            "  {\n" +
            "    \"name\": \"programs\",\n" +
            "    \"columns\": [\n" +
            "      {\n" +
            "        \"name\": \"Name\",\n" +
            "        \"type\": \"\",\n" +
            "        \"isPrimaryKey\": false,\n" +
            "        \"reference\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Id\",\n" +
            "        \"type\": \"\",\n" +
            "        \"isPrimaryKey\": false,\n" +
            "        \"reference\": null\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"episodes\",\n" +
            "    \"columns\": [\n" +
            "      {\n" +
            "        \"name\": \"Name1\",\n" +
            "        \"type\": \"\",\n" +
            "        \"isPrimaryKey\": false,\n" +
            "        \"reference\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Id1\",\n" +
            "        \"type\": \"\",\n" +
            "        \"isPrimaryKey\": false,\n" +
            "        \"reference\": null\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "]\n";
        CommonTestHelper.setValuesForMemberFields(asIsTableGenerator, "metadataJson", metadataJson);
    }

    @Test
    public void shouldSetupTableDataForGivenInfo() throws Exception {
        Mockito.when(BatchUtils.convertResourceOutputToString(metadataJson)).thenReturn(json);

        asIsTableGenerator.run();

        Assert.assertEquals(2, asIsTableGenerator.getTableData().size());

        Assert.assertEquals("Name", asIsTableGenerator.getTableData().get(0).getColumns().get(0).getName());
        Assert.assertEquals("Id", asIsTableGenerator.getTableData().get(0).getColumns().get(1).getName());

        Assert.assertEquals("Name1", asIsTableGenerator.getTableData().get(1).getColumns().get(0).getName());
        Assert.assertEquals("Id1", asIsTableGenerator.getTableData().get(1).getColumns().get(1).getName());
    }

}
