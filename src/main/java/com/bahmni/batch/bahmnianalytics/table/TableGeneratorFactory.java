package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;

public class TableGeneratorFactory {

    public FormTableMetaDataGenerator getGeneratorForNewForm(BahmniForm form){
        return new FormTableMetaDataGenerator(form);
    }

    public FormTableMetaDataGenerator getGeneratorForExistingForm(BahmniForm form, TableData tableData){
        return new FormTableMetaDataGenerator(form,tableData);
    }
}
