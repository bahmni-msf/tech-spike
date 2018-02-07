package com.bahmni.batch.bahmnianalytics.views.domain;

import java.util.List;

public class ViewModel {
    private String view_name;
    private List<JoinTable> join_table;


    public String getView_name() {
        return view_name;
    }

    public void setView_name(String view_name) {
        this.view_name = view_name;
    }

    public List<JoinTable> getJoin_table() {
        return join_table;
    }

    public void setJoin_table(List<JoinTable> join_table) {
        this.join_table = join_table;
    }
}
