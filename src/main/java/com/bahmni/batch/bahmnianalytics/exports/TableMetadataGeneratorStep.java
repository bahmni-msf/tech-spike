package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TableMetadataGeneratorStep {

    @Autowired
    private FreeMarkerEvaluator<TableData> freeMarkerEvaluatorForTables;

    private  Map<String, TableData> tableDataMap = new LinkedHashMap<>();

    private TableMetadataGeneratorStep() {

    }

    public List<TableData> getTableData() {
        return new ArrayList<TableData>(tableDataMap.values());
    }


    public TableData getTableDataForForm(BahmniForm form) {
        return tableDataMap.get(form.getFormName().getName());
    }

    public void generateTableDataForForm(BahmniForm form) {
        TableData tableDataForForm = getTableDataForForm(form);
        if(tableDataForForm != null) {
            tableDataMap.remove(form.getFormName().getName());
            TableMetaDataGenerator generator = new TableMetaDataGenerator(form,tableDataForForm);
            generator.addForeignKey();
            tableDataMap.put(form.getFormName().getName(),tableDataForForm);
        } else {
            TableMetaDataGenerator generator = new TableMetaDataGenerator(form);
            tableDataForForm = generator.run();
            tableDataMap.put(form.getFormName().getName(),tableDataForForm);
        }
    }
}
