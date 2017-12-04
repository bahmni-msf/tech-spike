package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TableGeneratorStep {

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Autowired
    private FreeMarkerEvaluator<TableData> freeMarkerEvaluatorForTables;

    Map<String, TableData> tableDataMap = new HashMap<>();

    private TableGeneratorStep() {

    }

    public TableData getTableDataForForm(BahmniForm form) {
        return tableDataMap.get(form.getFormName().getName());
    }

    public void createTableForForm(BahmniForm form) {
        TableData tableData = new TableData();
        try {
            TableMetaDataGenerator generator = new TableMetaDataGenerator(form);
            tableData = generator.run();
            String sql = freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData);
            postgresJdbcTemplate.execute(sql);
            tableDataMap.put(form.getFormName().getName(), tableData);
        } catch (Exception e) {
            System.out.println("Cannot create table: " + tableData.getName() + " " + e.getMessage());
        }
    }
}
