package com.bahmni.batch.bahmnianalytics.views.domain;

public class JoinTable {
    String table_name;
    String join_on_column;
    String with_table_name;
    String with_join_on_column;

    public String getTable_name() { return table_name; }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getJoin_on_column() {
        return join_on_column;
    }

    public void setJoin_on_column(String join_on_column) {
        this.join_on_column = join_on_column;
    }

    public String getWith_table_name() {
        return with_table_name;
    }

    public void setWith_table_name(String with_table_name) {
        this.with_table_name = with_table_name;
    }

    public String getWith_join_on_column() {
        return with_join_on_column;
    }

    public void setWith_join_on_column(String with_join_on_column) {
        this.with_join_on_column = with_join_on_column;
    }

}
