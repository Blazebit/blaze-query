/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.devops.model;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Stores a set of named profile attributes.
 */
@JsonPropertyOrder({
AttributesContainer.JSON_PROPERTY_ATTRIBUTES,
AttributesContainer.JSON_PROPERTY_CONTAINER_NAME,
AttributesContainer.JSON_PROPERTY_REVISION
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.10.0")
public class AttributesContainer {
public static final String JSON_PROPERTY_ATTRIBUTES = "attributes";
@jakarta.annotation.Nullable
private Map<String, ProfileAttribute> attributes = new HashMap<>();

public static final String JSON_PROPERTY_CONTAINER_NAME = "containerName";
@jakarta.annotation.Nullable
private String containerName;

public static final String JSON_PROPERTY_REVISION = "revision";
@jakarta.annotation.Nullable
private Integer revision;

public AttributesContainer() {
}

public AttributesContainer attributes(@jakarta.annotation.Nullable Map<String, ProfileAttribute> attributes) {
	this.attributes = attributes;
	return this;
}

public AttributesContainer putAttributesItem(String key, ProfileAttribute attributesItem) {
	if (this.attributes == null) {
	this.attributes = new HashMap<>();
	}
	this.attributes.put(key, attributesItem);
	return this;
}

/**
* The attributes stored by the container.
* @return attributes
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_ATTRIBUTES)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public Map<String, ProfileAttribute> getAttributes() {
	return attributes;
}


@JsonProperty(JSON_PROPERTY_ATTRIBUTES)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setAttributes(@jakarta.annotation.Nullable Map<String, ProfileAttribute> attributes) {
	this.attributes = attributes;
}


public AttributesContainer containerName(@jakarta.annotation.Nullable String containerName) {
	this.containerName = containerName;
	return this;
}

/**
* The name of the container.
* @return containerName
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_CONTAINER_NAME)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public String getContainerName() {
	return containerName;
}


@JsonProperty(JSON_PROPERTY_CONTAINER_NAME)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setContainerName(@jakarta.annotation.Nullable String containerName) {
	this.containerName = containerName;
}


public AttributesContainer revision(@jakarta.annotation.Nullable Integer revision) {
	this.revision = revision;
	return this;
}

/**
* The maximum revision number of any attribute within the container.
* @return revision
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_REVISION)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public Integer getRevision() {
	return revision;
}


@JsonProperty(JSON_PROPERTY_REVISION)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setRevision(@jakarta.annotation.Nullable Integer revision) {
	this.revision = revision;
}


/**
* Return true if this AttributesContainer object is equal to o.
*/
@Override
public boolean equals(Object o) {
	if (this == o) {
	return true;
	}
	if (o == null || getClass() != o.getClass()) {
	return false;
	}
	AttributesContainer attributesContainer = (AttributesContainer) o;
	return Objects.equals(this.attributes, attributesContainer.attributes) &&
		Objects.equals(this.containerName, attributesContainer.containerName) &&
		Objects.equals(this.revision, attributesContainer.revision);
}

@Override
public int hashCode() {
	return Objects.hash(attributes, containerName, revision);
}

@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("class AttributesContainer {\n");
	sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
	sb.append("    containerName: ").append(toIndentedString(containerName)).append("\n");
	sb.append("    revision: ").append(toIndentedString(revision)).append("\n");
	sb.append("}");
	return sb.toString();
}

/**
* Convert the given object to string with each line indented by 4 spaces
* (except the first line).
*/
private String toIndentedString(Object o) {
	if (o == null) {
	return "null";
	}
	return o.toString().replace("\n", "\n    ");
}

}
