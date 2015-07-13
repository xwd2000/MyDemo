package com.hikvision.parentdotworry.bean;

import java.io.Serializable;

import com.hikvision.parentdotworry.bean.interf.FromDb;

public class ChildInfo implements FromDb,Serializable{
	
	private static final long serialVersionUID = 7844572870628655740L;
	
	private Integer id;
	private String name;
	private String personCode;
	private String schoolName;
	private String gradeName;
	private String className;
	private String parentPhone;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPersonCode() {
		return personCode;
	}

	public void setPersonCode(String personCode) {
		this.personCode = personCode;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParentPhone() {
		return parentPhone;
	}

	public void setParentPhone(String parentPhone) {
		this.parentPhone = parentPhone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return sb.append("id:")
				.append(id)
				.append("name:")
				.append(name)
				.append("schoolName:")
				.append(schoolName)
				.append("gradeName")
				.append(gradeName)
				.append("className")
				.append(className)
				.append("parentPhone")
				.append(parentPhone)
				.append("personCode")
				.append(personCode).toString();
	}
	
	
	
}
