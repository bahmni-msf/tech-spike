SELECT
  c.concept_id                                                   AS id,
  cn.concept_full_name                                           AS name,
  cdt.name                                                       AS dataType,
  c.is_set                                                       AS isset,
  COALESCE(cv.code, cn.concept_full_name, cn.concept_short_name) AS title
FROM concept c
  INNER JOIN concept_datatype cdt ON c.datatype_id = cdt.concept_datatype_id AND cdt.retired IS FALSE
  LEFT OUTER JOIN concept_reference_term_map_view cv
    ON cv.concept_id = c.concept_id AND cv.concept_map_type_name = 'SAME-AS' AND
       cv.concept_reference_source_name = 'EndTB-Export'
  LEFT OUTER JOIN concept_view cn ON cn.concept_id = c.concept_id
WHERE c.concept_id IN (
  SELECT DISTINCT obs.concept_id
  FROM obs
  WHERE obs.obs_group_id IS NOT NULL AND
        obs.voided IS FALSE
  GROUP BY obs.obs_group_id,
    obs.concept_id
  HAVING count(*) > 1)
