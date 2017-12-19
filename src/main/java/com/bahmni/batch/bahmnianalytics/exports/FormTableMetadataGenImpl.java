package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.TableGeneratorFactory;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("FormTableMetadataGenImpl")
public class FormTableMetadataGenImpl implements TableMetadataGenerator{

    private Map<String, TableData> tableDataMap = new LinkedHashMap<>();

    public void setTableDataMap(Map<String, TableData> tableDataMap) {
        this.tableDataMap = tableDataMap;
    }

    public List<TableData> getTableData() {
        return new ArrayList<TableData>(tableDataMap.values());
    }

    public void setGeneratorFactory(TableGeneratorFactory factory) {
        this.factory = factory;
    }

    private TableGeneratorFactory factory;

    private TableMetaDataGenerator generator;

    public TableData getTableDataForForm(BahmniForm form) {
        return tableDataMap.get(form.getFormName().getName());
    }

    public void generateTableDataForForm(BahmniForm form) {
        TableData tableDataForForm = getTableDataForForm(form);
        if (tableDataForForm != null) {
            tableDataMap.remove(form.getFormName().getName());
            generator = factory.getGeneratorForExistingForm(form, tableDataForForm);
            generator.addForeignKey();
            tableDataMap.put(form.getFormName().getName(), tableDataForForm);
        } else {
            generator = factory.getGeneratorForNewForm(form);
            tableDataForForm = generator.run();
            tableDataMap.put(form.getFormName().getName(), tableDataForForm);
        }
    }
}
