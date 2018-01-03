package com.bahmni.batch.bahmnianalytics.attribute.flattening;

import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import org.springframework.stereotype.Component;

@Component
public class AttributesModel {

    private TableData tableData;

    private String attribute_type_table_name;
    private String attribute_table_name;
    private String attribute_type_pivot_column_name;
    private String value_table_joining_id;
    private String type_table_joining_id;


    public AttributesModel() {

    }

    public AttributesModel(String attribute_type_table_name, String attribute_table_name, String attribute_type_pivot_column_name) {
        this.attribute_type_table_name = attribute_type_table_name;
        this.attribute_table_name = attribute_table_name;
        this.attribute_type_pivot_column_name = attribute_type_pivot_column_name;
    }

    public String getValue_table_joining_id() {
        return value_table_joining_id;
    }

    public void setValue_table_joining_id(String value_table_joining_id) {
        this.value_table_joining_id = value_table_joining_id;
    }

    public String getType_table_joining_id() {
        return type_table_joining_id;
    }

    public void setType_table_joining_id(String type_table_joining_id) {
        this.type_table_joining_id = type_table_joining_id;
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
