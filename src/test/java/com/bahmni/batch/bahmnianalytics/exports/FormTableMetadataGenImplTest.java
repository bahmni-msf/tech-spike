package com.bahmni.batch.bahmnianalytics.exports;

import com.bahmni.batch.bahmnianalytics.table.TableGeneratorFactory;
import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.table.domain.TableColumn;
import com.bahmni.batch.bahmnianalytics.table.domain.TableData;
import com.bahmni.batch.bahmnianalytics.form.FormTableMetaDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class FormTableMetadataGenImplTest {

    @Mock
    private TableGeneratorFactory factory;

    @Mock
    private FormTableMetaDataGenerator generator;

    private FormTableMetadataGenImpl formTableMetadataGenImpl;

    @Before
    public void setUp() throws Exception {
        formTableMetadataGenImpl = new FormTableMetadataGenImpl();
        formTableMetadataGenImpl.setGeneratorFactory(factory);
    }

    @Test
    public void generateTableDataForANewFrom() throws Exception {
        BahmniForm form = new BahmniForm();
        form.setFormName(new Concept(123, "formName", 1));
        TableData tableData = new TableData();
        tableData.setName("formName");
        Map<String, TableData> tableDataMap = new LinkedHashMap<>();
        formTableMetadataGenImpl.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForNewForm(any(),anyBoolean(),anyList())).thenReturn(generator);
        when(generator.run()).thenReturn(tableData);

        formTableMetadataGenImpl.generateTableDataForForm(form);

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
        formTableMetadataGenImpl.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForExistingForm(any(), anyBoolean(),anyList(),any())).thenReturn(generator);

        formTableMetadataGenImpl.generateTableDataForForm(form);

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
        formTableMetadataGenImpl.setTableDataMap(tableDataMap);
        when(factory.getGeneratorForExistingForm(any(),anyBoolean(),anyList(),any())).thenReturn(generator);

        formTableMetadataGenImpl.generateTableDataForForm(form);

        verify(generator,times(1)).addForeignKey();
        assertEquals(tableDataMap.size(),2);
        assertEquals(tableDataMap.keySet().toArray()[1],"firstForm");
        assertEquals(tableDataMap.keySet().toArray()[0],"secondForm");
    }

    @Test
    public void name() throws Exception {

    }
}