<@compress single_line=true>
    SELECT
        <#list input.columns as column>
        ${column.name}
            <#if input.columns?seq_index_of(column) <= input.columns?size - 2 >,</#if>
        </#list>
    FROM ${input.name};
</@compress>