package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
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

    public void createTables(List<TableData> tables) {
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
