select
c.concept_id                                                          as id,
cn.concept_full_name                                                  as name,
c.is_set                                                              as isset,
COALESCE (cv.code, cn.concept_full_name, cn.concept_short_name)       as title
from concept c
  left outer join concept_reference_term_map_view cv on cv.concept_id = c.concept_id  and cv.concept_map_type_name = 'SAME-AS' and cv.concept_reference_source_name = 'EndTB-Export'
  left outer join concept_view  cn on cn.concept_id = c.concept_id
where c.is_set = 1