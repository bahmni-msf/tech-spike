package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.CommonTestHelper;
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
import java.util.ArrayList;

@PrepareForTest(FreeMarkerEvaluator.class)
@RunWith(PowerMockRunner.class)
public class DDLForFormFreeMarkerEvaluatorTest {
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
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name ["+templateName+"]");

        freeMarkerEvaluator.evaluate(templateName, new TableData());
    }

    @Test
    public void emptyTableDataObjectWithNoNameAndNoneOfTheColumns() throws Exception {
        String templateName = "ddlForForm.ftl";
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name ["+templateName+"]");

        freeMarkerEvaluator.evaluate(templateName, new TableData());
    }


    @Test
    public void shouldDropTheTableIfTheUpdatedFormDoesntHaveAnyColumns() throws Exception {
        TableData tableData = new TableData();
        tableData.setName("formWithNoChildren");
        String generatedSql = freeMarkerEvaluator.evaluate("ddlForForm.ftl", tableData);
        Assert.assertNotNull(generatedSql);
        Assert.assertEquals("DROP TABLE IF EXISTS \"formWithNoChildren\" CASCADE;", generatedSql);
    }

    @Test
    public void shouldParseTableDataWithColumnsWithNoPrimaryAndNoForeignKey() throws Exception {
        TableData tableData = new TableData();
        tableData.setName("formWithChildren");
        ArrayList<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("patient_id", "integer", false, null));
        columns.add(new TableColumn("encounter_id", "integer", false, null));
        tableData.setColumns(columns);
        String generatedSql = freeMarkerEvaluator.evaluate("ddlForForm.ftl", tableData);
        Assert.assertNotNull(generatedSql);
        Assert.assertEquals("DROP TABLE IF EXISTS \"formWithChildren\" CASCADE; CREATE TABLE \"formWithChildren\"( \"patient_id\" integer , \"encounter_id\" integer );", generatedSql);
    }

    @Test
    public void shouldParseTableDataWithColumnsWithPrimaryAndNoForeignKey() throws Exception {
        TableData tableData = new TableData();
        tableData.setName("formWithChildren");
        ArrayList<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("patient_id", "integer", true, null));
        columns.add(new TableColumn("encounter_id", "integer", false, null));
        tableData.setColumns(columns);
        String generatedSql = freeMarkerEvaluator.evaluate("ddlForForm.ftl", tableData);
        Assert.assertNotNull(generatedSql);
        Assert.assertEquals("DROP TABLE IF EXISTS \"formWithChildren\" CASCADE; CREATE TABLE \"formWithChildren\"( \"patient_id\" integer PRIMARY KEY , \"encounter_id\" integer );", generatedSql);
    }

    @Test
    public void shouldParseTableDataWithColumnsWithPrimaryAndForeignKey() throws Exception {
        TableData tableData = new TableData();
        tableData.setName("formWithChildren");
        ArrayList<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("patient_id", "integer", true, null));
        columns.add(new TableColumn("encounter_id", "integer", false, new ForeignKey("id", "encounter")));
        tableData.setColumns(columns);
        String generatedSql = freeMarkerEvaluator.evaluate("ddlForForm.ftl", tableData);
        Assert.assertNotNull(generatedSql);
        Assert.assertEquals("DROP TABLE IF EXISTS \"formWithChildren\" CASCADE; CREATE TABLE \"formWithChildren\"( \"patient_id\" integer PRIMARY KEY , \"encounter_id\" integer REFERENCES \"encounter\" (\"id\") );", generatedSql);
    }

}
