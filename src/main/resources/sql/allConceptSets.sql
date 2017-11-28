SELECT
  c.concept_id                                                   AS id,
  cn.concept_full_name                                           AS name,
  c.is_set                                                       AS isset,
  COALESCE(cv.code, cn.concept_full_name, cn.concept_short_name) AS title
FROM concept c
  LEFT OUTER JOIN concept_reference_term_map_view cv
    ON cv.concept_id = c.concept_id AND cv.concept_map_type_name = 'SAME-AS' AND
       cv.concept_reference_source_name = 'EndTB-Export'
  LEFT OUTER JOIN concept_view cn ON cn.concept_id = c.concept_id
WHERE c.is_set = 1