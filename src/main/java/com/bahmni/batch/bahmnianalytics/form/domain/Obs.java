package com.bahmni.batch.bahmnianalytics.form.domain;

public class Obs {
	private String encounterId;
	private String patientId;
	private Integer id;
	private Integer parentId;
	private Concept field;
	private String value;
	private String parentName;

	public Obs(){}

	public Obs(String patientId,String encounterId, Integer id, Integer parentId, Concept field, String value) {
		this.patientId = patientId;
		this.encounterId = encounterId;
		this.id = id;
		this.parentId = parentId;
		this.field = field;
		this.value = value;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Concept getField() {
		return field;
	}

	public void setField(Concept field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
