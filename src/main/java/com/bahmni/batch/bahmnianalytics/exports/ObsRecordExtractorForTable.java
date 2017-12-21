package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator.getProcessedName;

public class ObsRecordExtractorForTable {

    private String tableName = "";

    private List<Map<String, String>> recordList = new ArrayList<>();

    public ObsRecordExtractorForTable(String tableName) {
        this.tableName = tableName;
    }

    public void run(List<? extends List<Obs>> items, TableData tableData) {

        for (List<Obs> record : items) {

            if(record.size() == 0) {
                continue;
            }

            Map<String, String> recordMap = new HashMap<>();

            tableData.getColumns().forEach(tableColumn -> recordMap.put(tableColumn.getName(), null));

            record.stream().forEach(obs -> {
                final String[] value = {""};
                tableData.getColumns().forEach(tableColumn -> {
                    String tableColumnName = tableColumn.getName();
                    if (tableColumnName.equals(getProcessedName(obs.getField().getName()))) {
                        value[0] = BatchUtils.getPostgresCompatibleValue(obs.getValue(), tableColumn.getType());
                        recordMap.replace(tableColumnName, value[0]);
                    }
                });
            });

            tableData.getColumns().forEach(tableColumn -> {
                final String[] value = {""};
                Obs obs = record.get(0);
                String tableColumnName = tableColumn.getName();
                if (tableColumnName.contains("id_") && recordMap.get(tableColumnName) == null) {
                    if (tableColumn.getReference() != null && obs.getParentName() != null && tableColumnName.equals("id_"+getProcessedName(obs.getParentName()))) {
                        value[0] = BatchUtils.getPostgresCompatibleValue(obs.getParentId().toString(),tableColumn.getType());
                        recordMap.replace(tableColumn.getName(), value[0]);
                    } else if(tableColumn.getReference() == null){
                        value[0] = BatchUtils.getPostgresCompatibleValue(obs.getId().toString(),tableColumn.getType());
                        recordMap.replace(tableColumn.getName(), value[0]);
                    }
                }
                else if (tableColumnName.contains("encounter") && recordMap.get(tableColumnName) == null) {
                    value[0] = BatchUtils.getPostgresCompatibleValue(obs.getEncounterId(), tableColumn.getType());
                    recordMap.replace(tableColumnName, value[0]);
                }
                else if (tableColumnName.contains("patient") && recordMap.get(tableColumnName) == null) {
                    value[0] = BatchUtils.getPostgresCompatibleValue(obs.getPatientId(), tableColumn.getType());
                    recordMap.replace(tableColumnName, value[0]);
                }
            });
            recordList.add(recordMap);
        }
    }

    public List<Map<String, String>> getRecordList() {
        return recordList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRecordList(List<Map<String, String>> recordList) {
        this.recordList = recordList;
    }


}
