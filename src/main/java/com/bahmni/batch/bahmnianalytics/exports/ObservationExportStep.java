package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.ObservationProcessor;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = "prototype")
public class ObservationExportStep {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Value("${outputFolder}")
    public Resource outputFolder;

    @Autowired
    private FreeMarkerEvaluator<BahmniForm> freeMarkerEvaluator;

    private BahmniForm form;

    @Autowired
    private ObjectFactory<ObservationProcessor> observationProcessorFactory;

    @Autowired
    private ObjectFactory<DatabaseObsWriter> databaseObsWriterObjectFactory;


    public Step getStep() {
        return stepBuilderFactory.get(getStepName())
            .<Map<String, Object>, List<Obs>>chunk(100)
            .reader(obsReader())
            .processor(observationProcessor())
            .writer(getWriter())
            .build();
    }

    private JdbcCursorItemReader<Map<String, Object>> obsReader() {
        String sql = freeMarkerEvaluator.evaluate("obsWithParentSql.ftl", form);
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(sql);
        reader.setRowMapper(new ColumnMapRowMapper());
        return reader;
    }

    private ObservationProcessor observationProcessor() {
        ObservationProcessor observationProcessor = observationProcessorFactory.getObject();
        observationProcessor.setForm(form);
        return observationProcessor;
    }


    private DatabaseObsWriter getWriter() {
        DatabaseObsWriter writer = databaseObsWriterObjectFactory.getObject();
        writer.setForm(this.form);
        return writer;
    }

    public void setForm(BahmniForm form) {
        this.form = form;
    }

    public String getStepName() {
        String formName = form.getFormName().getName();
        return formName.substring(0, Math.min(formName.length(), 100));
    }
}
