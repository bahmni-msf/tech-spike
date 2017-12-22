package com.bahmni.batch.bahmnianalytics.table;


import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Component;
import sun.misc.IOUtils;
import sun.nio.ch.IOUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

import static com.bahmni.batch.bahmnianalytics.exports.ObservationExportStep.stepNumber;

@Component
public class TablesExportStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectFactory<TableRecordWriter> recordWriterObjectFactory;

    @Autowired
    ResourceLoader resourceLoader;

    TableData tableData;

    public Step getStep() {
        return stepBuilderFactory.get(getStepName())
            .<Map<String, Object>, Map<String, Object>>chunk(100)
            .reader(getReader())
            .processor(getProcessor())
            .writer(getWriter())
            .build();
    }

    private JdbcCursorItemReader<Map<String, Object>> getReader() {
        Resource sqlResource = resourceLoader.getResource("classpath:sql/" + tableData.getName() + ".sql");
        String sql = BatchUtils.convertResourceOutputToString(sqlResource);
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }

    private TableDataProcessor getProcessor() {
        TableDataProcessor tableDataProcessor = new TableDataProcessor();
        tableDataProcessor.setTableData(tableData);
        return tableDataProcessor;
    }

    private TableRecordWriter getWriter() {
        TableRecordWriter writer = recordWriterObjectFactory.getObject();
        writer.setTableData(tableData);
        return writer;
    }

    public String getStepName() {
        stepNumber++;
        String formName = "Step-" + stepNumber + " " + tableData.getName();
        return formName.substring(0, Math.min(formName.length(), 100));
    }

    public void setTableData(TableData tableData) {
        this.tableData = tableData;
    }

}