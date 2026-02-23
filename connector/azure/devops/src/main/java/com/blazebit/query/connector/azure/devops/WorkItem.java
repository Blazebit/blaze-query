/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.devops;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * A typed wrapper around the generated {@code devops.model.WorkItem} that extracts
 * well-known Azure DevOps system fields from the raw {@code Map<String, Object>} fields map.
 *
 * @author Martijn Sprengers
 * @since 1.0.8
 */
public record WorkItem(
		Integer id,
		Integer rev,
		String url,
		String title,
		String state,
		String workItemType,
		String teamProject,
		String areaPath,
		String iterationPath,
		String reason,
		String assignedTo,
		String createdBy,
		String changedBy,
		OffsetDateTime createdDate,
		OffsetDateTime changedDate,
		Integer priority
) {

	public WorkItem(com.blazebit.query.connector.devops.model.WorkItem w) {
		this(
				w.getId(),
				w.getRev(),
				w.getUrl(),
				stringField( w.getFields(), "System.Title" ),
				stringField( w.getFields(), "System.State" ),
				stringField( w.getFields(), "System.WorkItemType" ),
				stringField( w.getFields(), "System.TeamProject" ),
				stringField( w.getFields(), "System.AreaPath" ),
				stringField( w.getFields(), "System.IterationPath" ),
				stringField( w.getFields(), "System.Reason" ),
				displayName( w.getFields(), "System.AssignedTo" ),
				displayName( w.getFields(), "System.CreatedBy" ),
				displayName( w.getFields(), "System.ChangedBy" ),
				offsetDateTimeField( w.getFields(), "System.CreatedDate" ),
				offsetDateTimeField( w.getFields(), "System.ChangedDate" ),
				intField( w.getFields(), "Microsoft.VSTS.Common.Priority" )
		);
	}

	private static String stringField(Map<String, Object> fields, String key) {
		if ( fields == null ) {
			return null;
		}
		Object value = fields.get( key );
		return value instanceof String ? (String) value : null;
	}

	@SuppressWarnings("unchecked")
	private static String displayName(Map<String, Object> fields, String key) {
		if ( fields == null ) {
			return null;
		}
		Object value = fields.get( key );
		if ( value instanceof String ) {
			return (String) value;
		}
		if ( value instanceof Map ) {
			Object name = ( (Map<String, Object>) value ).get( "displayName" );
			return name instanceof String ? (String) name : null;
		}
		return null;
	}

	private static OffsetDateTime offsetDateTimeField(Map<String, Object> fields, String key) {
		if ( fields == null ) {
			return null;
		}
		Object value = fields.get( key );
		if ( value instanceof String ) {
			String s = (String) value;
			return s.isEmpty() ? null : OffsetDateTime.parse( s );
		}
		return null;
	}

	private static Integer intField(Map<String, Object> fields, String key) {
		if ( fields == null ) {
			return null;
		}
		Object value = fields.get( key );
		if ( value instanceof Number ) {
			return ( (Number) value ).intValue();
		}
		return null;
	}
}
