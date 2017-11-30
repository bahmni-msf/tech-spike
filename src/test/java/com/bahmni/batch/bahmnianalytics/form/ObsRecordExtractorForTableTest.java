package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.exports.ObsRecordExtractorForTable;
import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.form.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class ObsRecordExtractorForTableTest {

    private ObsRecordExtractorForTable obsRecordExtractorForTable;
    private TableData tableData;

    @Before
    public void setUp() throws Exception {
        String tableName = "post_operative_anaesthesia_notes";
        obsRecordExtractorForTable = new ObsRecordExtractorForTable(tableName);
        tableData = new TableData();
        tableData.setName("post_operative_anaesthesia_notes");
        TableColumn column1 = new TableColumn("id_post_operative_anaesthesia_note","integer",true,null);
        TableColumn column2 = new TableColumn("encounter_id","integer",false,null);
        TableColumn column3 = new TableColumn("apn_anaesthesia_start_time","timestamp",false,null);
        TableColumn column4 = new TableColumn("apn_anaesthesia_end_time","timestamp",false,null);
        TableColumn column5 = new TableColumn("apn_special_drug_used_during_surgery","text",false,null);
        List columnList = new ArrayList<>();
        columnList.add(column1);columnList.add(column2);columnList.add(column3);columnList.add(column4);columnList.add(column4);columnList.add(column5);
        tableData.addAllColumns(columnList);

        List<List<Obs>> recordList = new ArrayList<>();
        List<Obs> record1 = new ArrayList<>();
    }

    @Test
    public void shouldGiveRecordsForTableGivenObsAndColumns() throws Exception {

    }
}
