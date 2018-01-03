package com.bahmni.batch.bahmnianalytics.attribute.flattening;

import com.bahmni.batch.bahmnianalytics.table.TableMetadataGenerator;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AttributeFlattener implements TableMetadataGenerator {

    @Qualifier("mysqlJdbcTemplate")
    @Autowired
    private JdbcTemplate mysqlJdbcTemplate;

    List<TableData> tables =  new ArrayList<>();

    @Value("classpath:attributes_metadata.json")
    private Resource metadataJson;

    private List<AttributesModel> attributesModelList;


    public List<TableData> getTableData() {
        return tables;
    }

    public void run() {
        attributesModelList = fetchAttributeModelList();

        attributesModelList.forEach( attributeModel -> {
            String sql =  "select " + attributeModel.getAttribute_type_pivot_column_name() + " from " + attributeModel.getAttribute_type_table_name() + ";";
            List<String> pivotColumns = mysqlJdbcTemplate.queryForList(sql,String.class);

            TableData tableData = attributeModel.getTableData();

            pivotColumns.forEach( columnTitle -> {
                TableColumn column = new TableColumn(columnTitle,"text",false,null);
                tableData.addColumn(column);
            });
            tables.add(tableData);
        });
    }

    private List<AttributesModel> fetchAttributeModelList(){
        Gson g = new Gson();
        AttributesModel[] attributesModelArray = g.fromJson(BatchUtils.convertResourceOutputToString(metadataJson),AttributesModel[].class);
        return Arrays.asList(attributesModelArray);
    }

    public List<AttributesModel> getAttributeModelList(){
       return  attributesModelList;
    }



}
