package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AsIsTableGeneratorImpl implements TableMetadataGenerator {

    private List<TableData> tables;

    @Value("classpath:metadata.json")
    private Resource metadataJson;

    @Override
    public List<TableData> getTableData() {
        return tables;
    }

    public void run() {
        Gson g = new Gson();
        TableData[] tablesData = g.fromJson(BatchUtils.convertResourceOutputToString(metadataJson),TableData[].class);
        tables = Arrays.asList(tablesData);
    }
}
