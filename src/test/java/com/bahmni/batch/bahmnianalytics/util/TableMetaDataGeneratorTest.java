package com.bahmni.batch.bahmnianalytics.util;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
@PrepareForTest(IOUtils.class)
@RunWith(PowerMockRunner.class)
public class TableMetaDataGeneratorTest {

    private TableMetaDataGenerator tableMetaDataGenerator;
    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(IOUtils.class);
    }

    @Test
    public void shouldReturnTabbleDataForGivenFormWithNoForeignKeys() {
        BahmniForm form = new BahmniForm();
        Concept formConcept = new Concept(1, "post-operative anaesthesia note", "N/A", 1, "post-operative anaesthesia note", null);
        form.setFormName(formConcept);
        form.setParent(null);
        form.addField(new Concept(2, "APN Anaesthesia start time", "Datetime", 0, "APN Anaesthesia start time", formConcept));
        form.addField(new Concept(3, "APN Anaesthesia end time", "Datetime", 0, "APN Anaesthesia end time", formConcept));

        tableMetaDataGenerator = new TableMetaDataGenerator(form);
        TableData tableData = tableMetaDataGenerator.run();

        assertNotNull(tableData);
        assertEquals("post_operative_anaesthesia_note", tableData.getName());
        assertNotNull(tableData.getColumns());
        assertEquals(4, tableData.getColumns().size());
        TableColumn postOperativeNoteColumn = tableData.getColumns().get(0);
        assertEquals("id_post_operative_anaesthesia_note", postOperativeNoteColumn.getName());
        assertEquals("integer", postOperativeNoteColumn.getType());
        assertTrue(postOperativeNoteColumn.isPrimaryKey());
        assertNull(postOperativeNoteColumn.getReference());

        TableColumn encounterIdColumn = tableData.getColumns().get(1);
        assertEquals("encounter_id", encounterIdColumn.getName());
        assertEquals("integer", encounterIdColumn.getType());
        assertFalse(encounterIdColumn.isPrimaryKey());
        assertNull(encounterIdColumn.getReference());

        TableColumn apnStartTimeColumn = tableData.getColumns().get(2);
        assertEquals("apn_anaesthesia_start_time", apnStartTimeColumn.getName());
        assertEquals("date", apnStartTimeColumn.getType());
        assertFalse(apnStartTimeColumn.isPrimaryKey());
        assertNull(apnStartTimeColumn.getReference());

        TableColumn apnEndTimeColumn = tableData.getColumns().get(3);
        assertEquals("apn_anaesthesia_end_time", apnEndTimeColumn.getName());
        assertEquals("date", apnEndTimeColumn.getType());
        assertFalse(apnEndTimeColumn.isPrimaryKey());
        assertNull(apnEndTimeColumn.getReference());
    }

    @Test
    public void shouldReturnTabbleDataForGivenFormWithForeignKeys() {
        BahmniForm postOperativeForm = new BahmniForm();
        postOperativeForm.setParent(null);
        Concept preOperativeConcept = new Concept(0, "post-operative anaesthesia note", "N/A", 1, "post-operative anaesthesia note", null);
        postOperativeForm.setFormName(preOperativeConcept);
        BahmniForm transfusionForm = new BahmniForm();
        Concept transfusionConcept = new Concept(1, "transfusion", "N/A", 1, "transfusion", preOperativeConcept);
        transfusionForm.setFormName(transfusionConcept);
        transfusionForm.setParent(postOperativeForm);
        transfusionForm.addField(new Concept(2, "Blood transfusion", "Text", 0, "Blood transfusion", transfusionConcept));
        transfusionForm.addField(new Concept(3, "Intra-operative transfusion related reaction comments", "Text", 0, "Intra-operative transfusion related reaction comments", transfusionConcept));

        tableMetaDataGenerator = new TableMetaDataGenerator(transfusionForm);
        TableData tableData = tableMetaDataGenerator.run();

        assertNotNull(tableData);
        assertEquals("transfusion", tableData.getName());
        assertNotNull(tableData.getColumns());
        assertEquals(5, tableData.getColumns().size());
        TableColumn transfusionColumn = tableData.getColumns().get(0);
        assertEquals("id_transfusion", transfusionColumn.getName());
        assertEquals("integer", transfusionColumn.getType());
        assertTrue(transfusionColumn.isPrimaryKey());
        assertNull(transfusionColumn.getReference());

        TableColumn postOperativeColumn = tableData.getColumns().get(1);
        assertEquals("id_post_operative_anaesthesia_note", postOperativeColumn.getName());
        assertEquals("integer", postOperativeColumn.getType());
        assertFalse(postOperativeColumn.isPrimaryKey());
        assertNotNull(postOperativeColumn.getReference());
        assertEquals("id_post_operative_anaesthesia_note", postOperativeColumn.getReference().getReferenceColumn());
        assertEquals("post_operative_anaesthesia_note", postOperativeColumn.getReference().getReferenceTable());

        TableColumn encounterIdColumn = tableData.getColumns().get(2);
        assertEquals("encounter_id", encounterIdColumn.getName());
        assertEquals("integer", encounterIdColumn.getType());
        assertFalse(encounterIdColumn.isPrimaryKey());
        assertNull(encounterIdColumn.getReference());

        TableColumn bloodTransfusionColumn = tableData.getColumns().get(3);
        assertEquals("blood_transfusion", bloodTransfusionColumn.getName());
        assertEquals("text", bloodTransfusionColumn.getType());
        assertFalse(bloodTransfusionColumn.isPrimaryKey());
        assertNull(bloodTransfusionColumn.getReference());

        TableColumn transfusionCommentsColumn = tableData.getColumns().get(4);
        assertEquals("intra_operative_transfusion_related_reaction_comments", transfusionCommentsColumn.getName());
        assertEquals("text", transfusionCommentsColumn.getType());
        assertFalse(transfusionCommentsColumn.isPrimaryKey());
        assertNull(transfusionCommentsColumn.getReference());
    }


}