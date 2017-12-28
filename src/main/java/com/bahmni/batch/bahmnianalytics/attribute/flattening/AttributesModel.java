package com.bahmni.batch.bahmnianalytics.attribute.flattening;

import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.springframework.stereotype.Component;

@Component
public class AttributesModel {

    private TableData tableData;

    private String attribute_type_table_name;
    private String attribute_table_name;
    private String attribute_type_pivot_column_name;


    public AttributesModel() {

    }

    public AttributesModel(String attribute_type_table_name, String attribute_table_name, String attribute_type_pivot_column_name) {
        this.attribute_type_table_name = attribute_type_table_name;
        this.attribute_table_name = attribute_table_name;
        this.attribute_type_pivot_column_name = attribute_type_pivot_column_name;
    }

    public String getAttribute_type_table_name() {
        return attribute_type_table_name;
    }

    public void setAttribute_type_table_name(String attribute_type_table_name) {
        this.attribute_type_table_name = attribute_type_table_name;
    }

    public String getAttribute_table_name() {
        return attribute_table_name;
    }

    public void setAttribute_table_name(String attribute_table_name) {
        this.attribute_table_name = attribute_table_name;
    }


    public String getAttribute_type_pivot_column_name() {
        return attribute_type_pivot_column_name;
    }

    public void setAttribute_type_pivot_column_name(String attribute_type_pivot_column_name) {
        this.attribute_type_pivot_column_name = attribute_type_pivot_column_name;
    }

    public TableData getTableData() {
        return tableData;
    }
}
