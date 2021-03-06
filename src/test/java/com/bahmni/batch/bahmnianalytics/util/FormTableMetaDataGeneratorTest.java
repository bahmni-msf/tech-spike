package com.bahmni.batch.bahmnianalytics.util;

import com.bahmni.batch.bahmnianalytics.form.domain.*;
import com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator;
import com.bahmni.batch.bahmnianalytics.table.domain.ForeignKey;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@PrepareForTest(IOUtils.class)
@RunWith(PowerMockRunner.class)
public class FormTableMetaDataGeneratorTest {

    private FormTableMetaDataGenerator formTableMetaDataGenerator;
    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(IOUtils.class);
    }

    @Test
    public void shouldReturnTableDataForGivenFormWithNoForeignKeys() {
        BahmniForm form = new BahmniForm();
        Concept formConcept = new Concept(1, "post-operative anaesthesia note", "N/A", 1, "post-operative anaesthesia note", null);
        form.setFormName(formConcept);
        form.setParent(null);
        form.addField(new Concept(2, "APN Anaesthesia start time", "Datetime", 0, "APN Anaesthesia start time", formConcept));
        form.addField(new Concept(3, "APN Anaesthesia end time", "Datetime", 0, "APN Anaesthesia end time", formConcept));

        formTableMetaDataGenerator = new FormTableMetaDataGenerator(form);
        TableData tableData = formTableMetaDataGenerator.run();

        assertNotNull(tableData);
        assertEquals("post-operative_anaesthesia_note", tableData.getName());
        assertNotNull(tableData.getColumns());
        assertEquals(5, tableData.getColumns().size());
        TableColumn postOperativeNoteColumn = tableData.getColumns().get(0);
        assertEquals("id_post-operative_anaesthesia_note", postOperativeNoteColumn.getName());
        assertEquals("integer", postOperativeNoteColumn.getType());
        assertTrue(postOperativeNoteColumn.isPrimaryKey());
        assertNull(postOperativeNoteColumn.getReference());

        TableColumn patientIdColumn = tableData.getColumns().get(1);
        assertEquals("patient_id", patientIdColumn.getName());
        assertEquals("integer", patientIdColumn.getType());
        assertFalse(patientIdColumn.isPrimaryKey());
        assertNull(patientIdColumn.getReference());

        TableColumn encounterIdColumn = tableData.getColumns().get(2);
        assertEquals("encounter_id", encounterIdColumn.getName());
        assertEquals("integer", encounterIdColumn.getType());
        assertFalse(encounterIdColumn.isPrimaryKey());
        assertNull(encounterIdColumn.getReference());

        TableColumn apnStartTimeColumn = tableData.getColumns().get(3);
        assertEquals("apn_anaesthesia_start_time", apnStartTimeColumn.getName());
        assertEquals("date", apnStartTimeColumn.getType());
        assertFalse(apnStartTimeColumn.isPrimaryKey());
        assertNull(apnStartTimeColumn.getReference());

        TableColumn apnEndTimeColumn = tableData.getColumns().get(4);
        assertEquals("apn_anaesthesia_end_time", apnEndTimeColumn.getName());
        assertEquals("date", apnEndTimeColumn.getType());
        assertFalse(apnEndTimeColumn.isPrimaryKey());
        assertNull(apnEndTimeColumn.getReference());
    }

    @Test
    public void shouldReturnTabbleDataForGivenFormWithForeignKeys() {
        BahmniForm allObservationTemplateForm = new BahmniForm();
        Concept allObservationTemplate = new Concept(0, "All Observation Template", "N/A", 1, "All Observation Template", null);
        BahmniForm postOperativeForm = new BahmniForm();
        postOperativeForm.setParent(allObservationTemplateForm);
        Concept postOperativeConcept = new Concept(0, "post-operative anaesthesia note", "N/A", 1, "post-operative anaesthesia note", allObservationTemplate);
        postOperativeForm.setFormName(postOperativeConcept);
        BahmniForm transfusionForm = new BahmniForm();
        Concept transfusionConcept = new Concept(1, "transfusion", "N/A", 1, "transfusion", postOperativeConcept);
        transfusionForm.setFormName(transfusionConcept);
        transfusionForm.setParent(postOperativeForm);
        transfusionForm.addField(new Concept(2, "Blood transfusion", "Text", 0, "Blood transfusion", transfusionConcept));
        transfusionForm.addField(new Concept(3, "Intra-operative transfusion related reaction comments", "Text", 0, "Intra-operative transfusion related reaction comments", transfusionConcept));

        formTableMetaDataGenerator = new FormTableMetaDataGenerator(transfusionForm);
        TableData tableData = formTableMetaDataGenerator.run();

        assertNotNull(tableData);
        assertEquals("transfusion", tableData.getName());
        assertNotNull(tableData.getColumns());
        assertEquals(6, tableData.getColumns().size());
        TableColumn transfusionColumn = tableData.getColumns().get(0);
        assertEquals("id_transfusion", transfusionColumn.getName());
        assertEquals("integer", transfusionColumn.getType());
        assertTrue(transfusionColumn.isPrimaryKey());
        assertNull(transfusionColumn.getReference());

        TableColumn postOperativeColumn = tableData.getColumns().get(1);
        assertEquals("id_post-operative_anaesthesia_note", postOperativeColumn.getName());
        assertEquals("integer", postOperativeColumn.getType());
        assertFalse(postOperativeColumn.isPrimaryKey());
        assertNotNull(postOperativeColumn.getReference());
        assertEquals("id_post-operative_anaesthesia_note", postOperativeColumn.getReference().getReferenceColumn());
        assertEquals("post-operative_anaesthesia_note", postOperativeColumn.getReference().getReferenceTable());

        TableColumn patientidColumn = tableData.getColumns().get(2);
        assertEquals("patient_id", patientidColumn.getName());
        assertEquals("integer", patientidColumn.getType());
        assertFalse(patientidColumn.isPrimaryKey());
        assertNull(patientidColumn.getReference());

        TableColumn encounterIdColumn = tableData.getColumns().get(3);
        assertEquals("encounter_id", encounterIdColumn.getName());
        assertEquals("integer", encounterIdColumn.getType());
        assertFalse(encounterIdColumn.isPrimaryKey());
        assertNull(encounterIdColumn.getReference());

        TableColumn bloodTransfusionColumn = tableData.getColumns().get(4);
        assertEquals("blood_transfusion", bloodTransfusionColumn.getName());
        assertEquals("text", bloodTransfusionColumn.getType());
        assertFalse(bloodTransfusionColumn.isPrimaryKey());
        assertNull(bloodTransfusionColumn.getReference());

        TableColumn transfusionCommentsColumn = tableData.getColumns().get(5);
        assertEquals("intra-operative_transfusion_related_reaction_comments", transfusionCommentsColumn.getName());
        assertEquals("text", transfusionCommentsColumn.getType());
        assertFalse(transfusionCommentsColumn.isPrimaryKey());
        assertNull(transfusionCommentsColumn.getReference());
    }

    @Test
    public void shouldAddForiegnKeyOfNewFormToAlreadyExistingTableData() throws Exception {
        BahmniForm form = new BahmniForm();
        BahmniForm parent = new BahmniForm();
        parent.setFormName(new Concept(123, "parent2", 1));
        parent.setParent(new BahmniForm());
        form.setParent(parent);
        TableData tableData = new TableData();
        TableColumn firstForiegnKey = new TableColumn("id_parent1", "TEXT", true, new ForeignKey("referenceColumn", "referenceTable"));
        List<TableColumn> tableColumns = new ArrayList<>();
        tableColumns.add(firstForiegnKey);
        tableData.setColumns(tableColumns);
        tableData.setName("formName");

        FormTableMetaDataGenerator formTableMetaDataGenerator = new FormTableMetaDataGenerator(form, tableData);
        formTableMetaDataGenerator.addForeignKey();

        assertEquals("id_parent2", tableData.getColumns().get(1).getName());
    }

    @Test
    public void shouldNotAddForiegnKeyToSameFormTwice() throws Exception {
        BahmniForm form = new BahmniForm();
        BahmniForm parent = new BahmniForm();
        parent.setFormName(new Concept(123, "parent1", 1));
        parent.setParent(new BahmniForm());
        form.setParent(parent);
        TableData tableData = new TableData();
        TableColumn firstForiegnKey = new TableColumn("id_parent1", "TEXT", true, new ForeignKey("referenceColumn", "referenceTable"));
        List<TableColumn> tableColumns = new ArrayList<>();
        tableColumns.add(firstForiegnKey);
        tableData.setColumns(tableColumns);
        tableData.setName("formName");

        FormTableMetaDataGenerator formTableMetaDataGenerator = new FormTableMetaDataGenerator(form, tableData);
        formTableMetaDataGenerator.addForeignKey();

        assertEquals(1, tableData.getColumns().size());
    }


}