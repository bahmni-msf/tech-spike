package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;

public class TableGeneratorFactory {

    public  TableMetaDataGenerator getGeneratorForNewForm(BahmniForm form){
        return new TableMetaDataGenerator(form);
    }

    public  TableMetaDataGenerator getGeneratorForExistingForm(BahmniForm form, TableData tableData){
        return new TableMetaDataGenerator(form,tableData);
    }
}
