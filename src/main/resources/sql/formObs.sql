SELECT
  o.encounter_id                   AS encounterId,
  o.concept_id                     AS conceptId,
  o.obs_id                         AS id,
  coalesce(DATE_FORMAT(o.value_datetime, '%d/%b/%Y'), o.value_numeric, o.value_text, cv.code, cvn.concept_full_name,
           cvn.concept_short_name) AS value,
  obs_con.concept_full_name        AS conceptName
FROM obs o
  JOIN concept_view obs_con ON o.concept_id = obs_con.concept_id
  LEFT OUTER JOIN concept codedConcept ON o.value_coded = codedConcept.concept_id
  LEFT OUTER JOIN concept_reference_term_map_view cv
    ON cv.concept_id = codedConcept.concept_id AND cv.concept_map_type_name = 'SAME-AS' AND
       cv.concept_reference_source_name = 'MSF-INTERNAL'
  LEFT OUTER JOIN concept_view cvn ON codedConcept.concept_id = cvn.concept_id
WHERE
  o.obs_id = :obsId
  AND o.voided = 0


