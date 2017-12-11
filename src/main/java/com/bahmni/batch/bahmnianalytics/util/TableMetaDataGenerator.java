package com.bahmni.batch.bahmnianalytics.util;

import com.bahmni.batch.bahmnianalytics.form.domain.*;
import com.bahmni.batch.bahmnianalytics.helper.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TableMetaDataGenerator {

    private BahmniForm form;
    private TableData tableData;

    public TableMetaDataGenerator(BahmniForm form) {
        this.form = form;
        tableData = new TableData();
    }

    public TableMetaDataGenerator(BahmniForm form, TableData tableData) {
        this.form = form;
        this.tableData = tableData;
    }

    public TableData run() {
        String formName = form.getFormName().getName();
        tableData.setName(getProcessedName(formName));
        configureColumns();
        return tableData;
    }

    private void configureColumns() {
        configurePrimaryKeyColumn();
        configureForeignKeyColumn();
        configurePatientIdColumn();
        configureEncounterIdColumn();
        configureNonKeyColumns();
    }

    private void configurePatientIdColumn() {
        TableColumn patientIdColumn = new TableColumn("patient_id", "integer", false, null);
        tableData.addColumn(patientIdColumn);
    }

    private void configureEncounterIdColumn() {
        TableColumn encounterIdColumn = new TableColumn("encounter_id", "integer", false, null);
        tableData.addColumn(encounterIdColumn);
    }

    private void configureNonKeyColumns() {
        List<Concept> fields = form.getFields();
        List<TableColumn> columns = new ArrayList<>();
        fields.stream().forEach(field -> columns.add(new TableColumn(getProcessedName(field.getName()),
                Constants.openMRSToPostgresDataTypeMap.get(field.getDataType()),
                false,
                null)));
        tableData.addAllColumns(columns);
    }

    private void configureForeignKeyColumn() {
        if (form.getParent() != null && form.getParent().getParent() != null) {

            Concept formParentConcept = form.getParent().getFormName();
            String formParentConceptName = formParentConcept.getName();
            String processedName = getProcessedName(formParentConceptName);
            String referenceColumn = "id_" + processedName;
            ForeignKey reference = new ForeignKey(referenceColumn, processedName);

            tableData.addColumn(new TableColumn(referenceColumn,
                    Constants.openMRSToPostgresDataTypeMap.get(formParentConcept.getDataType()),
                    false,
                    reference));
        }
    }

    private void configurePrimaryKeyColumn() {
        tableData.addColumn(new TableColumn(getProcessedName("id_" + form.getFormName().getName()),
                Constants.openMRSToPostgresDataTypeMap.get(form.getFormName().getDataType()),
                true,
                null));
    }

    public void addForeignKey() {
        if (form.getParent() != null && form.getParent().getParent() != null) {
            Concept formParentConcept = form.getParent().getFormName();
            String formParentConceptName = formParentConcept.getName();
            String processedName = getProcessedName(formParentConceptName);
            String referenceColumn = "id_" + processedName;
            List<TableColumn> filteredReferenceColumns = tableData.getColumns().stream().filter( tableColumn -> tableColumn.getName().equals(referenceColumn)).collect(Collectors.toList());
            if(filteredReferenceColumns.size() == 0){
                configureForeignKeyColumn();
            }
        }
    }

    public static String getProcessedName(String formName) {
        return formName.replaceAll("[^\\w\\s]", " ").trim().replaceAll(" +","_").toLowerCase();
    }
}
