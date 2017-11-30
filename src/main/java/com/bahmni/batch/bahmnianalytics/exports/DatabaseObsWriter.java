package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(value = "prototype")
public class DatabaseObsWriter implements ItemWriter<List<Obs>> {

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Autowired
    private FreeMarkerEvaluator<TableData> freeMarkerEvaluatorForTables;

    @Autowired
    private FreeMarkerEvaluator<ObsRecordExtractorForTable> freeMarkerEvaluatorForTableRecords;

    BahmniForm form;

    TableData tableData;

    @Override
    public void write(List<? extends List<Obs>> items) throws Exception {
        try {
            createTableForForm();
            insertRecords(items);
        } catch (Exception e) {
            System.out.println("Cannot create table" + e.getMessage());
        }
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }

    private void createTableForForm()  {
        TableMetaDataGenerator generator = new TableMetaDataGenerator(this.form);
        tableData = generator.run();
        String sql = freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData);
        postgresJdbcTemplate.execute(sql);
    }

    private void insertRecords(List<? extends List<Obs>> items) {
        ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable(tableData.getName());
        extractor.run(items,tableData);
        String sql = freeMarkerEvaluatorForTableRecords.evaluate("insertObs.ftl", extractor);
        postgresJdbcTemplate.execute(sql);
    }
}
