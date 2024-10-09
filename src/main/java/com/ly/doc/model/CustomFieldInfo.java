package com.ly.doc.model;

import java.io.Serializable;

/**
 * CustomFieldInfo
 *
 * @author linwumingshi
 * @since 3.0.9
 */
public class CustomFieldInfo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7310122325722122250L;

	/**
	 * ignore
	 */
	private Boolean ignore;

	/**
	 * field name
	 */
	private String fieldName;

	/**
	 * field value
	 */
	private String fieldValue;

	/**
	 * is required
	 */
	private Boolean strRequired;

	/**
	 * comment
	 */
	private String comment;

	public Boolean getIgnore() {
		return ignore;
	}

	public CustomFieldInfo setIgnore(Boolean ignore) {
		this.ignore = ignore;
		return this;
	}

	public String getFieldName() {
		return fieldName;
	}

	public CustomFieldInfo setFieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public CustomFieldInfo setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public Boolean getStrRequired() {
		return strRequired;
	}

	public CustomFieldInfo setStrRequired(Boolean strRequired) {
		this.strRequired = strRequired;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public CustomFieldInfo setComment(String comment) {
		this.comment = comment;
		return this;
	}

}
