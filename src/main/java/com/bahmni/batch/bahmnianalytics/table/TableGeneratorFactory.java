package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;

import java.util.List;

public class TableGeneratorFactory {

    public FormTableMetaDataGenerator getGeneratorForNewForm(BahmniForm form, boolean withOutConfig, List<Concept> multiSelectConcepts){
        return new FormTableMetaDataGenerator(form, withOutConfig, multiSelectConcepts);
    }

    public FormTableMetaDataGenerator getGeneratorForExistingForm(BahmniForm form, boolean withOutConfig, List<Concept> multiSelectConcepts, TableData tableData){
        return new FormTableMetaDataGenerator(form, withOutConfig, multiSelectConcepts, tableData);
    }
}
