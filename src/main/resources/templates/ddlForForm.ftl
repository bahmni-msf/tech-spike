SELECT obs0.obs_id,obs${input.depthToParent}.obs_id as parent_obs_id
FROM obs obs0
<#if input.depthToParent &gt; 0>
<#list 1..input.depthToParent as x>
INNER JOIN obs obs${x} on ( obs${x}.obs_id=obs${x-1}.obs_group_id and obs${x}.voided=0 )
</#list>
</#if>
WHERE obs0.concept_id=${input.formName.id?c}
AND obs0.voided = 0
<#if input.parent?has_content>
AND obs${input.depthToParent}.concept_id=${input.parent.formName.id?c}
</#if>



<#if input.fields &gt; 0>
CREATE TABLE ${input.formName.name?c}(
    <#list input.fields as field>
        <#if input.parent?has_content>
            ${input.parent.formName.name?c} ${field.dataType} FOREIGN KEY
        </#if>

        ${field.name} ${field.dataType}
    </#list>
);
</#if>







CREATE TABLE COMPANY(

ID INT PRIMARY KEY     NOT NULL,
NAME           TEXT    NOT NULL,
AGE            INT     NOT NULL,
ADDRESS        CHAR(50),
SALARY         REAL

);


keys
dataType conversions
