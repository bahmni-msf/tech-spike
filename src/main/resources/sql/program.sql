SELECT
  p.program_id   AS program_id,
  pcn.name       AS concept_name,
  ocn.name       AS outcome_concept_name,
  p.date_created AS date_created,
  p.date_changed AS date_changed
FROM program p
  LEFT JOIN concept_name pcn ON pcn.concept_id = p.concept_id
  LEFT JOIN concept_name ocn ON ocn.concept_id = p.outcomes_concept_id
