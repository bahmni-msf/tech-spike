package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.service.ObsService;
import com.bahmni.batch.bahmnianalytics.table.TableGeneratorFactory;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.table.TableMetadataGenerator;
import com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component("FormTableMetadataGenImpl")
public class FormTableMetadataGenImpl implements TableMetadataGenerator {

    @Value("${flag}")
    private String flag;

    @Value("${addMoreAndMultiSelectConcepts}")
    private String multiSelectConceptsNames;

    private List<Concept> multiSelectConcepts;

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

    private FormTableMetaDataGenerator generator;

    @Autowired
    private ObsService obsService;

    public TableData getTableDataForForm(BahmniForm form) {
        return tableDataMap.get(form.getFormName().getName());
    }

    public void generateTableDataForForm(BahmniForm form) {
        TableData tableDataForForm = getTableDataForForm(form);
        if (tableDataForForm != null) {
            tableDataMap.remove(form.getFormName().getName());
            generator = factory.getGeneratorForExistingForm(form, Boolean.parseBoolean(flag), multiSelectConcepts, tableDataForForm);
            generator.addForeignKey();
            tableDataMap.put(form.getFormName().getName(), tableDataForForm);
        } else {
            generator = factory.getGeneratorForNewForm(form, Boolean.parseBoolean(flag), multiSelectConcepts);
            tableDataForForm = generator.run();
            tableDataMap.put(form.getFormName().getName(), tableDataForForm);
        }
    }

    @PostConstruct
    public void postContruct() {
        this.multiSelectConcepts = obsService.getConceptsByNames(multiSelectConceptsNames);
    }
}
