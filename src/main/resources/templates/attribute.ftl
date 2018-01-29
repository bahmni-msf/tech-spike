<@compress single_line=true>
SELECT
    <#list input.tableData.columns as column>
        <#if column.name?contains('_id')>
            ${column.name}
            <#assign primary_key = column.name>
        <#else >
        MAX(if( name =  '${column.name}',
                    if  (value_table.${input.value_column_name} REGEXP '^[[:digit:]]*$' AND
                        ${getConceptName(input.value_column_name)} IS NOT NULL,
                        ${getConceptName(input.value_column_name)},
                        value_table.${input.value_column_name})
        , NULL)) AS '${column.name}'
        </#if>
        <#if input.tableData.columns?seq_index_of(column) <=  input.tableData.columns?size - 2 >,</#if>
    </#list>
FROM ${input.attribute_table_name} as value_table INNER JOIN  ${input.attribute_type_table_name} as type_table
WHERE value_table.${input.value_table_joining_id} = type_table.${input.type_table_joining_id}
GROUP BY ${primary_key} ;
</@compress>

<#function getConceptName conceptId>
    <#assign conceptName = "(select max(name) from concept_name where concept_id = value_table.${conceptId})">
    <#return conceptName>
</#function>


