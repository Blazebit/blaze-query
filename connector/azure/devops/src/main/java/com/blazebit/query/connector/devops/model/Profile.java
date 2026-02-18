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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * A user profile.
 */
@JsonPropertyOrder({
Profile.JSON_PROPERTY_APPLICATION_CONTAINER,
Profile.JSON_PROPERTY_CORE_ATTRIBUTES,
Profile.JSON_PROPERTY_CORE_REVISION,
Profile.JSON_PROPERTY_ID,
Profile.JSON_PROPERTY_PROFILE_STATE,
Profile.JSON_PROPERTY_REVISION,
Profile.JSON_PROPERTY_TIME_STAMP
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.10.0")
public class Profile {
public static final String JSON_PROPERTY_APPLICATION_CONTAINER = "applicationContainer";
@jakarta.annotation.Nullable
private AttributesContainer applicationContainer;

public static final String JSON_PROPERTY_CORE_ATTRIBUTES = "coreAttributes";
@jakarta.annotation.Nullable
private Map<String, CoreProfileAttribute> coreAttributes = new HashMap<>();

public static final String JSON_PROPERTY_CORE_REVISION = "coreRevision";
@jakarta.annotation.Nullable
private Integer coreRevision;

public static final String JSON_PROPERTY_ID = "id";
@jakarta.annotation.Nullable
private UUID id;

/**
* The current state of the profile.
*/
public enum ProfileStateEnum {
	CUSTOM(String.valueOf("custom")),

	CUSTOM_READ_ONLY(String.valueOf("customReadOnly")),

	READ_ONLY(String.valueOf("readOnly"));

	private String value;

	ProfileStateEnum(String value) {
	this.value = value;
	}

	@JsonValue
	public String getValue() {
	return value;
	}

	@Override
	public String toString() {
	return String.valueOf(value);
	}

	@JsonCreator
	public static ProfileStateEnum fromValue(String value) {
	for (ProfileStateEnum b : ProfileStateEnum.values()) {
		if (b.value.equals(value)) {
		return b;
		}
	}
	throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}

public static final String JSON_PROPERTY_PROFILE_STATE = "profileState";
@jakarta.annotation.Nullable
private ProfileStateEnum profileState;

public static final String JSON_PROPERTY_REVISION = "revision";
@jakarta.annotation.Nullable
private Integer revision;

public static final String JSON_PROPERTY_TIME_STAMP = "timeStamp";
@jakarta.annotation.Nullable
private OffsetDateTime timeStamp;

public Profile() {
}

public Profile applicationContainer(@jakarta.annotation.Nullable AttributesContainer applicationContainer) {
	this.applicationContainer = applicationContainer;
	return this;
}

/**
* Get applicationContainer
* @return applicationContainer
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_APPLICATION_CONTAINER)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public AttributesContainer getApplicationContainer() {
	return applicationContainer;
}


@JsonProperty(JSON_PROPERTY_APPLICATION_CONTAINER)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setApplicationContainer(@jakarta.annotation.Nullable AttributesContainer applicationContainer) {
	this.applicationContainer = applicationContainer;
}


public Profile coreAttributes(@jakarta.annotation.Nullable Map<String, CoreProfileAttribute> coreAttributes) {
	this.coreAttributes = coreAttributes;
	return this;
}

public Profile putCoreAttributesItem(String key, CoreProfileAttribute coreAttributesItem) {
	if (this.coreAttributes == null) {
	this.coreAttributes = new HashMap<>();
	}
	this.coreAttributes.put(key, coreAttributesItem);
	return this;
}

/**
* The core attributes of this profile.
* @return coreAttributes
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_CORE_ATTRIBUTES)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public Map<String, CoreProfileAttribute> getCoreAttributes() {
	return coreAttributes;
}


@JsonProperty(JSON_PROPERTY_CORE_ATTRIBUTES)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setCoreAttributes(@jakarta.annotation.Nullable Map<String, CoreProfileAttribute> coreAttributes) {
	this.coreAttributes = coreAttributes;
}


public Profile coreRevision(@jakarta.annotation.Nullable Integer coreRevision) {
	this.coreRevision = coreRevision;
	return this;
}

/**
* The maximum revision number of any attribute.
* @return coreRevision
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_CORE_REVISION)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public Integer getCoreRevision() {
	return coreRevision;
}


@JsonProperty(JSON_PROPERTY_CORE_REVISION)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setCoreRevision(@jakarta.annotation.Nullable Integer coreRevision) {
	this.coreRevision = coreRevision;
}


public Profile id(@jakarta.annotation.Nullable UUID id) {
	this.id = id;
	return this;
}

/**
* The unique identifier of the profile.
* @return id
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_ID)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public UUID getId() {
	return id;
}


@JsonProperty(JSON_PROPERTY_ID)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setId(@jakarta.annotation.Nullable UUID id) {
	this.id = id;
}


public Profile profileState(@jakarta.annotation.Nullable ProfileStateEnum profileState) {
	this.profileState = profileState;
	return this;
}

/**
* The current state of the profile.
* @return profileState
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_PROFILE_STATE)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public ProfileStateEnum getProfileState() {
	return profileState;
}


@JsonProperty(JSON_PROPERTY_PROFILE_STATE)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setProfileState(@jakarta.annotation.Nullable ProfileStateEnum profileState) {
	this.profileState = profileState;
}


public Profile revision(@jakarta.annotation.Nullable Integer revision) {
	this.revision = revision;
	return this;
}

/**
* The maximum revision number of any attribute.
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


public Profile timeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
	this.timeStamp = timeStamp;
	return this;
}

/**
* The time at which this profile was last changed.
* @return timeStamp
*/
@jakarta.annotation.Nullable
@JsonProperty(JSON_PROPERTY_TIME_STAMP)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

public OffsetDateTime getTimeStamp() {
	return timeStamp;
}


@JsonProperty(JSON_PROPERTY_TIME_STAMP)
@JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
public void setTimeStamp(@jakarta.annotation.Nullable OffsetDateTime timeStamp) {
	this.timeStamp = timeStamp;
}


/**
* Return true if this Profile object is equal to o.
*/
@Override
public boolean equals(Object o) {
	if (this == o) {
	return true;
	}
	if (o == null || getClass() != o.getClass()) {
	return false;
	}
	Profile profile = (Profile) o;
	return Objects.equals(this.applicationContainer, profile.applicationContainer) &&
		Objects.equals(this.coreAttributes, profile.coreAttributes) &&
		Objects.equals(this.coreRevision, profile.coreRevision) &&
		Objects.equals(this.id, profile.id) &&
		Objects.equals(this.profileState, profile.profileState) &&
		Objects.equals(this.revision, profile.revision) &&
		Objects.equals(this.timeStamp, profile.timeStamp);
}

@Override
public int hashCode() {
	return Objects.hash(applicationContainer, coreAttributes, coreRevision, id, profileState, revision, timeStamp);
}

@Override
public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("class Profile {\n");
	sb.append("    applicationContainer: ").append(toIndentedString(applicationContainer)).append("\n");
	sb.append("    coreAttributes: ").append(toIndentedString(coreAttributes)).append("\n");
	sb.append("    coreRevision: ").append(toIndentedString(coreRevision)).append("\n");
	sb.append("    id: ").append(toIndentedString(id)).append("\n");
	sb.append("    profileState: ").append(toIndentedString(profileState)).append("\n");
	sb.append("    revision: ").append(toIndentedString(revision)).append("\n");
	sb.append("    timeStamp: ").append(toIndentedString(timeStamp)).append("\n");
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
