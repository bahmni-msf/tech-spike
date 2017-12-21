package com.bahmni.batch.bahmnianalytics.table;

import com.bahmni.batch.bahmnianalytics.table.domain.TableData;

import java.util.List;

/**
 * Created by rajashrk on 12/19/17.
 */
public interface TableMetadataGenerator {
    List<TableData> getTableData();
}
