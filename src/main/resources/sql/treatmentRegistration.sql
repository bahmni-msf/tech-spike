SELECT
  o.identifier                                                                           AS 'id_emr',
  DATE_FORMAT(o.birthdate, '%d/%b/%Y')                                                   AS 'dob',
  o.age                                                                                  AS 'age',
  IF(o.gender = 'M', 1, IF(o.gender = 'F', 2, 3))                                        AS 'sex',
  o.program_name                                                                         AS 'tbregtype',
  MAX(IF(pat.program_attribute_type_id = '2', CONCAT('\"', o.attr_value, '\"'), NULL))   AS `regnum`,
  DATE_FORMAT(o.date_enrolled, '%d/%b/%Y')                                               AS 'd_reg',
  MAX(IF(pat.program_attribute_type_id = '6', CONCAT('\"', o.concept_name, '\"'), NULL)) AS `reg_facility`,
  o.status
FROM
  (SELECT
     CONCAT('\"', pi.identifier, '\"')             AS identifier,
     floor(datediff(CURDATE(), p.birthdate) / 365) AS age,
     p.birthdate,
     p.gender,
     CONCAT('\"', prog.name, '\"')                 AS program_name,
     attr.attribute_type_id,
     attr.value_reference                          AS attr_value,
     pp.date_enrolled                              AS date_enrolled,
     pp.patient_id,
     prog.program_id,
     cn.name                                       AS concept_name,
     CONCAT('\"', outcome_concept.name, '\"')      AS status,
     pp.patient_program_id
   FROM patient_program pp
     JOIN program prog ON pp.program_id = prog.program_id AND pp.voided = 0
     JOIN person p ON pp.patient_id = p.person_id
     JOIN person_name pn ON p.person_id = pn.person_id
     JOIN patient pa ON pp.patient_id = pa.patient_id
     JOIN patient_identifier pi ON pa.patient_id = pi.patient_id
     LEFT OUTER JOIN patient_program_attribute attr
       ON pp.patient_program_id = attr.patient_program_id AND attr.voided = 0
     LEFT OUTER JOIN program_attribute_type attr_type ON attr.attribute_type_id = attr_type.program_attribute_type_id
     LEFT OUTER JOIN concept_name cn ON cn.concept_id = attr.value_reference AND cn.voided = 0
     LEFT OUTER JOIN concept_name outcome_concept
       ON outcome_concept.concept_id = pp.outcome_concept_id AND outcome_concept.concept_name_type = 'FULLY_SPECIFIED'
          AND outcome_concept.voided = 0
  ) o
  LEFT OUTER JOIN program_attribute_type pat ON o.attribute_type_id = pat.program_attribute_type_id
GROUP BY patient_id, patient_program_id
ORDER BY patient_id, date_enrolled;
