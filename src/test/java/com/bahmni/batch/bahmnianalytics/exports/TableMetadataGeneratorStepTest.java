package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.form.TableGeneratorFactory;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.form.domain.TableData;
import com.bahmni.batch.bahmnianalytics.util.TableMetaDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class TableMetadataGeneratorStepTest {

    @Mock
    private TableGeneratorFactory factory;

    @Mock
    private TableMetaDataGenerator generator;

    private TableMetadataGeneratorStep tableMetadataGeneratorStep;

    @Before
    public void setUp() throws Exception {
        tableMetadataGeneratorStep = new TableMetadataGeneratorStep();
        tableMetadataGeneratorStep.setGeneratorFactory(factory);
    }

    @Test
    public void generateTableDataForANewFrom() throws Exception {
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123, "formName", 1));
        TableData tableData = new TableData();
        tableData.setName("formName");
        Map<String, TableData> tableDataMap = new LinkedHashMap<>();
        tableMetadataGeneratorStep.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForNewForm(form)).thenReturn(generator);
        when(generator.run()).thenReturn(tableData);

        tableMetadataGeneratorStep.generateTableDataForForm(form);

        assertEquals(1, tableDataMap.size());
        assertTrue(tableDataMap.keySet().contains(form.getFormName().getName()));
        assertTrue(tableDataMap.get(form.getFormName().getName()).equals(tableData));
    }

    @Test
    public void addsNewForiegnKeyForSameFormWithDifferentParent() throws Exception {
        BahmniForm form = new BahmniForm();
        String formName = "formName";
        form.setFormName(new Concept(123, formName, 1));
        TableData tableData = new TableData();
        tableData.setName(formName);
        TableColumn tableColumn = new TableColumn("id_" + formName, "String", true, null);
        tableData.setColumns(Arrays.asList(tableColumn));
        Map<String, TableData> tableDataMap = new LinkedHashMap<>();
        tableDataMap.put(formName, tableData);
        tableMetadataGeneratorStep.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForExistingForm(form, tableData)).thenReturn(generator);

        tableMetadataGeneratorStep.generateTableDataForForm(form);

        verify(generator, times(1)).addForeignKey();
        assertEquals(1, tableDataMap.size());
        assertTrue(tableDataMap.keySet().contains(form.getFormName().getName()));
        assertTrue(tableDataMap.get(form.getFormName().getName()).equals(tableData));
    }

    @Test
    public void addsTheNewlyModifiedTableDataEntryAtTheEnd() throws Exception {
        Map<String,TableData> tableDataMap = new LinkedHashMap<>();
        tableDataMap.put("firstForm",new TableData());
        tableDataMap.put("secondForm",new TableData());
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123,"firstForm",1));
        tableMetadataGeneratorStep.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForExistingForm(any(),any())).thenReturn(generator);

        tableMetadataGeneratorStep.generateTableDataForForm(form);

        verify(generator,times(1)).addForeignKey();
        assertEquals(tableDataMap.size(),2);
        assertEquals(tableDataMap.keySet().toArray()[1],"firstForm");
        assertEquals(tableDataMap.keySet().toArray()[0],"secondForm");
    }

    @Test
    public void name() throws Exception {

    }
}