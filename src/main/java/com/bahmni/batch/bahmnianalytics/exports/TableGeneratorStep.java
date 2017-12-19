package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TableGeneratorStep {

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Autowired
    private FreeMarkerEvaluator<TableData> freeMarkerEvaluatorForTables;

    @Autowired
    private TableMetadataGeneratorStep tableMetadataGeneratorStep;

    public void createTables() {
        List<TableData> tables = tableMetadataGeneratorStep.getTableData();
        tables.forEach(tableData -> {
                    try {
                        String sql = freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData);
                        postgresJdbcTemplate.execute(sql);
                    } catch (Exception e) {
                        System.out.println("Cannot create table: " + tableData.getName() + " " + e.getMessage());
                    }
                }
        );
    }
}
