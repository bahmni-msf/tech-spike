package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.domain.Obs;
import com.bahmni.batch.bahmnianalytics.helper.FreeMarkerEvaluator;
import com.bahmni.batch.bahmnianalytics.util.BatchUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Scope(value="prototype")
public class ObservationProcessor implements ItemProcessor<Map<String,Object>, List<Obs>> {

	private String obsDetailSql;

	private String leafObsSql;

	private String formObsSql;

	private BahmniForm form;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Value("classpath:sql/obsDetail.sql")
	private Resource obsDetailSqlResource;

	@Value("classpath:sql/leafObs.sql")
	private Resource leafObsSqlResource;

	@Value("classpath:sql/formObs.sql")
	private Resource formObsSqlResource;

	@Value("${flag}")
	private String flag;

	@Autowired
	private FormFieldTransformer formFieldTransformer;

	@Autowired
	private FreeMarkerEvaluator<BahmniForm> freeMarkerEvaluator;

	@Override
	public List<Obs> process(Map<String,Object> obsRow) throws Exception {
		List<Integer> allChildObsIds = new ArrayList<>();

		if (form.getFormName().getIsSet() == 1) {
			retrieveChildObsIds(allChildObsIds, Arrays.asList((Integer)obsRow.get("obs_id")));
		}
		else
			allChildObsIds.add((Integer)obsRow.get("obs_id"));

			List<Obs> obsRows = fetchAllLeafObs(allChildObsIds);

			obsRows.addAll(formObs((Integer) obsRow.get("obs_id")));

		setObsIdAndParentObsId(obsRows,(Integer)obsRow.get("obs_id"), (Integer)obsRow.get("parent_obs_id"));

		return obsRows;
	}

	private List<Obs> formObs(Integer obsId) {
		Map<String,Integer> params = new HashMap<>();
		params.put("obsId",obsId );
		return jdbcTemplate.query(formObsSql, params, new BeanPropertyRowMapper<Obs>(Obs.class) {
			@Override
			public Obs mapRow(ResultSet resultSet, int i) throws SQLException {
				Obs obs = super.mapRow(resultSet, i);
				Concept concept = new Concept(resultSet.getInt("conceptId"), resultSet.getString("conceptName"), 0, "");
				obs.setParentName(resultSet.getString("parentConceptName"));
				obs.setField(concept);
				return obs;
			}
		});
	}

	private List<Obs> fetchAllLeafObs(List<Integer> allChildObsGroupIds) {
		Map<String,List<Integer>> params = new HashMap<>();
		List<Integer> leafConcepts = formFieldTransformer.transformFormToFieldIds(form);

		if(allChildObsGroupIds.size() > 0 && leafConcepts.size() > 0) {
			params.put("childObsIds", allChildObsGroupIds);
			params.put("leafConceptIds", leafConcepts);

			if(Boolean.parseBoolean(flag)) {
				return jdbcTemplate.query(leafObsSql, params, new BeanPropertyRowMapper<Obs>(Obs.class) {
					@Override
					public Obs mapRow(ResultSet resultSet, int i) throws SQLException {
						Obs obs = super.mapRow(resultSet, i);
						Concept concept = new Concept(resultSet.getInt("conceptId"), resultSet.getString("conceptName"), 0, "");
						obs.setParentName(resultSet.getString("parentConceptName"));
						obs.setField(concept);
						return obs;
					}
				});
			} else {
				String leafObsWithConfigSql = freeMarkerEvaluator.evaluate("leafObsWithConfig.ftl",form);
				return jdbcTemplate.query(leafObsWithConfigSql, params, new BeanPropertyRowMapper<Obs>(Obs.class) {
					@Override
					public Obs mapRow(ResultSet resultSet, int i) throws SQLException {
						Obs obs = super.mapRow(resultSet, i);
						Concept concept = new Concept(resultSet.getInt("conceptId"), resultSet.getString("conceptName"), 0, "");
						obs.setParentName(resultSet.getString("parentConceptName"));
						obs.setField(concept);
						return obs;
					}
				});
			}
		}
		return  new ArrayList<>();
	}

	protected void retrieveChildObsIds(List<Integer> allChildObsIds, List<Integer> ids){

		Map<String,List<Integer>> params = new HashMap<>();
		params.put("parentObsIds",ids);

		List<Map<String, Object>> results = jdbcTemplate.query(obsDetailSql,params,new ColumnMapRowMapper());
		List<Integer> obsGroupIds = new ArrayList<>();
		for(Map res : results){
			if((boolean)res.get("isSet"))
				obsGroupIds.add((Integer) res.get("obsId"));
			else{
				allChildObsIds.add((Integer) res.get("obsId"));
			}
		}
		if (!obsGroupIds.isEmpty()){
			retrieveChildObsIds(allChildObsIds, obsGroupIds);
		}

	}

	public void setForm(BahmniForm form) {
		this.form = form;
	}

	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setObsDetailSqlResource(Resource obsDetailSqlResource) {
		this.obsDetailSqlResource = obsDetailSqlResource;
	}

	public void setLeafObsSqlResource(Resource leafObsSqlResource) {
		this.leafObsSqlResource = leafObsSqlResource;
	}

	public void setFormObsSqlResource(Resource formObsSqlResource) {
		this.formObsSqlResource = formObsSqlResource;
	}

	public void setFormFieldTransformer(FormFieldTransformer formFieldTransformer) {
		this.formFieldTransformer = formFieldTransformer;
	}

	@PostConstruct
	public void postConstruct(){
		this.obsDetailSql = BatchUtils.convertResourceOutputToString(obsDetailSqlResource);
		this.leafObsSql = BatchUtils.convertResourceOutputToString(leafObsSqlResource);
		this.formObsSql = BatchUtils.convertResourceOutputToString(formObsSqlResource);
	}

	public void setObsIdAndParentObsId(List<Obs> childObs, Integer obsId,Integer parentObsId) {
		for(Obs child: childObs){
			child.setParentId(parentObsId);
			child.setId(obsId);
		}
	}
}
