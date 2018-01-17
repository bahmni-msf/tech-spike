<@compress single_line=true>
    <#assign parent_obs =0>

SELECT
  obs0.encounter_id                   AS encounterId,
  obs0.person_id                      AS patientId,
  obs0.concept_id                     AS conceptId,
  obs0.obs_id                         AS id,
  coalesce(DATE_FORMAT(obs0.value_datetime, '%d/%b/%Y'), obs0.value_numeric, obs0.value_text, cvn.concept_full_name,
           cvn.concept_short_name) AS value,
  obs_con.concept_full_name        AS conceptName,
  parent_obs_con.concept_full_name AS parentConceptName
FROM  obs obs0
    JOIN concept_view obs_con ON obs0.concept_id = obs_con.concept_id
    <#if input.depthToParent &gt; 0>
        <#list 1..input.depthToParent + 1 as x>
            LEFT OUTER JOIN obs obs${x} on obs${x}.obs_id = obs${x-1}.obs_group_id and obs${x}.voided = 0
            <#assign parent_concept_id>  obs${x}.concept_id </#assign>
            <#assign parent_obs =x >
        <#--<#if input.parent.formName.id == parent_concept_id>
            <#break>
        </#if>-->
        </#list>
    </#if>
  LEFT OUTER JOIN concept_view parent_obs_con ON obs${parent_obs}.concept_id = parent_obs_con.concept_id
  LEFT OUTER JOIN concept codedConcept ON obs0.value_coded = codedConcept.concept_id
  LEFT OUTER JOIN concept_view cvn ON codedConcept.concept_id = cvn.concept_id
WHERE
  obs0.obs_id IN (:childObsIds)
  AND obs_con.concept_id IN (:leafConceptIds)
  AND obs0.voided = 0
</@compress>
