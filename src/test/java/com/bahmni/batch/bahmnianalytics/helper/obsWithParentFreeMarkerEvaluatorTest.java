package com.bahmni.batch.bahmnianalytics.helper;

import com.bahmni.batch.bahmnianalytics.exception.BatchResourceException;
import com.bahmni.batch.bahmnianalytics.exports.ObsRecordExtractorForTable;
import com.bahmni.batch.bahmnianalytics.form.domain.*;
import freemarker.template.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class obsWithParentFreeMarkerEvaluatorTest {
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
        expectedException.expectMessage("Unable to continue generating a the template with name ["+templateName+"]");

        freeMarkerEvaluator.evaluate(templateName, new BahmniForm());
    }

    @Test
    public void shouldThrowExceptionGivenEmptyForm() throws Exception {
        String templateName = "obsWithParentSql.ftl";
        BahmniForm emptyForm = new BahmniForm();
        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name ["+templateName+"]");

        freeMarkerEvaluator.evaluate(templateName, emptyForm);

    }

    @Test
    public void shouldReturnSelectStatementWithNoJoinsForFormAtDepthLessThanTwo() throws Exception {
        String templateName = "obsWithParentSql.ftl";
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123,"FormAtDepthOne",1));
        form.setDepthToParent(1);
        String expectedSql = "SELECT\n" +
                "    obs0.obs_id\n" +
                "    FROM obs obs0\n" +
                "WHERE obs0.concept_id=123\n" +
                "AND obs0.voided = 0\n";

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, form);

        Assert.assertNotNull(generatedSql);
        assertEquals(expectedSql, generatedSql);
    }

    @Test
    public void shouldThrowExceptionForFormAtDepthGeaterThanOneAndHaveNoRootForm() throws Exception {
        String templateName = "obsWithParentSql.ftl";
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123,"FormAtDepthTwo",1));
        form.setDepthToParent(2);
        BahmniForm parent = new BahmniForm();
        parent.setFormName(new Concept(1234,"ParentForm",1));
        parent.setDepthToParent(1);
        form.setParent(parent);

        expectedException.expect(BatchResourceException.class);
        expectedException.expectMessage("Unable to continue generating a the template with name ["+templateName+"]");

        freeMarkerEvaluator.evaluate(templateName, form);
    }

    @Test
    public void shouldReturnSelectStatementWithJoinsForFormAtDepthGreaterThanOneHavingRootForm() throws Exception {
        String templateName = "obsWithParentSql.ftl";
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123,"FormAtDepthTwo",1));
        form.setDepthToParent(2);
        BahmniForm parent = new BahmniForm();
        parent.setFormName(new Concept(1234,"ParentForm",1));
        parent.setDepthToParent(1);
        form.setParent(parent);
        form.setRootForm(parent);
        String expectedSql = "SELECT\n" +
                "    obs0.obs_id\n" +
                "            ,obs1.obs_id as parent_obs_id\n" +
                "FROM obs obs0\n" +
                "INNER JOIN obs obs1 on ( obs1.obs_id=obs0.obs_group_id and obs1.voided=0 )\n" +
                "WHERE obs0.concept_id=123\n" +
                "AND obs0.voided = 0\n" +
                "AND obs1.concept_id=1234\n" +
                "AND obs1.concept_id=1234\n";

        String generatedSql = freeMarkerEvaluator.evaluate(templateName, form);

        Assert.assertNotNull(generatedSql);
        assertEquals(expectedSql, generatedSql);
    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }
}
