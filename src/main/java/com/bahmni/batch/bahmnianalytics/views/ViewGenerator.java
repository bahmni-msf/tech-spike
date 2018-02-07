package com.bahmni.batch.bahmnianalytics.views;

import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import com.bahmni.batch.bahmnianalytics.views.domain.ViewModel;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ViewGenerator {

    @Value("classpath:view_metadata.json")
    private Resource metadataJson;

    @Qualifier("postgresJdbcTemplate")
    @Autowired
    private JdbcTemplate postgresJdbcTemplate;

    @Autowired
    private FreeMarkerEvaluator<ViewModel> freeMarkerEvaluator;

    public void createView(){
        fetchViewList();
        List<ViewModel> viewModelList = fetchViewList();
        viewModelList.forEach(viewModel -> {
            String sql = freeMarkerEvaluator.evaluate("view.ftl",viewModel);
            postgresJdbcTemplate.execute(sql);
        });
    }

    private List<ViewModel> fetchViewList(){
        Gson g = new Gson();
        ViewModel[] viewModelArray = g.fromJson(BatchUtils.convertResourceOutputToString(metadataJson),ViewModel[].class);
        return Arrays.asList(viewModelArray);
    }

}
