package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.service.ObsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class FormListProcessor {

    public static final String ALL_FORMS = "Post-operative Anaesthesia Note";

    @Autowired
    private ObsService obsService;

    @Autowired
    private BahmniFormFactory bahmniFormFactory;


    public List<BahmniForm> retrieveAllForms() {
        List<Concept> allFormConcepts = obsService.getConceptsByNames(ALL_FORMS);

        List<BahmniForm> forms = new ArrayList<>();
        for (Concept concept : allFormConcepts) {
            forms.add(bahmniFormFactory.createForm(concept, null));
        }

        List<BahmniForm> flattenedFormList = new ArrayList<>(forms);
        fetchExportFormsList(forms, flattenedFormList);
        return flattenedFormList;
    }

    private void fetchExportFormsList(List<BahmniForm> forms, List<BahmniForm> flattenedList) {
        for (BahmniForm form : forms) {
            if (form.getChildren().size() != 0) {
                flattenedList.addAll(form.getChildren());
                fetchExportFormsList(form.getChildren(), flattenedList);
            }
        }
    }

    public void setObsService(ObsService obsService) {
        this.obsService = obsService;
    }

    public void setBahmniFormFactory(BahmniFormFactory bahmniFormFactory) {
        this.bahmniFormFactory = bahmniFormFactory;
    }
}
