package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.attribute.flattening.AttributesModel;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.bahmni.batch.bahmnianalytics.util.BatchUtils.stepNumber;

@Component
public class AttributeTableExportStep  extends TablesExportStep {
    private AttributesModel attributesModel;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private FreeMarkerEvaluator<AttributesModel> attributesModelFreeMarkerEvaluator;


    public AttributeTableExportStep() {
        attributesModel = new AttributesModel();
    }

    public AttributesModel getAttributesModel() {
        return attributesModel;
    }

    public void setAttributesModel(AttributesModel attributesModel) {
        this.attributesModel = attributesModel;
    }

    @Override
    public Step getStep() {
        return stepBuilderFactory.get(getStepName())
                .<Map<String, Object>, Map<String, Object>>chunk(100)
                .reader(getReader())
                .processor(getProcessor())
                .writer(getWriter())
                .build();
    }

    @Override
    public String getStepName() {
        stepNumber++;
        String formName = "Step-" + stepNumber + " " + attributesModel.getTableData().getName();
        return formName.substring(0, Math.min(formName.length(), 100));
    }

    @Override
    protected TableData getTableData() {
        return attributesModel.getTableData();
    }

    @Override
    protected String getJDBCReaderSqlString() {
        return attributesModelFreeMarkerEvaluator.evaluate("attribute.ftl", attributesModel);
    }
}
