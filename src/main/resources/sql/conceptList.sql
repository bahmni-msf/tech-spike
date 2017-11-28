select
conceptSet.concept_id                                         as id,
cn.concept_full_name                                           as name,
cdt.name                                                       AS dataType,
conceptSet.is_set                                          as isset,
COALESCE (cv.code, cn.concept_full_name ,cn.concept_short_name) as title
from concept_view parentConcept
  inner join concept_set setMembers on setMembers.concept_set = parentConcept.concept_id
  inner join concept conceptSet on conceptSet.concept_id = setMembers.concept_id
  INNER JOIN concept_datatype cdt ON conceptSet.datatype_id = cdt.concept_datatype_id AND cdt.retired IS FALSE
  left outer join concept_reference_term_map_view cv on (cv.concept_id = conceptSet.concept_id  and cv.concept_map_type_name = 'SAME-AS' and cv.concept_reference_source_name = 'EndTB-Export')
  left outer join concept_view  cn on (cn.concept_id = conceptSet.concept_id)
where parentConcept.concept_full_name = :parentConceptName and parentConcept.retired IS FALSE
order by setMembers.sort_weight;