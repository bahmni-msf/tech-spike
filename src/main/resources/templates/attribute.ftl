<@compress single_line=true>
SELECT
    <#list input.tableData.columns as column>
        <#if column.name?contains('_id')>
        ${column.name}
        <#else >
        MAX(if( name =  '${column.name}', value_table.value_reference, NULL)) AS '${column.name}'
        </#if>
        <#if input.tableData.columns?seq_index_of(column) <=  input.tableData.columns?size - 2 >,</#if>
    </#list>
FROM ${input.attribute_table_name} as value_table INNER JOIN  ${input.attribute_type_table_name} as type_table
WHERE value_table.attribute_type_id = type_table.visit_attribute_type_id
GROUP BY value_table.visit_id ;
</@compress>

