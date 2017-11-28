select cn.concept_id as id,cn.name,c.is_set as isSet,
  cdt.name AS dataType
from concept_name cn
  inner join concept c on cn.concept_id = c.concept_id
  INNER JOIN concept_datatype cdt ON c.datatype_id = cdt.concept_datatype_id AND cdt.retired IS FALSE
where cn.name in (:conceptNames) and cn.concept_name_type='FULLY_SPECIFIED' and cn.voided=0;
