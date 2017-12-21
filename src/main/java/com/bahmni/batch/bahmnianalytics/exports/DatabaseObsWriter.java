package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
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
    public FormTableMetadataGenImpl formTableMetadataGenImpl;

    @Autowired
    private FreeMarkerEvaluator<ObsRecordExtractorForTable> freeMarkerEvaluatorForTableRecords;

    BahmniForm form;


    @Override
    public void write(List<? extends List<Obs>> items) throws Exception {
            insertRecords(items);
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }


    private void insertRecords(List<? extends List<Obs>> items)  throws  Exception {
        TableData tableData = formTableMetadataGenImpl.getTableDataForForm(this.form);
        try {
            ObsRecordExtractorForTable extractor = new ObsRecordExtractorForTable(tableData.getName());
            extractor.run(items, tableData);
            String sql = freeMarkerEvaluatorForTableRecords.evaluate("insertObs.ftl", extractor);
            postgresJdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.out.println("Cannot insert into " + tableData.getName() + " " + e.getMessage());
        }
    }
}
