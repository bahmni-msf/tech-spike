<@compress single_line=true>
DROP VIEW IF EXISTS "${input.view_name}" CASCADE;
CREATE VIEW "${input.view_name}" as select * from
<#list input.join_table as joiningtables>
    ${joiningtables.table_name} as tableone join ${joiningtables.with_table_name} as tabletwo
    on tableone.${joiningtables.join_on_column} = tabletwo.${joiningtables.with_join_on_column}
</#list>
</@compress>