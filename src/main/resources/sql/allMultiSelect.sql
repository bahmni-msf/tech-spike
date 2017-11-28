select
c.concept_id                                                          as id,
cn.concept_full_name                                                  as name,
cdt.name                                                              as dataType,
c.is_set                                                              as isset,
COALESCE (cv.code, cn.concept_full_name, cn.concept_short_name)       as title
From concept  c
INNER JOIN concept_datatype cdt ON c.datatype_id = cdt.concept_datatype_id AND cdt.retired IS FALSE
left outer join concept_reference_term_map_view cv on cv.concept_id = c.concept_id  and cv.concept_map_type_name = 'SAME-AS' and cv.concept_reference_source_name = 'EndTB-Export'
left outer join concept_view  cn on cn.concept_id = c.concept_id
WHERE c.concept_id in (
SELECT DISTINCT
  obs.concept_id
FROM obs
WHERE obs.obs_group_id IS NOT NULL AND
      obs.voided IS FALSE
GROUP BY  obs.obs_group_id,
          obs.concept_id
HAVING count(*) > 1)
