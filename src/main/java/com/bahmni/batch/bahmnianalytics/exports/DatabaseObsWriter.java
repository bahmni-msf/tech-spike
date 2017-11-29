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

    BahmniForm form;

    @Override
    public void write(List<? extends List<Obs>> items) throws Exception {
        try {
            createTableForForm();
            System.out.println(items.size());
        } catch (Exception e) {
            System.out.println("Cannot create table" + e.getMessage());
        }
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }

    private void createTableForForm() throws Exception {
        TableMetaDataGenerator generator = new TableMetaDataGenerator(this.form);
        TableData tableData = generator.run();
        String sql = freeMarkerEvaluatorForTables.evaluate("ddlForForm.ftl", tableData);
        System.out.println("Form name is *** " + this.form.getFormName().getName());
        postgresJdbcTemplate.execute(sql);
    }

}
