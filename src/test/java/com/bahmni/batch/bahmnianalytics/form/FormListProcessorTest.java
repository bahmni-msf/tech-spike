package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.service.ObsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormListProcessorTest {

    @Mock
    private BahmniFormFactory bahmniFormFactory;

    @Mock
    private ObsService obsService;

    private FormListProcessor formListProcessor;

    @Before
    public void setup() {
        initMocks(this);
        formListProcessor = new FormListProcessor();
        formListProcessor.setObsService(obsService);
        formListProcessor.setBahmniFormFactory(bahmniFormFactory);
    }

        @Test
    public void shouldRetrieveAllFormsWhenFlagIsTrue() throws NoSuchFieldException, IllegalAccessException {
        Concept conceptA = new Concept(1, "a", 1);
        List<Concept> conceptList = new ArrayList();
        conceptList.add(conceptA);
        setValuesForMemberFields(formListProcessor,"flag","true");

        when(obsService.getConceptsByNames(FormListProcessor.ALL_FORMS)).thenReturn(conceptList);

        BahmniForm a11 = new BahmniFormBuilder().withName("a11").build();
        BahmniForm a12 = new BahmniFormBuilder().withName("a12").build();
        BahmniForm a13 = new BahmniFormBuilder().withName("a13").build();


        BahmniForm b11 = new BahmniFormBuilder().withName("b11").build();
        BahmniForm b12 = new BahmniFormBuilder().withName("b12").build();
        BahmniForm b13 = new BahmniFormBuilder().withName("b13").build();

        BahmniForm a1 = new BahmniFormBuilder().withName("a1").withChild(a11).withChild(a12).withChild(a13).build();
        BahmniForm b1 = new BahmniFormBuilder().withName("b1").withChild(b11).withChild(b12).withChild(b13).build();

        BahmniForm a = new BahmniFormBuilder().withName("a").withChild(a1).withChild(b1).build();


        when(bahmniFormFactory.createForm(conceptA, null)).thenReturn(a);

        List<BahmniForm> expected = Arrays.asList(a, a1, b1, a11, a12, a13, b11, b12, b13);

        List<BahmniForm> actual = formListProcessor.retrieveAllForms();

        assertEquals(expected.size(), actual.size());
        assertEquals(new HashSet(expected), new HashSet(actual));
        verify(obsService).getConceptsByNames(FormListProcessor.ALL_FORMS);
    }

    @Test
    public void shouldRetrieveAllFormsWhenFlagIsFalse() throws NoSuchFieldException, IllegalAccessException {
        Concept allObservation = new Concept(1, "All Observstion Templates", 1);
        Concept formOneConcept = new Concept(2, "form one", 1, "form one", allObservation);
        Concept formTwoConcept = new Concept(3, "form two", 1, "form two", allObservation);

        Concept formOneField1 = new Concept(4, "form one field 1", 0, "form one field 1", formOneConcept);
        Concept formOneField2 = new Concept(5, "form one field 2", 1, "form one field 2", formOneConcept);
        Concept formTwoField = new Concept(6, "form two field", 0, "form two field", formTwoConcept);

        List<Concept> conceptList = new ArrayList();
        conceptList.add(formOneConcept);
        conceptList.add(formTwoConcept);

        when(obsService.getChildConcepts(FormListProcessor.ALL_FORMS)).thenReturn(conceptList);
        setValuesForMemberFields(formListProcessor,"flag","false");

        BahmniForm formOne = new BahmniFormBuilder().withName("form one").withField(formOneField1).withField(formOneField2).build();
        BahmniForm formTwo = new BahmniFormBuilder().withName("form two").withField(formTwoField).build();

        when(bahmniFormFactory.createForm(formOneConcept, null)).thenReturn(formOne);
        when(bahmniFormFactory.createForm(formTwoConcept, null)).thenReturn(formTwo);


        List<BahmniForm> expectedForms = Arrays.asList(formOne, formTwo);
        List<BahmniForm> actual = formListProcessor.retrieveAllForms();

        assertEquals(expectedForms.size(), actual.size());
        assertEquals(new HashSet(expectedForms), new HashSet(actual));
        assertEquals(formOne.getFields().size(), actual.get(0).getFields().size());
        verify(obsService).getChildConcepts(FormListProcessor.ALL_FORMS);

    }

    private void setValuesForMemberFields(Object batchConfiguration, String fieldName, Object valueForMemberField) throws NoSuchFieldException, IllegalAccessException {
        Field f1 = batchConfiguration.getClass().getDeclaredField(fieldName);
        f1.setAccessible(true);
        f1.set(batchConfiguration, valueForMemberField);
    }

}
