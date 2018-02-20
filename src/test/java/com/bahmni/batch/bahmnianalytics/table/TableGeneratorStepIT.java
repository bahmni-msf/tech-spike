package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.AbstractBaseBatchIT;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class TableGeneratorStepIT extends AbstractBaseBatchIT {

    @Autowired
    private TableGeneratorStep tableGeneratorStep;

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Test
    public void shouldCreateTableWithGivenData() {
        TableData tableData = new TableData();
        tableData.setName("tablename");
        tableData.addColumn(new TableColumn("test", "Integer", false, null));

        tableGeneratorStep.createTables(Arrays.asList(tableData));
        postgresJdbcTemplate.queryForList("SELECT * FROM \"tablename\"");

        assertEquals(1, 1); // To check postgresJdbcTemplate.queryForList didn't throw any error
    }

}